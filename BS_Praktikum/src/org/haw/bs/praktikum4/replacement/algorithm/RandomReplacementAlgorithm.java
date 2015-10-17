package org.haw.bs.praktikum4.replacement.algorithm;

import java.util.List;

import org.haw.bs.praktikum4.OperatingSystem;
import org.haw.bs.praktikum4.PageTableEntry;

/**
 * Implementation des Random-Replacement-Algorithmus.
 * 
 * @author Lars Nielsen
 */
public class RandomReplacementAlgorithm implements ReplacementAlgorithm {
	@Override
	public PageTableEntry replaceAndReturn(List<PageTableEntry> pteRAMlist, int pid, PageTableEntry newPte, OperatingSystem os) {
		int pteRandomIndex = (int)(Math.random()*pteRAMlist.size());
		PageTableEntry pte = pteRAMlist.remove(pteRandomIndex);
		os.testOut("Prozess " + pid + ": RANDOM-Algorithmus hat pte ausgewählt: " + pte.virtPageNum);
		pteRAMlist.add(pteRandomIndex, newPte);
		return pte;
	}
}
