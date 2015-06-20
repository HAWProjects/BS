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
	 * Die Seitentabelle als ArrayList von Seitentabelleneintrï¿½gen
	 * (PageTableEntry). Die Seitentabelle darf nicht sortiert werden!
	 */
	private ArrayList<PageTableEntry> pageTable;

	/**
	 * Liste aller Seiten, die sich im RAM befinden
	 */
	private LinkedList<PageTableEntry> pteRAMlist;

	/**
	 * Uhrzeiger fï¿½r Clock-Algorithmus
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
		// Die Liste auf RAM-Seiteneintrï¿½ge erzeugen
		pteRAMlist = new LinkedList<PageTableEntry>();
		pteRAMlistIndex = 0;
	}

	/**
	 * Rï¿½ckgabe: Seitentabelleneintrag pte (PageTableEntry) fï¿½r die
	 * ï¿½bergebene virtuelle Seitennummer (VPN = Virtual Page Number) oder
	 * null, falls Seite nicht existiert
	 */
	public PageTableEntry getPte(int vpn) {
		if ((vpn < 0) || (vpn >= pageTable.size())) {
			// Rï¿½ckgabe null, da Seite nicht existiert!
			return null;
		} else {
			return pageTable.get(vpn);
		}
	}

	/**
	 * Einen Eintrag (PageTableEntry) an die Seitentabelle anhï¿½ngen. Die
	 * Seitentabelle darf nicht sortiert werden!
	 */
	public void addEntry(PageTableEntry pte) {
		pageTable.add(pte);
	}

	/**
	 * Rï¿½ckgabe: Aktuelle Grï¿½ï¿½e der Seitentabelle.
	 */
	public int getSize() {
		return pageTable.size();
	}

	/**
	 * Pte in pteRAMlist eintragen, wenn sich die Zahl der RAM-Seiten des
	 * Prozesses erhï¿½ht hat.
	 */
	public void pteRAMlistInsert(PageTableEntry pte) {
		pteRAMlist.add(pte);
	}

	/**
	 * Eine Seite, die sich im RAM befindet, anhand der pteRAMlist auswï¿½hlen
	 * und zurï¿½ckgeben
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
	 * FIFO-Algorithmus: Auswahl = Listenkopf (1. Element) Anschlieï¿½end
	 * Listenkopf lï¿½schen, neue Seite (newPte) an Liste anhï¿½ngen
	 */
	private PageTableEntry fifoAlgorithm(PageTableEntry newPte) {
		PageTableEntry pte; // Auswahl

		pte = (PageTableEntry) pteRAMlist.getFirst();
		os.testOut("Prozess " + pid
				+ ": FIFO-Algorithmus hat pte ausgewï¿½hlt: " + pte.virtPageNum);
		pteRAMlist.removeFirst();
		pteRAMlist.add(newPte);
		return pte;
	}

	/**
	 * CLOCK-Algorithmus (Second-Chance): Nï¿½chstes Listenelement, ausgehend
	 * vom aktuellen Index, mit Referenced-Bit = 0 (false) auswï¿½hlen Sonst
	 * R-Bit auf 0 setzen und nï¿½chstes Element in der pteRAMlist untersuchen.
	 * Anschlieï¿½end die ausgewï¿½hlte Seite durch die neue Seite (newPte) am
	 * selben Listenplatz ersetzen
	 */
	private PageTableEntry clockAlgorithm(PageTableEntry newPte) {
		PageTableEntry pte; // Aktuell untersuchter Seitentabelleneintrag

		// Immer ab altem "Uhrzeigerstand" weitersuchen
		pte = (PageTableEntry) pteRAMlist.get(pteRAMlistIndex);

		// Suche den nï¿½chsten Eintrag mit referenced == false (R-Bit = 0)
		while (pte.referenced == true) {
			// Seite wurde referenziert, also nicht auswï¿½hlen, sondern R-Bit
			// zurï¿½cksetzen
			os.testOut("Prozess " + pid + ": CLOCK-Algorithmus! --- pte.vpn: "
					+ pte.virtPageNum + " ref: " + pte.referenced);
			pte.referenced = false;
			incrementPteRAMlistIndex();
			pte = (PageTableEntry) pteRAMlist.get(pteRAMlistIndex);
		}

		// Seite ausgewï¿½hlt! (--> pteRAMlistIndex)
		// Alte Seite gegen neue in pteRAMlist austauschen
		pteRAMlist.remove(pteRAMlistIndex);
		pteRAMlist.add(pteRAMlistIndex, newPte);
		// Index auf Nachfolger setzen
		incrementPteRAMlistIndex();
		os.testOut("Prozess " + pid
				+ ": CLOCK-Algorithmus hat pte ausgewï¿½hlt: "
				+ pte.virtPageNum + "  Neuer pteRAMlistIndex ist "
				+ pteRAMlistIndex);

		return pte;
	}

	/**
	 * RANDOM-Algorithmus: Zufaellige Auswahl
	 */
	private PageTableEntry randomAlgorithm(PageTableEntry newPte) {
		PageTableEntry pte; // Aktuell untersuchter Seitentabelleneintrag

		// zufälliger index bestimmen
		Random rand = new Random();
		int index = rand.nextInt(pteRAMlist.size());

		pte = (PageTableEntry) pteRAMlist.get(index);

		// Alte Seite gegen neue in pteRAMlist austauschen
		pteRAMlist.remove(index);
		pteRAMlist.add(index, newPte);
		
		return pte;
	}

	// ----------------------- Hilfsmethode --------------------------------
	/**
	 * ramPteIndex zirkular hochzï¿½hlen zwischen 0 .. Listengrï¿½ï¿½e-1
	 */
	private void incrementPteRAMlistIndex() {
		pteRAMlistIndex = (pteRAMlistIndex + 1) % pteRAMlist.size();
	}

}
