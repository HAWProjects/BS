package org.haw.bs.praktikum4;

/**
 * PageTableEntry (pte)
 * 
 * Daten eines Seitentabelleneintrags
 * 
 */
public class PageTableEntry {
	/**
	 *	virtPageNum: Virtuelle Seitennummer (VSN - engl. VPN) 
	 */
	public int virtPageNum;

	/**
	 * realPageFrameAdr: reale RAM- oder Plattenblockadresse des Seitenrahmens;
	 * auf die Verwendung von Seitenrahmennummern oder eine gesonderte
	 * Plattenblockverwaltung wird hier einfachheitshalber verzichtet
	 */
	public int realPageFrameAdr;

	/**
	 *	valid: true = im Hauptspeicher, false = auf Platte 
	 */
	public boolean valid;

	/**
	 * 	referenced: Referenziert, d.h. Zugriff erfolgt?
	 */
	public boolean referenced;

	/**
	 * 	modified: Hier nicht verwendet, da keine Kopie einer Seite auf der Platte
	 *  gehalten wird
	 */
	public boolean modified;

	/**
	 * Konstruktor: Belegung mit Default-Werten
	 */
	public PageTableEntry() {
		virtPageNum = 0;
		realPageFrameAdr = -1;
		valid = true;
		referenced = false;
		modified = false;
	}

	@Override
	public String toString() {
		return "PageTableEntry [virtPageNum=" + virtPageNum
				+ ", realPageFrameAdr=" + realPageFrameAdr + ", valid=" + valid
				+ ", referenced=" + referenced + ", modified=" + modified + "]";
	}
}
