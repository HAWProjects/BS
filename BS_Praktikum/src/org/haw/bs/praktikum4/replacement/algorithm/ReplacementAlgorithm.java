package org.haw.bs.praktikum4.replacement.algorithm;

import java.util.List;

import org.haw.bs.praktikum4.OperatingSystem;
import org.haw.bs.praktikum4.PageTableEntry;

/**
 * Schnittstelle für Replacement-Algorithmen.
 * 
 * @author Lars Nielsen
 */
public interface ReplacementAlgorithm {
	/**
	 * Ersetzt einen Eintrag in der Seitentabelle mit dem übergebenen Eintrag
	 * und gibt den alten, entfernten Eintrag zurück.
	 * @param pteRAMlist Die Liste der Seiteneinträge im Arbeitsspeicher
	 * @param pid Die Prozess-ID
	 * @param newPte Der neue Eintrag
	 * @param os Das {@link OperatingSystem}, wird nur für die Debugausgaben benutzt
	 * @return Der ersetzte Eintrag
	 */
	PageTableEntry replaceAndReturn(List<PageTableEntry> pteRAMlist, int pid, PageTableEntry newPte, OperatingSystem os);
}
