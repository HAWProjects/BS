package org.haw.bs.praktikum4.replacement.algorithm;

import java.util.List;

import org.haw.bs.praktikum4.OperatingSystem;
import org.haw.bs.praktikum4.PageTableEntry;

/**
 * Implementation des first-in first-out Replacement-Algorithmus.
 * 
 * @author Lars Nielsen
 */
public class FifoReplacementAlgorithm implements ReplacementAlgorithm {
	@Override
	public PageTableEntry replaceAndReturn(List<PageTableEntry> pteRAMlist, int pid, PageTableEntry newPte, OperatingSystem os) {
		PageTableEntry pte = pteRAMlist.remove(0);
		os.testOut("Prozess " + pid + ": FIFO-Algorithmus hat pte ausgewählt: " + pte.virtPageNum);
		pteRAMlist.add(newPte);
		return pte;
	}
}
