package org.haw.bs.praktikum4;

/*
 * OperatingSystem.java
 */
import java.util.*;

/**
 * Basisfunktionen eines 32-Bit Betriebssystems System Calls: createProcess,
 * killAll, write, read
 * 
 */
public class OperatingSystem {
	// ------------ Vordefinierte Prozess-Parameter -----------------------
	/**
	 * max. Anzahl Seiten pro Prozess im Hauptspeicher (sonst Verdrängung
	 * eigener Seiten):
	 */
	private int MAX_RAM_PAGES_PER_PROCESS = 10;

	/**
	 * max. Anzahl Prozesse (muss beschränkt werden, da kein Swapping
	 * implementiert ist!): (ein Teil des Hauptspeichers muss immer frei bleiben
	 * (u.a. für Caching etc.), daher -PAGE_SIZE)
	 */
	private int MAX_NUM_OF_PROCESSES = (RAM_SIZE - PAGE_SIZE) / (MAX_RAM_PAGES_PER_PROCESS * PAGE_SIZE);

	/**
	 * Dieser Faktor bestimmt das "Lokalitätsverhalten" eines Programms (=
	 * Anzahl Operationen innerhalb eines Seitenbereichs)
	 */
	private int DEFAULT_LOCALITY_FACTOR = 30;

	// ------------ Konfigurierbare maschinenabhängige Parameter
	// ---------------------------------------------------------

	/**
	 * Länge eines Datenworts in Byte (default: 4 Byte = 32 Bit)
	 */
	private static final int WORD_SIZE = 4;

	/**
	 * Größe des RAM in Byte (default: 2^16 Byte = 64 KB RAM)
	 */
	private static final int RAM_SIZE = 65536;

	/**
	 * Seitengröße (default: 2^8 Byte = 256 Byte Seitengröße --> max. 2^8 = 256
	 * Seitenrahmen , 64 Worte pro Seitenrahmen)
	 */
	private static final int PAGE_SIZE = 256;

	/**
	 * Virtueller Adressraum (default: 2^20 Byte = 1 MB)
	 */
	private static final int VIRT_ADR_SPACE = 1048576;

	// ------------ Abgeleitete maschinenabhängige Parameter
	// ------------------------------------------------------

	/**
	 * Max. Anzahl virtueller Seiten: VIRT_ADR_SPACE / PAGE_SIZE (default: 2^12
	 * = 4096 Seiten)
	 */
	private static final int MAX_NO_OF_PAGES = VIRT_ADR_SPACE / PAGE_SIZE;

	/**
	 * Platteneigenschaften: Plattengröße = virt. Adressraum reicht hier, weil
	 * wir keine weiteren Dateien brauchen
	 */
	private static final int DISK_SIZE = VIRT_ADR_SPACE;

	/**
	 * Größe eines Plattenblocks = PAGE_SIZE
	 */
	private static final int BLOCK_SIZE = PAGE_SIZE;

	// ------------ Hardware-Stubs --------------------------------------

	/**
	 * Physikalischer Hauptspeicher (Adresse, Datenwort)
	 */
	private Hashtable<Integer, Integer> physRAM;

	/**
	 * Physikalische Festplatte (Adresse, Datenwort)
	 */
	private Hashtable<Integer, Integer> physDisk;

	// ---------- Systemtabellen ----------------------------------------

	/**
	 * Freibereichsliste Hauptspeicher
	 */
	private LinkedList<FreeListBlock> ramFreeList;

	/**
	 * Freibereichsliste Festplatte
	 */
	private LinkedList<FreeListBlock> diskFreeList;

	/**
	 * Liste aller Prozesse
	 */
	private LinkedList<Process> processTable;
	/**
	 * Anzahl erzeugter Prozesse (fuer naechste freie PID)
	 */
	private int processCounter;

	/**
	 * Zeiger auf Statistik-Objekt
	 */
	public Statistics eventLog; // Protokollierung und statistische Auswertung

	// -------------------------- Teststeuerung -----------------------------
	/**
	 * Testausgaben erwünscht?
	 */
	private boolean testMode = false;

	// -------------------------- Seitenersetzungs-Algorithmus --------------

	/**
	 * Implementierte Seitenersetzungsalgorithmen
	 */
	public enum ImplementedReplacementAlgorithms {
		CLOCK, FIFO, RANDOM
	}

