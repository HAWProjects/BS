package org.haw.bs.praktikum3.ssp;

public class SchereSteinPapierMain extends Thread {
	private static final int DEFAULT_DAUER_MILLIS = 1000;
	private static final Tisch DEFAULT_TISCH = new LockedTisch();
	
	private Tisch myTisch;
	private Spieler mySpieler1;
	private Spieler mySpieler2;
	private Schiedsrichter mySchiedsrichter;
	
	private int myDauerMS;
	
	public SchereSteinPapierMain(int dauerMS, Tisch tisch) {
		myTisch = tisch;
		mySpieler1 = new Spieler("Spieler 1", myTisch);
		mySpieler2 = new Spieler("Spieler 2", myTisch);
		mySchiedsrichter = new Schiedsrichter(myTisch, mySpieler1, mySpieler2);
		myDauerMS = dauerMS;
	}
	
	public void run() {
		mySpieler1.start();
		mySpieler2.start();
		mySchiedsrichter.start();
		
		try {
			Thread.sleep(myDauerMS);
		} catch(InterruptedException e) {}
		
		mySpieler1.interrupt();
		mySpieler2.interrupt();
		mySchiedsrichter.interrupt();
		
		System.out.println("***** ENDE *****");
		System.out.println("Runden: " + mySchiedsrichter.getRunden());
		System.out.println("Siege " + mySpieler1.getName() + ": " + mySchiedsrichter.getSpieler1Gewonnen());
		System.out.println("Siege " + mySpieler2.getName() + ": " + mySchiedsrichter.getSpieler2Gewonnen());
		System.out.println("Unentschieden: " + mySchiedsrichter.getUnentschieden());
	}
	
	public static void main(String[] args) {
		if(args.length == 0) {
			new SchereSteinPapierMain(DEFAULT_DAUER_MILLIS, DEFAULT_TISCH).start();
		}
	}
}
