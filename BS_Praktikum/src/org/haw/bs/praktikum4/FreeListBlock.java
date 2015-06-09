package org.haw.bs.praktikum4;

/**
 * FreeListBlock
 *
 * Datenstrukturelement einer Freibereichsliste, das Adresse und Gr��e eines freien Blocks angibt
 */
public class FreeListBlock implements Comparable<Object> {
	private int adress;		// Reale Startadresse des freien Blocks
	private int size;		// L�nge des freien Blocks in Byte

	/**
	 * Konstruktor
	 *
	 */	
	public FreeListBlock(int curAdr, int curSize) {
		adress = curAdr;
		size = curSize;
	}

	/**
	 * Vergleichsfunktion f�r Sortierung
	 */	

	public int compareTo(Object otherBlock) {
		// Vergleiche mit anderem FreeListBlock f�r die Sortierung
		return this.getAdress() - ((FreeListBlock) otherBlock).getAdress();
	}
	
	/**
	 * @return Adresse des freien Blocks
	 */
	public int getAdress() {
		return adress;
	}

	/**
	 * @return Gr��e des Blocks
	 */
	public int getSize() {
		return size;
	}

	/**
	 * @param i Adresse zuweisen
	 */
	public void setAdress(int i) {
		adress = i;
	}

	/**
	 * @param i Gr��e zuweisen
	 */
	public void setSize(int i) {
		size = i;
	}

	@Override
	public String toString() {
		return "(" + adress + "," + size + ")";
	}
}
