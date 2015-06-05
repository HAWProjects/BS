package org.haw.bs.praktikum3.mensa;

public class Student extends Thread {
	private Mensa myMensa;
	
	public Student(String name, Mensa mensa) {
		super(name);
		myMensa = mensa;
	}
	
	@Override
	public void run() {
		try {
			while(!Thread.interrupted()) {
				myMensa.bezahlen();
				
				// Essen
				Thread.sleep((int)(Math.random()*5000));
			}
		} catch(InterruptedException e) {}
	}
}
