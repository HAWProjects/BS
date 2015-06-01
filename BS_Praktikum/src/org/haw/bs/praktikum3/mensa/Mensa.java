package org.haw.bs.praktikum3.mensa;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Mensa extends Thread {
	private List<Kasse> myKassen;
	private List<Student> myStudenten;
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
		myDauerMillis = dauerMillis;
	}
	
	public List<Kasse> getKassen() {
		return Collections.unmodifiableList(myKassen);
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
