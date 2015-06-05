package org.haw.bs.praktikum3.mensa;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Mensa extends Thread {
	private List<Kasse> myKassen;
	private List<Student> myStudenten;
	private ReentrantLock myKassenLock;
	private int myDauerMillis;
	
	public Mensa(int kassen, int studenten, int dauerMillis) {
		myKassen = new LinkedList<>();
		for(int i=0; i<kassen; i++) {
			myKassen.add(new Kasse("Kasse"+(i+1)));
		}
		myStudenten = new LinkedList<>();
		for(int i=0; i<studenten; i++) {
			myStudenten.add(new Student("Student"+(i+1), this));
		}
		myKassenLock = new ReentrantLock(true);
		myDauerMillis = dauerMillis;
	}
	
	public void bezahlen() throws InterruptedException {
		myKassenLock.lockInterruptibly();
		Kasse minKasse = null;
		for(Kasse kasse : myKassen) {
			if(minKasse == null || kasse.laengeDerSchlange() < minKasse.laengeDerSchlange()) {
				minKasse = kasse;
			}
		}
		minKasse.anstellen();
		myKassenLock.unlock();
		
		minKasse.bezahlen();
		
		myKassenLock.lockInterruptibly();
		minKasse.verlassen();
		myKassenLock.unlock();
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
