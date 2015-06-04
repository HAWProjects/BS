package org.haw.bs.praktikum3.mensa;

import java.util.Collections;
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
	
	public List<Kasse> getKassen() {
		return Collections.unmodifiableList(myKassen);
	}
	
	public Kasse getKasseMitkuerzesterSchlange() throws InterruptedException{
		lockKassen.lock();
		Kasse minKasse = null;
		int min = Integer.MAX_VALUE;
		for(Kasse kasse : myKassen){
				int schlangenLaenge = kasse.laengeDerSchlange();
				if(schlangenLaenge < min){
					min = schlangenLaenge;
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