	/**
	 * Auswahl des Seitenersetzungs-Algorithmus
	 */
	private ImplementedReplacementAlgorithms replacementAlgorithm = ImplementedReplacementAlgorithms.CLOCK;

	// ------------------------- Public-Methoden ---------------------------
	/**
	 * Konstruktor Operating System
	 */
	public OperatingSystem() {
		// RAM initialisieren (Zugriffe erfolgen wortweise!)
		physRAM = new Hashtable<Integer, Integer>(RAM_SIZE / WORD_SIZE);
		// RAM - Freibereichsliste initialisieren
		ramFreeList = new LinkedList<FreeListBlock>();
		FreeListBlock ramFB = new FreeListBlock(0, RAM_SIZE);
		ramFreeList.add(ramFB);

		// Platte initialisieren (Zugriffe erfolgen blockweise!))
		physDisk = new Hashtable<Integer, Integer>(DISK_SIZE / BLOCK_SIZE);
		// Platten - Freibereichsliste initialisieren
		diskFreeList = new LinkedList<FreeListBlock>();
		FreeListBlock diskFB = new FreeListBlock(0, DISK_SIZE);
		diskFreeList.add(diskFB);

		// Prozessliste initialisieren
		processTable = new LinkedList<Process>();
		processCounter = 0;

		// Statistische Protokollierung aktivieren
		eventLog = new Statistics();
	}

	/**
	 * Prozess-Objekt (Thread) erzeugen und in Prozessliste eintragen
	 * 
	 * @param die
	 *            Größe des Prozess-Hauptspeicherbedarfs in Byte
	 * 
	 * @return die neue Prozess-ID oder -1, wenn Erzeugung nicht möglich
	 *         (Speichermangel)
	 */
	public synchronized int createProcess(int processSize) {
		if (processTable.size() < MAX_NUM_OF_PROCESSES) {
			// RAM-Platz für neuen Prozess vorhanden
			Process proc = new Process(this, processCounter, processSize);
			processTable.add(proc);
			System.out.println("Prozess " + proc.pid + " wurde erzeugt!");
			// Prozess in den Hauptspeicher "laden"
			loadProcess(processCounter, processSize);
			// Prozess als JAVA-Thread starten
			proc.start();
			processCounter++; // Neue Prozess-IDs werden hochgezählt
			return proc.pid;
		} else {
			// RAM voll
			return -1;
		}
	}

	private void loadProcess(int pid, int processSize) {
		// Laden des Programmtextes und initialisieren der Datenbereiche
		// Speicherbelegung durch write - Operationen
		int item; // Dummy

		for (int virtAdr = 0; virtAdr < processSize; virtAdr = virtAdr
				+ getWORD_SIZE()) {
			// Zu schreibendes Datenwort bestimmen
			item = (int) (Math.pow(2, 31) * Math.random());
			// System Call
			write(pid, virtAdr, item);
		}
		System.out.println("Prozess " + pid + ": " + processSize + " Byte ("
				+ processSize / getPAGE_SIZE()
				+ " Seiten) in den Speicher geladen!");
		// Statistikzähler neu initialisieren
		eventLog.resetCounter();
	}

	/**
	 * Alle aktiven Prozesse aus Prozessliste beenden
	 */
	public synchronized void killAll() {
		for (int i = 0; i < processTable.size(); i++) {
			Process proc = (Process)processTable.get(i);
			System.out.println("Prozess " + proc.pid + " wird unterbrochen!");
			proc.interrupt();
		}
	}

