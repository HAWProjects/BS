package org.haw.bs.praktikum4;

/**
 * Sammlung und Auswertung statistischer Daten eines Simulationslaufs
 */
public class Statistics {

	private int pageFaults;			// Anzahl Seitenfehler
	private int writeAccesses;		// Anzahl Schreibzugriffe
	private int readAccesses;		// Anzahl Lesezugriffe

	//	Seitenfehlerrrate = Anzahl Seitenfehler / Anzahl Zugriffe
	private float pageFaultRate;	
				
	/**
	 * Konstruktor
	 */
	public Statistics() {
		resetCounter();
	}

	/**
	 * Alle Statistik-Zähler zurücksetzen
	 */
	public synchronized void resetCounter() {
		pageFaults = 0;
		writeAccesses = 0;
		readAccesses = 0;
		pageFaultRate = 0;		
	}
	
	/**
	 * @return Seitenfehlerrrate = Anzahl Seitenfehler / Anzahl Zugriffe
	 */
	public synchronized float getPageFaultRate() {
		pageFaultRate = (float) pageFaults / (writeAccesses + readAccesses);
		return pageFaultRate;
	}

	/**
	 * @return Anzahl Seitenfehler
	 */
	public synchronized int getPageFaults() {
		return pageFaults;
	}

	/**
	 * @return Anzahl Zugriffe insgesamt
	 */
	public synchronized int getTotalAccesses() {
		return readAccesses + writeAccesses;
	}
	
	/**
	 * @return Anzahl Lesezugriffe
	 */
	public synchronized int getReadAccesses() {
		return readAccesses;
	}

	/**
	 * @return Anzahl Schreibzugriffe 
	 */
	public synchronized int getWriteAccesses() {
		return writeAccesses;
	}

	/**
	 * Seitenfehler zählen
	 */
	public synchronized void incrementPageFaults() {
		pageFaults++;
	}

	/**
	 * Lesezugriff zählen
	 */
	public synchronized void incrementReadAccesses() {
		readAccesses++;
	}

	/**
	 * Schreibzugriff zählen
	 */
	public synchronized void incrementWriteAccesses() {
		writeAccesses++;
	}

	/**
	 *  Statistik-Bericht auf der Console ausgeben
	 *
	 */
	public synchronized void showReport() {
		System.out.println("\n**************** Statistik *************************");
		System.out.println("*** Anzahl Seitenfehler: "+getPageFaults());
		System.out.println("*** Anzahl Zugriffe:     "+getTotalAccesses());
		System.out.println("*** Seitenfehlerrate:    "+getPageFaultRate());
		System.out.println("****************************************************");
	}
}
