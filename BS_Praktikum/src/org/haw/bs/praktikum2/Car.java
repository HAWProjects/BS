package org.haw.bs.praktikum2;

public class Car implements Runnable {
	private String myName;
	private final int myRundenMax;
	private int myRundenZeitMax;
	
	private int myRunde;
	private boolean bImZiel;
	private long myGesamtfahrtzeit;
	
	public Car(String name, int runden, int rundenZeitMax) {
		if(runden <= 0) throw new IllegalArgumentException("Ein Auto muss mindestens eine Runde fahren!");
		myName = name;
		myRundenMax = runden;
		myRundenZeitMax = rundenZeitMax;
	}
	
	public String getName() {
		return myName;
	}
	
	public boolean isImZiel() {
		return bImZiel;
	}
	
	public long getGesamtfahrtzeit() {
		return myGesamtfahrtzeit;
	}
	
	@Override
	public void run() {
		while(myRunde < myRundenMax && !Thread.interrupted()) {
			long roundTime = (long)(Math.random()*myRundenZeitMax);
			try {
				Thread.sleep(roundTime);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			myGesamtfahrtzeit += roundTime;
			myRunde++;
		}
		bImZiel = true;
	}
}
