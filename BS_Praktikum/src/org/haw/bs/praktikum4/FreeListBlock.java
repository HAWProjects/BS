package org.haw.bs.praktikum4;

/**
 * FreeListBlock
 *
 * Datenstrukturelement einer Freibereichsliste, das Adresse und Größe eines freien Blocks angibt
 */
public class FreeListBlock implements Comparable<Object> {
	private int adress;		// Reale Startadresse des freien Blocks
	private int size;		// Länge des freien Blocks in Byte

	/**
	 * Konstruktor
	 */	
	public FreeListBlock(int curAdr, int curSize) {
		adress = curAdr;
		size = curSize;
	}

	/**
	 * Vergleichsfunktion für Sortierung
	 */	
	public int compareTo(Object otherBlock) {
		// Vergleiche mit anderem FreeListBlock für die Sortierung
		return this.getAdress() - ((FreeListBlock) otherBlock).getAdress();
	}
	
	/**
	 * @return Adresse des freien Blocks
	 */
	public int getAdress() {
		return adress;
	}

	/**
	 * @return Größe des Blocks
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
	 * @param i Größe zuweisen
	 */
	public void setSize(int i) {
		size = i;
	}

	@Override
	public String toString() {
		return "(" + adress + "," + size + ")";
	}
}