	/**
	 * Datenwort item auf eine virtuelle Adresse virtAdr im virtuellen Speicher
	 * schreiben
	 * 
	 * @param pid
	 *            Prozess-ID
	 * @param virtAdr
	 *            virtuelle Adresse
	 * @param item
	 *            Datenwort
	 * @return 0 wenn Schreiboperation erfolgreich oder -1 bei fehlerhafter
	 *         Adresse
	 */
	public synchronized int write(int pid, int virtAdr, int item) {
		// übergebene Adresse prüfen
		if ((virtAdr < 0) || (virtAdr > VIRT_ADR_SPACE - WORD_SIZE)) {
			System.err.println("OS: write ERROR " + pid + ": Adresse " + virtAdr + " liegt außerhalb des virtuellen Adressraums 0 - " + VIRT_ADR_SPACE);
			return -1;
		}
		
		// Seitenadresse berechnen
		int virtualPageNum = getVirtualPageNum(virtAdr);
		int offset = getOffset(virtAdr);
		
		testOut("OS: write " + pid + " " + virtAdr + " " + item + " +++ Seitennr.: " + virtualPageNum + " Offset: " + offset);

		// Seite in Seitentabelle referenzieren
		Process proc = getProcess(pid);
		PageTableEntry pte = proc.pageTable.getPte(virtualPageNum);
		if (pte == null) {
			// Seite nicht vorhanden:
			testOut("OS: write " + pid + " +++ Seitennr.: " + virtualPageNum + " in Seitentabelle nicht vorhanden");
			
			pte = new PageTableEntry();
			pte.virtPageNum = virtualPageNum;
			
			// Seitenrahmen im RAM für die neue Seite anfordern und reale (RAM-)SeitenAdresse eintragen
			pte.realPageFrameAdr = getNewRAMPage(pte, pid);
			pte.valid = true;
			
			// neue Seite in Seitentabelle eintragen
			proc.pageTable.addEntry(pte);
			
			testOut("OS: write " + pid + " Neue Seite " + virtualPageNum + " in Seitentabelle eingetragen! RAM-Adr.: " + pte.realPageFrameAdr);
		} else {
			// Seite vorhanden: Seite valid (also im RAM)?
			if (!pte.valid) {
				// Seite nicht valid (also auf Platte --> Seitenfehler):
				pte = handlePageFault(pte, pid);
			}
		}
		
		// ------ Zustand: Seite ist in Seitentabelle und im RAM vorhanden

		// Reale Adresse des Datenworts berechnen
		int realAddressOfItem = pte.realPageFrameAdr + offset;
		
		// Datenwort in RAM eintragen
		writeToRAM(realAddressOfItem, item);
		
		testOut("OS: write " + pid + " +++ item: " + item + " erfolgreich an virt. Adresse " + virtAdr + " geschrieben! RAM-Adresse: " + realAddressOfItem + " \n");
		
		// Seitentabelle bzgl. Zugriffshistorie aktualisieren
		pte.referenced = true;
		
		// Statistische Zählung
		eventLog.incrementWriteAccesses();
		
		return 0;
	}

	/**
	 * Datenwort von einer Adresse im virtuellen Speicher lesen
	 * 
	 * @param pid
	 *            Prozess-ID
	 * @param virtAdr
	 *            virtuelle Adresse
	 * @return Datenwort auf logischer Adresse virtAdr oder -1 bei
	 *         Zugriffsfehler
	 */
	public synchronized int read(int pid, int virtAdr) {
		// übergebene Adresse prüfen
		if ((virtAdr < 0) || (virtAdr > VIRT_ADR_SPACE - WORD_SIZE)) {
			System.err.println("OS: write ERROR " + pid + ": Adresse " + virtAdr + " liegt außerhalb des virtuellen Adressraums 0 - " + VIRT_ADR_SPACE);
			return -1;
		}
		
		// Seitenadresse berechnen
		int virtualPageNum = getVirtualPageNum(virtAdr);
		int offset = getOffset(virtAdr);
		
		testOut("OS: read " + pid + " " + virtAdr + " +++ Seitennr.: " + virtualPageNum + " Offset: " + offset);
		
		// Seite in Seitentabelle referenzieren
		Process proc = getProcess(pid);
		PageTableEntry pte = proc.pageTable.getPte(virtualPageNum);
		if (pte == null) {
			// Seite nicht vorhanden
			return -1;
		} else {
			// Seite vorhanden: Seite valid (also im RAM)?
			if (!pte.valid) {
				// Seite nicht valid (also auf Platte --> Seitenfehler):
				pte = handlePageFault(pte, pid);
			}
		}
		
		// ------ Zustand: Seite ist in Seitentabelle und im RAM vorhanden
		
		// Reale Adresse des Datenworts berechnen
		int realAddressOfItem = pte.realPageFrameAdr + offset;
		
		// Datenwort aus RAM lesen
		int item = readFromRAM(realAddressOfItem);
		
		testOut("OS: read " + pid + " +++ item: " + item + " erfolgreich aus virt. Adresse " + virtAdr + " gelesen! RAM-Adresse: " + realAddressOfItem + " \n");
		
		// Seitentabelle bzgl. Zugriffshistorie aktualisieren
		pte.referenced = true;
		
		// Statistische Zählung
		eventLog.incrementWriteAccesses();
		
		return item;
	}

