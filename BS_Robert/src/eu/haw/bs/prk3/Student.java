package eu.haw.bs.prk3;

import java.util.LinkedList;

public class Student extends Thread {
	
	LinkedList<Kasse> kassenList;
	
	public Student(LinkedList<Kasse> kassenList ) {
		this.kassenList = kassenList;
	}

	@Override
	public void run() {
		
	}
}
