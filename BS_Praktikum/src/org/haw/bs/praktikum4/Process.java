package org.haw.bs.praktikum4;

/*
 * Process
 *
 * Repräsentation eines unabhängigen Prozess-Objekts
 */

/**
 * Programm-Simulation:
 * 
 * Daten eines Prozesses verwalten (PCB) sowie Laufzeitverhalten simulieren,
 * d.h. read-Operationen im eigenen virtuellen Speicher ausführen
 * (Pseudo-Zufallszahlengeneratorgesteuert) mit mehreren Operationen im selben
 * Seitenbereich (gemäß "Lokalitätsfaktor")
 */
public class Process extends Thread {
	/**
	 * Speicherbedarf für das gesamte Programm (in Byte)
	 */
	private int processSize;

	/**
	 * Dieser Faktor bestimmt das "Lokalitätsverhalten" eines Programms (=
	 * Anzahl Operationen innerhalb eines Seitenbereichs) Setzen über
	 * os.getDEFAULT_LOCALITY_FACTOR()
	 */
	private int localityFactor;

	/**
	 * Dieser Faktor bestimmt das "Lokalitätsverhalten" eines Programms = max.
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
	private OperatingSystem os; // Handle für System Calls

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
		int median; // Mittelwert für virtuelle Adressen
		int bias; // Streuung um den Mittelwert
		int virtAdr; // Virtuelle Adresse
		int i; // Zähler

		// Streuungsbereich festlegen (Anzahl Seiten)
		bias = BIAS_FACTOR * os.getPAGE_SIZE();

		while (!isInterrupted()) {
			// Neue Adresse bestimmen (muss im bereits geschriebenen Bereich
			// liegen!)
			median = (int) (processSize * Math.random());

			// Neue Adresse als Mittelwert für die nächsten Zugriffe verwenden
			// (localityFactor)
			for (i = 0; i < localityFactor; i++) {
				// Virtuelle Adresse in der "Nähe" des Mittelwerts (median) bestimmen
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
