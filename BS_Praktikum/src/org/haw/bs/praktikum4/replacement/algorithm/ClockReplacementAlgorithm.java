package org.haw.bs.praktikum4.replacement.algorithm;

import java.util.List;

import org.haw.bs.praktikum4.OperatingSystem;
import org.haw.bs.praktikum4.PageTableEntry;

/**
 * Implementation des Clock-Replacement-Algorithmus.
 * 
 * @author Lars Nielsen
 */
public class ClockReplacementAlgorithm implements ReplacementAlgorithm {
	/**
	 * Uhrzeiger für Clock-Algorithmus
	 */
	private int pteRAMlistIndex;
	
	@Override
	public PageTableEntry replaceAndReturn(List<PageTableEntry> pteRAMlist, int pid, PageTableEntry newPte, OperatingSystem os) {
		// Immer ab altem "Uhrzeigerstand" weitersuchen
		PageTableEntry pte = (PageTableEntry)pteRAMlist.get(pteRAMlistIndex);
		
		// Suche den nächsten Eintrag mit referenced == false (R-Bit = 0)
		while (pte.referenced == true) {
			// Seite wurde referenziert, also nicht auswählen, sondern R-Bit zurücksetzen
			os.testOut("Prozess " + pid + ": CLOCK-Algorithmus! --- pte.vpn: " + pte.virtPageNum + " ref: " + pte.referenced);
			pte.referenced = false;
			incrementPteRAMlistIndex(pteRAMlist);
			pte = (PageTableEntry) pteRAMlist.get(pteRAMlistIndex);
		}
		
		// Seite ausgewählt! (--> pteRAMlistIndex)
		// Alte Seite gegen neue in pteRAMlist austauschen
		pteRAMlist.remove(pteRAMlistIndex);
		pteRAMlist.add(pteRAMlistIndex, newPte);
		
		// Index auf Nachfolger setzen
		incrementPteRAMlistIndex(pteRAMlist);
		
		os.testOut("Prozess " + pid + ": CLOCK-Algorithmus hat pte ausgewählt: " + pte.virtPageNum + "  Neuer pteRAMlistIndex ist " + pteRAMlistIndex);
		
		return pte;
	}
	
	/**
	 * ramPteIndex zirkular hochzählen zwischen 0 .. Listengröße-1
	 */
	private void incrementPteRAMlistIndex(List<PageTableEntry> pteRAMlist) {
		pteRAMlistIndex = (pteRAMlistIndex + 1) % pteRAMlist.size();
	}
}