	// --------------- Private Methoden des Betriebssystems
	// ---------------------------------

	/**
	 * @param pid
	 * @return Prozess-Objekt für die Prozess-ID
	 */
	private Process getProcess(int pid) {
		return processTable.get(pid);
	}

	/**
	 * @param virtAdr
	 *            : eine virtuelle Adresse
	 * @return Die entsprechende virtuelle Seitennummer
	 */
	private int getVirtualPageNum(int virtAdr) {
		return virtAdr / PAGE_SIZE;
	}

	/**
	 * @param virtAdr
	 *            : eine virtuelle Adresse
	 * @return Den entsprechenden Offset zur Berechnung der realen Adresse
	 */
	private int getOffset(int virtAdr) {
		return virtAdr % PAGE_SIZE;
	}

	/**
	 * Behandlung eines Seitenfehlers für die durch den pte beschriebene Seite
	 * 
	 * @param pte
	 *            Seitentabelleneintrag
	 * @param pid
	 *            Prozess-Id
	 * @return modifizierter Seitentabelleneintrag
	 */
	private PageTableEntry handlePageFault(PageTableEntry pte, int pid) {
		testOut("OS: " + pid + " +++ Seitenfehler für Seite " + pte.virtPageNum);
		eventLog.incrementPageFaults(); // Statistische Zählung
		
		// neue Seite im RAM anfordern (ggf. alte Seite verdrängen!)
		int newPageFrameAdr = getNewRAMPage(pte, pid); // Reale Adresse einer neuen Seite im RAM
		
		// Seite von Platte in neue RAM-Seite lesen (realPageAdr muss Plattenblockadresse gewesen sein!)
		dataTransferFromDisk(pte.realPageFrameAdr, newPageFrameAdr);
		
		// Plattenblock freigeben
		freeDiskBlock(pte.realPageFrameAdr);
		
		// Seitentabelle aktualisieren
		pte.realPageFrameAdr = newPageFrameAdr;
		pte.valid = true;
		
		testOut("OS: " + pid + " +++ Seite " + pte.virtPageNum + " ist wieder im RAM mit Startadresse " + pte.realPageFrameAdr);

		return pte;
	}

	/**
	 * Leere RAM-Seite zur Verfügung stellen (ggf. alte Seite auslagern)
	 * 
	 * @param pid
	 *            Prozess-Id
	 * @return Reale RAM-Adresse einer neuen und freien Seite
	 */
	private int getNewRAMPage(PageTableEntry newPte, int pid) {
		// Algorithmus:
		// Anforderung einer neuen RAM-Seite für die gegebene newPte erfüllbar?
		// (< MAX_RAM_PAGES_PER_PROCESS)
		// Ja, Seitenanforderung im RAM ist erfüllbar:
		// neue Seite belegen und Adresse zurückgeben
		// Nein, Seitenanforderung im RAM ist nicht erfüllbar:
		// eine alte Seite zur Verdrängung auswählen -->
		// Seitenersetzungs-Algorithmus
		// alte Seite auf Platte auslagern (neuen Diskblock anfordern)
		// im RAM löschen (mit Nullen überschreiben)
		// Adresse als neue Seite zurückgeben
		// ----------- Start ----------------
		int newPageFrameAdr = 0; // Reale Adresse einer neuen Seite im RAM

		Process proc = getProcess(pid);
		// Anforderung einer neuen RAM-Seite erfüllbar?
		if (proc.pageTable.getSize() < MAX_RAM_PAGES_PER_PROCESS) {
			// Ja, Seitenanforderung im RAM ist erfüllbar: neue Seite belegen und Adresse zurückgeben
			newPageFrameAdr = allocateRAMPage();
			
			// Liste der RAM-Seiten für den Prozess erweitern
			proc.pageTable.pteRAMlistInsert(newPte);
		} else {
			// Nein, Seitenanforderung im RAM ist nicht erfüllbar:
			testOut("OS: getNewRAMPage " + pid + " ++ Seitenfehler für Seite " + newPte.virtPageNum + " --> Seitenersetzungs-Algorithmus!");
			
			// eine alte Seite zur Verdrängung auswählen --> Seitenersetzungs-Algorithmus
			PageTableEntry replacePte = proc.pageTable.selectNextRAMpteAndReplace(newPte); // Eintrag für eine ggf. zu ersetzende Seite
			int replacePageFrameAdr = replacePte.realPageFrameAdr; // Reale Adresse einer zu ersetzenden Seite
			
			// alte Seite auf Platte auslagern (vorher neuen Diskblock  anfordern)
			// hier: IMMER zurückschreiben, weil keine Kopie auf der Platte
			// bleibt (M-Bit wird also nicht benutzt!)
			int newDiskBlock = allocateDiskBlock(); // Reale Adresse eines neuen Plattenblocks
			dataTransferToDisk(replacePageFrameAdr, newDiskBlock);
			
			// Plattenadresse in Seitentabelle eintragen
			replacePte.realPageFrameAdr = newDiskBlock;
			replacePte.valid = false;

			testOut("OS: getNewRAMPage " + pid + " ++ Seite " + replacePte.virtPageNum + " ist nun auf der Platte an Adresse " + replacePte.realPageFrameAdr);
			
			// Adresse als neue Seite zurückgeben
			newPageFrameAdr = replacePageFrameAdr;
		}
		return newPageFrameAdr;
	}

