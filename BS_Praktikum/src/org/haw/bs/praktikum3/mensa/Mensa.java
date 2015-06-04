package org.haw.bs.praktikum3.mensa;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Mensa extends Thread {
	private List<Kasse> myKassen;
	private List<Student> myStudenten;
	private int myDauerMillis;
	
	private ReentrantLock lockKassen;
	
	public Mensa(int kassen, int studenten, int dauerMillis) {
		myKassen = new LinkedList<>();
		this.lockKassen = new ReentrantLock();
		
		for(int i=0; i<kassen; i++) {
			myKassen.add(new Kasse("Kasse"+(i+1)));
		}
		myStudenten = new LinkedList<>();
		for(int i=0; i<studenten; i++) {
			myStudenten.add(new Student("Student"+(i+1), this));
		}
		myDauerMillis = dauerMillis;
	}
	
	public Kasse getKasseMitKuerzesterSchlange() throws InterruptedException {
		lockKassen.lock();
		Kasse minKasse = null;
		for(Kasse kasse : myKassen) {
				if(minKasse == null || kasse.laengeDerSchlange() < minKasse.laengeDerSchlange()) {
					minKasse = kasse;
				}
		}
		lockKassen.unlock();
		return minKasse;
	}
	
	@Override
	public void run() {
		for(Student student : myStudenten) {
			student.start();
		}
		
		try {
			Thread.sleep(myDauerMillis);
		} catch(InterruptedException e) {}
		
		for(Student student : myStudenten) {
			student.interrupt();
		}
	}
}
