package org.haw.bs.praktikum4;

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
	 * Die Seitentabelle als ArrayList von Seitentabelleneinträgen
	 * (PageTableEntry). Die Seitentabelle darf nicht sortiert werden!
	 */
	private ArrayList<PageTableEntry> pageTable;

	/**
	 * Liste aller Seiten, die sich im RAM befinden
	 */
	private LinkedList<PageTableEntry> pteRAMlist;

	/**
	 * Uhrzeiger für Clock-Algorithmus
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
		pageTable = new ArrayList<PageTableEntry>();
		pteRAMlist = new LinkedList<PageTableEntry>();
		pteRAMlistIndex = 0;
	}

	/**
	 * Rückgabe: Seitentabelleneintrag pte (PageTableEntry) für die übergebene
	 * virtuelle Seitennummer (VPN = Virtual Page Number) oder null, falls Seite
	 * nicht existiert
	 */
	public PageTableEntry getPte(int vpn) {
		if ((vpn < 0) || (vpn >= pageTable.size())) {
			return null;
		} else {
			return pageTable.get(vpn);
		}
	}

	/**
	 * Einen Eintrag (PageTableEntry) an die Seitentabelle anhängen. Die
	 * Seitentabelle darf nicht sortiert werden!
	 */
	public void addEntry(PageTableEntry pte) {
		pageTable.add(pte);
	}

	/**
	 * Rückgabe: Aktuelle Größe der Seitentabelle.
	 */
	public int getSize() {
		return pageTable.size();
	}

	/**
	 * Pte in pteRAMlist eintragen, wenn sich die Zahl der RAM-Seiten des
	 * Prozesses erhöht hat.
	 */
	public void pteRAMlistInsert(PageTableEntry pte) {
		pteRAMlist.add(pte);
	}

	/**
	 * Eine Seite, die sich im RAM befindet, anhand der pteRAMlist auswählen und
	 * zurückgeben
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
	 * FIFO-Algorithmus: Auswahl = Listenkopf (1. Element) Anschließend
	 * Listenkopf löschen, neue Seite (newPte) an Liste anhängen
	 */
	private PageTableEntry fifoAlgorithm(PageTableEntry newPte) {
		PageTableEntry pte = (PageTableEntry) pteRAMlist.getFirst();
		os.testOut("Prozess " + pid + ": FIFO-Algorithmus hat pte ausgewählt: " + pte.virtPageNum);
		pteRAMlist.removeFirst();
		pteRAMlist.add(newPte);
		return pte;
	}

	/**
	 * CLOCK-Algorithmus (Second-Chance): Nächstes Listenelement, ausgehend vom
	 * aktuellen Index, mit Referenced-Bit = 0 (false) auswählen Sonst R-Bit auf
	 * 0 setzen und nächstes Element in der pteRAMlist untersuchen. Anschließend
	 * die ausgewählte Seite durch die neue Seite (newPte) am selben Listenplatz
	 * ersetzen
	 */
	private PageTableEntry clockAlgorithm(PageTableEntry newPte) {
		PageTableEntry pte; // Aktuell untersuchter Seitentabelleneintrag

		// Immer ab altem "Uhrzeigerstand" weitersuchen
		pte = (PageTableEntry) pteRAMlist.get(pteRAMlistIndex);

		// Suche den nächsten Eintrag mit referenced == false (R-Bit = 0)
		while (pte.referenced == true) {
			// Seite wurde referenziert, also nicht auswählen, sondern R-Bit zurücksetzen
			os.testOut("Prozess " + pid + ": CLOCK-Algorithmus! --- pte.vpn: " + pte.virtPageNum + " ref: " + pte.referenced);
			pte.referenced = false;
			incrementPteRAMlistIndex();
			pte = (PageTableEntry) pteRAMlist.get(pteRAMlistIndex);
		}

		// Seite ausgewählt! (--> pteRAMlistIndex)
		// Alte Seite gegen neue in pteRAMlist austauschen
		pteRAMlist.remove(pteRAMlistIndex);
		pteRAMlist.add(pteRAMlistIndex, newPte);
		// Index auf Nachfolger setzen
		incrementPteRAMlistIndex();
		os.testOut("Prozess " + pid + ": CLOCK-Algorithmus hat pte ausgewählt: " + pte.virtPageNum + "  Neuer pteRAMlistIndex ist " + pteRAMlistIndex);

		return pte;
	}

	/**
	 * RANDOM-Algorithmus: Zufällige Auswahl
	 */
	private PageTableEntry randomAlgorithm(PageTableEntry newPte) {
		int pteRandomIndex = (int)(Math.random()*pteRAMlist.size());
		PageTableEntry pte = pteRAMlist.remove(pteRandomIndex);
		os.testOut("Prozess " + pid + ": RANDOM-Algorithmus hat pte ausgewählt: " + pte.virtPageNum);
		pteRAMlist.add(pteRandomIndex, newPte);
		return pte;
	}

	// ----------------------- Hilfsmethode --------------------------------
	/**
	 * ramPteIndex zirkular hochzählen zwischen 0 .. Listengröße-1
	 */
	private void incrementPteRAMlistIndex() {
		pteRAMlistIndex = (pteRAMlistIndex + 1) % pteRAMlist.size();
	}
}