	/**
	 * Schreibe das item an der realen Adresse ramAdr in den RAM
	 * 
	 * @param ramAdr
	 * @param item
	 */
	private void writeToRAM(int ramAdr, int item) {
		physRAM.put(new Integer(ramAdr), new Integer(item));
	}

	/**
	 * Lies das item an der realen Adresse ramAdr aus dem RAM
	 * 
	 * @param ramAdr
	 * @return das item als positive Integerzahl oder -1, falls Adresse nicht
	 *         belegt
	 */
	private int readFromRAM(int ramAdr) {
		Integer itemObject = (Integer)physRAM.get(new Integer(ramAdr));
		return itemObject == null ? -1 : itemObject.intValue();
	}

	/**
	 * Schreibe die Seite an der realen RAM-Adresse ramAdr auf die Platte unter
	 * der Adresse diskAdr
	 * 
	 * @param ramAdr
	 * @param diskAdr
	 */
	private void dataTransferToDisk(int ramAdr, int diskAdr) {
		int di = diskAdr; // aktuelle Speicherwortadresse auf der Platte
		int ri; // aktuelle Speicherwortadresse im RAM
		for (ri = ramAdr; ri < ramAdr + PAGE_SIZE; ri = ri + WORD_SIZE) {
			Integer currentWord = (Integer) physRAM.get(new Integer(ri)); // aktuelles Speicherwort
			physDisk.put(new Integer(di), currentWord);
			di = di + WORD_SIZE;
		}
	}

	/**
	 * Schreibe den Plattenblock an der realen Plattenadresse diskAdr in den RAM
	 * unter der Adresse ramAdr
	 * 
	 * @param diskAdr
	 * @param ramAdr
	 */
	private void dataTransferFromDisk(int diskAdr, int ramAdr) {
		int ri = ramAdr; // aktuelle Speicherwortadresse im RAM
		int di; // aktuelle Speicherwortadresse auf der Platte
		for (di = diskAdr; di < diskAdr + BLOCK_SIZE; di = di + WORD_SIZE) {
			Integer currentWord = (Integer)physDisk.get(new Integer(di)); // aktuelles Speicherwort
			physRAM.put(new Integer(ri), currentWord);
			ri = ri + WORD_SIZE;
		}
	}

	/**
	 * Liefere eine freie RAM-Seite (Seitenrahmen) und lösche sie aus der
	 * RAM-Freibereichsliste
	 * 
	 * @return reale Adresse einer freien RAM-Seite
	 */
	private int allocateRAMPage() {
		// Algorithmus:
		// 1. Block der Freibereichsliste um PAGE_SIZE verkleinern.
		// Falls size = 0 --> löschen!
		
		FreeListBlock ramFB = ramFreeList.getFirst(); // Erster Block aus Freibereichsliste
		int freePageAdr = ramFB.getAdress(); // Rückgabeadresse
		
		// Block in Freibereichsliste aktualisieren
		if (ramFB.getSize() == PAGE_SIZE) {
			// Block wäre anschließend leer --> Löschen
			ramFreeList.removeFirst();
		} else {
			ramFB.setAdress(freePageAdr + PAGE_SIZE);
			ramFB.setSize(ramFB.getSize() - PAGE_SIZE);
		}
		
		testOut("OS: Neuer Seitenrahmen (RAM page) belegt, Adresse: " + freePageAdr);
		testOut("OS: ramFreeList:" + ramFreeList);
		
		return freePageAdr;
	}

