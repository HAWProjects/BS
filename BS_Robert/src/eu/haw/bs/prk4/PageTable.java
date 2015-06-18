package eu.haw.bs.prk4;

import java.util.*;

/**
 * PageTable.java
 * 
 * Eine Seitentabelle eines Prozesses, implementiert als ArrayList von
 * PageTableEntry-Elementen (pte)
 * 
 */
public class PageTable {
	/**
	 * Die Seitentabelle als ArrayList von Seitentabelleneintr�gen
	 * (PageTableEntry). Die Seitentabelle darf nicht sortiert werden!
	 */
	private ArrayList<PageTableEntry> pageTable;

	/**
	 * Liste aller Seiten, die sich im RAM befinden
	 */
	private LinkedList<PageTableEntry> pteRAMlist;

	/**
	 * Uhrzeiger f�r Clock-Algorithmus
	 */
	private int pteRAMlistIndex;

	/**
	 * Zeiger auf das Betriebssystem-Objekt
	 */
	private OperatingSystem os;

	/**
	 * Prozess-ID des eigenen Prozesses
	 */
	private int pid;

	/**
	 * Konstruktor
	 */
	public PageTable(OperatingSystem currentOS, int myPID) {
		os = currentOS;
		pid = myPID;
		// Die Seitentabelle erzeugen
		pageTable = new ArrayList<PageTableEntry>();
		// Die Liste auf RAM-Seiteneintr�ge erzeugen
		pteRAMlist = new LinkedList<PageTableEntry>();
		pteRAMlistIndex = 0;
	}

	/**
	 * R�ckgabe: Seitentabelleneintrag pte (PageTableEntry) f�r die �bergebene
	 * virtuelle Seitennummer (VPN = Virtual Page Number) oder null, falls Seite
	 * nicht existiert
	 */
	public PageTableEntry getPte(int vpn) {
		if ((vpn < 0) || (vpn >= pageTable.size())) {
			// R�ckgabe null, da Seite nicht existiert!
			return null;
		} else {
			return pageTable.get(vpn);
		}
	}

	/**
	 * Einen Eintrag (PageTableEntry) an die Seitentabelle anh�ngen. Die
	 * Seitentabelle darf nicht sortiert werden!
	 */
	public void addEntry(PageTableEntry pte) {
		pageTable.add(pte);
	}

	/**
	 * R�ckgabe: Aktuelle Gr��e der Seitentabelle.
	 */
	public int getSize() {
		return pageTable.size();
	}

	/**
	 * Pte in pteRAMlist eintragen, wenn sich die Zahl der RAM-Seiten des
	 * Prozesses erh�ht hat.
	 */
	public void pteRAMlistInsert(PageTableEntry pte) {
		pteRAMlist.add(pte);
	}

	/**
	 * Eine Seite, die sich im RAM befindet, anhand der pteRAMlist ausw�hlen und
	 * zur�ckgeben
	 */
	public PageTableEntry selectNextRAMpteAndReplace(PageTableEntry newPte) {
		if (os.getReplacementAlgorithm() == OperatingSystem.ImplementedReplacementAlgorithms.CLOCK) {
			return clockAlgorithm(newPte);
		} else {
			if (os.getReplacementAlgorithm() == OperatingSystem.ImplementedReplacementAlgorithms.FIFO) {
				return fifoAlgorithm(newPte);
			} else {
				return randomAlgorithm(newPte);
			}
		}
	}

	/**
	 * FIFO-Algorithmus: Auswahl = Listenkopf (1. Element) Anschlie�end
	 * Listenkopf l�schen, neue Seite (newPte) an Liste anh�ngen
	 */
	private PageTableEntry fifoAlgorithm(PageTableEntry newPte) {
		PageTableEntry pte; // Auswahl

		pte = (PageTableEntry) pteRAMlist.getFirst();
		os.testOut("Prozess " + pid + ": FIFO-Algorithmus hat pte ausgew�hlt: "
				+ pte.virtPageNum);
		pteRAMlist.removeFirst();
		pteRAMlist.add(newPte);
		return pte;
	}

	/**
	 * CLOCK-Algorithmus (Second-Chance): N�chstes Listenelement, ausgehend vom
	 * aktuellen Index, mit Referenced-Bit = 0 (false) ausw�hlen Sonst R-Bit auf
	 * 0 setzen und n�chstes Element in der pteRAMlist untersuchen. Anschlie�end
	 * die ausgew�hlte Seite durch die neue Seite (newPte) am selben Listenplatz
	 * ersetzen
	 */
	private PageTableEntry clockAlgorithm(PageTableEntry newPte) {
		PageTableEntry pte; // Aktuell untersuchter Seitentabelleneintrag

		// Immer ab altem "Uhrzeigerstand" weitersuchen
		pte = (PageTableEntry) pteRAMlist.get(pteRAMlistIndex);

		// Suche den n�chsten Eintrag mit referenced == false (R-Bit = 0)
		while (pte.referenced == true) {
			// Seite wurde referenziert, also nicht ausw�hlen, sondern R-Bit
			// zur�cksetzen
			os.testOut("Prozess " + pid + ": CLOCK-Algorithmus! --- pte.vpn: "
					+ pte.virtPageNum + " ref: " + pte.referenced);
			pte.referenced = false;
			incrementPteRAMlistIndex();
			pte = (PageTableEntry) pteRAMlist.get(pteRAMlistIndex);
		}

		// Seite ausgew�hlt! (--> pteRAMlistIndex)
		// Alte Seite gegen neue in pteRAMlist austauschen
		pteRAMlist.remove(pteRAMlistIndex);
		pteRAMlist.add(pteRAMlistIndex, newPte);
		// Index auf Nachfolger setzen
		incrementPteRAMlistIndex();
		os.testOut("Prozess " + pid
				+ ": CLOCK-Algorithmus hat pte ausgew�hlt: " + pte.virtPageNum
				+ "  Neuer pteRAMlistIndex ist " + pteRAMlistIndex);

		return pte;
	}

	/**
	 * RANDOM-Algorithmus: Zuf�llige Auswahl
	 */
	private PageTableEntry randomAlgorithm(PageTableEntry newPte) {
		// ToDo
		
		return pte;
	}

	// ----------------------- Hilfsmethode --------------------------------
	/**
	 * ramPteIndex zirkular hochz�hlen zwischen 0 .. Listengr��e-1
	 */
	private void incrementPteRAMlistIndex() {
		pteRAMlistIndex = (pteRAMlistIndex + 1) % pteRAMlist.size();
	}

}
