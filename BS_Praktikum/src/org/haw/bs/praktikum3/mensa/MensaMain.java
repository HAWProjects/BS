package org.haw.bs.praktikum3.mensa;

public class MensaMain {
	private static final int DEFAULT_ANZAHL_KASSEN = 4;
	private static final int DEFAULT_ANZAHL_STUDENTEN = 20;
	private static final int DEFAULT_DAUER_MILLIS = 5000;
	
	public static void usage() {
		System.out.println(MensaMain.class.getSimpleName() + " [KASSEN, STUDENTEN, DAUER]\n"
		                 + "\n"
		                 + " KASSEN\tDie Anzahl der zu simulierenden Kassen\n"
		                 + " STUDENTEN\tDie Anzahl der zu simulierenden Studenten\n"
		                 + " DAUER\tDie Dauer der Simulation in Millisekunden");
	}
	
	public static void main(String[] args) {
		if(args.length == 3) {
			try {
				int kassen = Integer.parseInt(args[0]);
				int studenten = Integer.parseInt(args[1]);
				int dauerMS = Integer.parseInt(args[2]);
				new Mensa(kassen, studenten, dauerMS).start();
			} catch(NumberFormatException e) {
				System.out.println("Die Parameter müssen Zahlen sein");
			}
		} else if(args.length == 0) {
			new Mensa(DEFAULT_ANZAHL_KASSEN, DEFAULT_ANZAHL_STUDENTEN, DEFAULT_DAUER_MILLIS).start();
		} else {
			usage();
		}
	}
}