	/**
	 * Lösche eine RAM-Seite und trage sie in die RAM-Freibereichsliste ein.
	 * Wird hier nicht verwendet, da ein Prozess keinen Seitenrahmen wieder
	 * zurückgeben muss (keine dynamische Seitenzuteilung).
	 * 
	 * @param ramAdr
	 */
	private void freeRAMPage(int ramAdr) {
		// Algorithmus:
		// RAM-Seite mit Nullen überschreiben (Security!) und neuen
		// Freibereichsblock erzeugen
		// (Eine Zusammenfassung von Freibereichsblöcken (Bereinigen der
		// Fragmentierung) müsste
		// zusätzlich implementiert werden!)
		
		Integer nullWord = new Integer(0); // Null-Speicherwort
		
		// RAM-Seite überschreiben
		int ri; // aktuelle Speicherwortadresse im RAM
		for (ri = ramAdr; ri < ramAdr + PAGE_SIZE; ri = ri + WORD_SIZE) {
			physRAM.put(new Integer(ri), nullWord);
		}
		
		// In Freibereichsliste eintragen
		FreeListBlock ramFB = new FreeListBlock(ramAdr, PAGE_SIZE); // neuer FreeListBlock
		ramFreeList.add(ramFB);
		Collections.sort(ramFreeList);
		
		testOut("OS: Seitenrahmen (RAM page) wurde freigegeben, Adresse " + ramAdr);
		testOut("OS: ramFreeList:" + ramFreeList);
	}

	/**
	 * Liefere einen freien Plattenblock und lösche ihn aus der
	 * Platten-Freibereichsliste
	 * 
	 * @return reale Adresse eines freien Plattenblocks oder -1, wenn die Platte
	 *         voll ist
	 */
	private int allocateDiskBlock() {
		// Algorithmus:
		// 1. Block der Freibereichsliste um BLOCK_SIZE verkleinern. Falls size = 0 --> löschen!
		FreeListBlock diskFB = (FreeListBlock) diskFreeList.getFirst(); // Erster Block aus Freibereichsliste
		if ((diskFreeList.size() == 1) && (diskFB.getSize() == BLOCK_SIZE)) {
			// Nur noch ein freier Block vorhanden --> Platte voll!
			testOut("OS: allocateDiskBlock: Platte ist voll! --------------------------------------- ");
			return -1;
		} else {
			int freeBlockAdr = diskFB.getAdress();
			// Block in Freibereichsliste aktualisieren
			if (diskFB.getSize() == BLOCK_SIZE) {
				// Block wäre anschließend leer --> Löschen
				diskFreeList.removeFirst();
			} else {
				diskFB.setAdress(freeBlockAdr + BLOCK_SIZE);
				diskFB.setSize(diskFB.getSize() - BLOCK_SIZE);
			}
			testOut("OS: neuer Plattenblock " + freeBlockAdr + " wurde belegt!");
			return freeBlockAdr;
		}
	}

	/**
	 * Lösche einen Plattenblock und trage ihn in die Platten-Freibereichsliste
	 * ein
	 * 
	 * @param diskAdr
	 */
	private void freeDiskBlock(int diskAdr) {
		// Algorithmus:
		// Plattenblock mit Nullen überschreiben (Security!) und neuen
		// Freibereichsblock erzeugen
		// (Eine Zusammenfassung von Freibereichsblöcken (Bereinigen der
		// Fragmentierung) müsste zusätzlich implementiert werden!)
		
		Integer nullWord = new Integer(0); // Null-Speicherwort

		// Plattenblock überschreiben
		int di; // aktuelle Speicherwortadresse auf der Platte
		for (di = diskAdr; di < diskAdr + BLOCK_SIZE; di = di + WORD_SIZE) {
			physDisk.put(new Integer(di), nullWord);
		}
		
		// In Freibereichsliste eintragen
		FreeListBlock diskFB = new FreeListBlock(diskAdr, BLOCK_SIZE); // neuer FreeListBlock
		diskFreeList.add(diskFB);
		Collections.sort(diskFreeList);
		
		testOut("OS: Plattenblock " + diskAdr + " wurde freigegeben!");
	}

