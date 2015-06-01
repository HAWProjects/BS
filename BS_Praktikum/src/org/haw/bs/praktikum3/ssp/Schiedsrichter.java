package org.haw.bs.praktikum3.ssp;

public class Schiedsrichter extends Thread {
	private Tisch myTisch;
	private Spieler mySpieler1;
	private Spieler mySpieler2;
	
	private int myRunden;
	private int mySpieler1Gewonnen;
	private int mySpieler2Gewonnen;
	private int myUnentschieden;
	
	public Schiedsrichter(Tisch tisch, Spieler spieler1, Spieler spieler2) {
		super("Schiedsrichter");
		myTisch = tisch;
		mySpieler1 = spieler1;
		mySpieler2 = spieler2;
	}
	
	public int getRunden() {
		return myRunden;
	}
	
	public int getSpieler1Gewonnen() {
		return mySpieler1Gewonnen;
	}
	
	public int getSpieler2Gewonnen() {
		return mySpieler2Gewonnen;
	}
	
	public int getUnentschieden() {
		return myUnentschieden;
	}
	
	@Override
	public void run() {
		try {
			while(!Thread.interrupted()) {
				Hand hand1 = myTisch.getHand(mySpieler1);
				Hand hand2 = myTisch.getHand(mySpieler2);
				if(hand1.schlaegt(hand2)) {
					System.out.println("Spieler 1 gewinnt!");
					mySpieler1Gewonnen++;
				} else if(hand2.schlaegt(hand1)) {
					System.out.println("Spieler 2 gewinnt!");
					mySpieler2Gewonnen++;
				} else {
					System.out.println("UNENTSCHIEDEN");
					myUnentschieden++;
				}
				myRunden++;
				myTisch.aufraeumen();
			}
		} catch(InterruptedException e) {}
	}
}
