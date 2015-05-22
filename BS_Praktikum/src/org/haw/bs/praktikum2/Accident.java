package org.haw.bs.praktikum2;

public class Accident extends Thread {
	private AccidentListener_I myAccidentListener;
	private int myAccidentZeitpunktMin;
	private int myAccidentZeitpunktMax;
	private boolean bAufgetretenWaehrendRennen;
	
	public Accident(AccidentListener_I accidentListener, int accidentZeitpunktMin, int accidentZeitpunktMax) {
		myAccidentListener = accidentListener;
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
			myAccidentListener.onAccident();
		} catch (InterruptedException e) {}
	}
}
