package org.haw.bs.praktikum2;

import java.util.List;

public class Accident implements Runnable {
	private List<Thread> myCarThreads;
	private List<Car> myCars;
	private int myAccidentZeitpunktMin;
	private int myAccidentZeitpunktMax;
	private boolean bAufgetretenWaehrendRennen;
	
	public Accident(List<Thread> carThreads, List<Car> cars, int accidentZeitpunktMin, int accidentZeitpunktMax) {
		myCarThreads = carThreads;
		myCars = cars;
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
		} catch (InterruptedException e) {}
		for(Thread thread : myCarThreads) {
			thread.interrupt();
		}
		for(Car car : myCars) {
			if(!car.isImZiel()) {
				bAufgetretenWaehrendRennen = true;
			}
		}
	}
}