	// ------------------------- getter-Methoden für Konstanten
	// -------------------------------

	/**
	 * @return Die max. Anzahl Seiten pro Prozess im Hauptspeicher (sonst
	 *         Verdrängung eigener Seiten).
	 */
	public synchronized int getMAX_RAM_PAGES_PER_PROCESS() {
		return MAX_RAM_PAGES_PER_PROCESS;
	}

	/**
	 * @param i
	 *            max. Anzahl Seiten pro Prozess im Hauptspeicher (sonst
	 *            Verdrängung eigener Seiten)
	 */
	public synchronized void setMAX_RAM_PAGES_PER_PROCESS(int i) {
		i = Math.max(1, i);
		i = Math.min(i, MAX_NO_OF_PAGES);
		MAX_RAM_PAGES_PER_PROCESS = i;
		MAX_NUM_OF_PROCESSES = (RAM_SIZE - PAGE_SIZE) / (MAX_RAM_PAGES_PER_PROCESS * PAGE_SIZE);
		testOut("OS: MAX_RAM_PAGES_PER_PROCESS: " + MAX_RAM_PAGES_PER_PROCESS + " MAX_NUM_OF_PROCESSES:" + MAX_NUM_OF_PROCESSES);
	}

	/**
	 * @return Die max. Anzahl an Prozessen
	 */
	public synchronized int getMAX_NUM_OF_PROCESSES() {
		return MAX_NUM_OF_PROCESSES;
	}

	/**
	 * @return Anzahl Operationen innerhalb eines Seitenbereichs
	 */
	public synchronized int getDEFAULT_LOCALITY_FACTOR() {
		return DEFAULT_LOCALITY_FACTOR;
	}

	/**
	 * @param i
	 *            Anzahl Operationen innerhalb eines Seitenbereichs
	 */
	public synchronized void setDEFAULT_LOCALITY_FACTOR(int i) {
		i = Math.max(1, i);
		DEFAULT_LOCALITY_FACTOR = i;
	}

	/**
	 * @return Die Länge eines Datenworts (in Byte)
	 */
	public synchronized int getWORD_SIZE() {
		return WORD_SIZE;
	}

	/**
	 * @return Die Größe einer Seite (in Byte)
	 */
	public synchronized int getPAGE_SIZE() {
		return PAGE_SIZE;
	}

	/**
	 * @return Die Größe des Hauptspeichers (in Byte)
	 */
	public synchronized int getRAM_SIZE() {
		return RAM_SIZE;
	}

	/**
	 * @return Die Größe des virtuellen Adressraums (in Byte)
	 */
	public synchronized int getVIRT_ADR_SPACE() {
		return VIRT_ADR_SPACE;
	}

	/**
	 * @return Die max. Anzahl an Seiten
	 * 
	 */
	public synchronized int getMAX_NO_OF_PAGES() {
		return MAX_NO_OF_PAGES;
	}

	/**
	 * @return Die Größe der Festplatte (in Byte)
	 */
	public synchronized int getDISK_SIZE() {
		return DISK_SIZE;
	}

	/**
	 * @return replacement_algorithm als Enum-Wert
	 *         ImplementedReplacementAlgorithms
	 */
	public synchronized ImplementedReplacementAlgorithms getReplacementAlgorithm() {
		return replacementAlgorithm;
	}

	/**
	 * @param alg
	 *            Enum-Wert ImplementedReplacementAlgorithms
	 */
	public synchronized void setREPLACEMENT_ALGORITHM(ImplementedReplacementAlgorithms alg) {
		replacementAlgorithm = alg;
	}

	/**
	 * @return Testausgaben erwünscht?
	 */
	public boolean isTestMode() {
		return testMode;
	}

	/**
	 * @param testMode
	 *            - Testausgaben erwünscht?
	 */
	public void setTestMode(boolean testMode) {
		this.testMode = testMode;
	}

	// ------------------ Steuerung der Testausgaben
	// -----------------------------
	/**
	 * @param ausgabe
	 *            String ausgeben, falls im TEST-Modus
	 */
	public synchronized void testOut(String ausgabe) {
		if (testMode) {
			System.err.println(ausgabe);
		}
	}
}
