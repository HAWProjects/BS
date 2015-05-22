package org.haw.bs.praktikum2;

public class Car extends Thread {
	private final int myRundenMax;
	private int myRundenZeitMax;
	
	private int myRunde;
	private long myGesamtfahrtzeit;
	
	public Car(String name, int runden, int rundenZeitMax) {
		if(runden <= 0) throw new IllegalArgumentException("Ein Auto muss mindestens eine Runde fahren!");
		setName(name);
		myRundenMax = runden;
		myRundenZeitMax = rundenZeitMax;
	}
	
	public long getGesamtfahrtzeit() {
		return myGesamtfahrtzeit;
	}
	
	@Override
	public void run() {
		while(myRunde < myRundenMax && !Thread.interrupted()) {
			long roundTime = (long)(Math.random()*myRundenZeitMax);
			System.out.println(getName() + " [" + myRunde + "]: " + roundTime);
			try {
				Thread.sleep(roundTime);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			myGesamtfahrtzeit += roundTime;
			myRunde++;
		}
	}
}
