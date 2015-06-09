package org.haw.bs.praktikum4;

/*
 * Process
 *
 * Repr�sentation eines unabh�ngigen Prozess-Objekts
 */

/**
 * Programm-Simulation:
 * 
 * Daten eines Prozesses verwalten (PCB) sowie Laufzeitverhalten simulieren,
 * d.h. read-Operationen im eigenen virtuellen Speicher ausf�hren
 * (Pseudo-Zufallszahlengeneratorgesteuert) mit mehreren Operationen im selben
 * Seitenbereich (gem�� "Lokalit�tsfaktor")
 */
public class Process extends Thread {

	/**
	 * Speicherbedarf f�r das gesamte Programm (in Byte)
	 */
	private int processSize;

	/**
	 * Dieser Faktor bestimmt das "Lokalit�tsverhalten" eines Programms (=
	 * Anzahl Operationen innerhalb eines Seitenbereichs) Setzen �ber
	 * os.getDEFAULT_LOCALITY_FACTOR()
	 */
	private int localityFactor;

	/**
	 * Dieser Faktor bestimmt das "Lokalit�tsverhalten" eines Programms = max.
	 * Streuung (+/-) bei lokalen Operationen in Anzahl Seiten
	 */
	private static final int BIAS_FACTOR = 2;

	// --------------- Process Control Block (PCB) -------------------
	/**
	 * Eigene Prozess-ID:
	 */
	public int pid;

	/**
	 * Eigene Seitentabelle
	 */
	public PageTable pageTable;

	// ---------- Prozess-Variablen ------------------------------
	private OperatingSystem os; // Handle f�r System Calls

	/**
	 * Konstruktor
	 */
	public Process(OperatingSystem currentOS, int newPID, int newProcessSize) {
		os = currentOS;
		pid = newPID;
		processSize = newProcessSize;
		pageTable = new PageTable(os, pid);
		localityFactor = os.getDEFAULT_LOCALITY_FACTOR();
	}

	/**
	 * Programmcode eines Prozesses: Zugriff auf Speicherseiten simulieren (nur
	 * read)
	 * 
	 */
	public void run() {
		int median; // Mittelwert f�r virtuelle Adressen
		int bias; // Streuung um den Mittelwert
		int virtAdr; // Virtuelle Adresse
		int i; // Z�hler

		// Streuungsbereich festlegen (Anzahl Seiten)
		bias = BIAS_FACTOR * os.getPAGE_SIZE();

		while (!isInterrupted()) {
			// Neue Adresse bestimmen (muss im bereits geschriebenen Bereich
			// liegen!)
			median = (int) (processSize * Math.random());

			// Neue Adresse als Mittelwert f�r die n�chsten Zugriffe verwenden
			// (localityFactor)
			for (i = 0; i < localityFactor; i++) {
				// Virtuelle Adresse in der "N�he" des Mittelwerts (median) bestimmen
				virtAdr = ((int) (2 * bias * Math.random() - bias)) + median;
				// Grenzen setzen: 0 <= virtAdr <= PROGRAM_SIZE -
				// os.getWORD_SIZE() !!
				virtAdr = Math.min(virtAdr, processSize - os.getWORD_SIZE());
				virtAdr = Math.max(virtAdr, 0);
				// Virt. Adresse auf Wortgrenze ausrichten
				virtAdr = virtAdr - (virtAdr % os.getWORD_SIZE());

				// Read System Call (Returnwert wird hier nicht ausgwertet)
				os.read(pid, virtAdr);
			}
		}

	}
}
