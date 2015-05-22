package org.haw.bs.praktikum2;

import java.util.List;

public class Accident implements Runnable {
	private List<Thread> myCarThreads;
	private int myAccidentZeitpunktMin;
	private int myAccidentZeitpunktMax;
	private boolean bAufgetretenWaehrendRennen;
	
	public Accident(List<Thread> carThreads, int accidentZeitpunktMin, int accidentZeitpunktMax) {
		myCarThreads = carThreads;
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
			System.out.println("UNFALL!");
			for(Thread thread : myCarThreads) {
				thread.interrupt();
			}
			bAufgetretenWaehrendRennen = true;
		} catch (InterruptedException e) {}
	}
}
