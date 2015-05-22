package org.haw.bs.praktikum2;

public class Accident extends Thread {
	private SimRace myRace;
	private int myAccidentZeitpunktMin;
	private int myAccidentZeitpunktMax;
	private boolean bAufgetretenWaehrendRennen;
	
	public Accident(SimRace race, int accidentZeitpunktMin, int accidentZeitpunktMax) {
		myRace = race;
		myAccidentZeitpunktMin = accidentZeitpunktMin;
		myAccidentZeitpunktMax = accidentZeitpunktMax;
	}
	
	public boolean isAufgetretenWaehrendRennen() {
		return bAufgetretenWaehrendRennen;
	}
	
	@Override
	public void run() {
		try {
			long accidentZeitpunkt = (long)(myAccidentZeitpunktMin + Math.random() * (myAccidentZeitpunktMax-myAccidentZeitpunktMin));
			Thread.sleep(accidentZeitpunkt);
			myRace.meldeUnfall();
		} catch (InterruptedException e) {}
	}
}
