package eu.haw.bs.prk3;

import java.util.LinkedList;

public class Mensa {

	private int kassen;
	private int studenten;
	
	public Mensa(int kassen, int studenten){
		this.kassen = kassen;
		this.studenten = studenten;
	}
	
	public void holeEssen() throws InterruptedException{
		int sleepTime = (int) (1000 * Math.random());
		Thread.sleep(sleepTime);
	}
	
	
	
	private void startMensa() {
		//liste enthälz alle kassen
		LinkedList<Kasse> kassenList = new LinkedList<Kasse>();
		
		//erzeugung der KassenThreads
		for(int i = 0 ; i < kassen; ++i){
			kassenList.add(new Kasse());
		}
		
		//erzeugung der Studententhreads
		for(int i = 0 ; i < studenten; ++i){
			Student student = new Student(kassenList);
			student.start();
		}
		
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void main(String[] args) {
		new Mensa(3, 10).startMensa();

	}


}
