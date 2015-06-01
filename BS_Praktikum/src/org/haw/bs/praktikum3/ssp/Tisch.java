package org.haw.bs.praktikum3.ssp;

import java.util.HashMap;
import java.util.Map;

public class Tisch {
	private Map<Spieler, Hand> mySpielerHaendeMapping;
	
	public Tisch() {
		mySpielerHaendeMapping = new HashMap<>();
	}
	
	public synchronized void putHand(Spieler spieler, Hand hand) throws InterruptedException {
		while(mySpielerHaendeMapping.containsKey(spieler)) {
			System.out.println(Thread.currentThread().getName() + " wartet auf seinen nächsten Zug!");
			wait();
		}
		System.out.println(Thread.currentThread().getName() + " hat " + hand + " gespielt!");
		mySpielerHaendeMapping.put(spieler, hand);
		notifyAll();
	}
	
	public synchronized Hand getHand(Spieler spieler) throws InterruptedException {
		while(mySpielerHaendeMapping.size() != 2) {
			wait();
		}
		return mySpielerHaendeMapping.get(spieler);
	}
	
	public synchronized void aufraeumen() throws InterruptedException {
		mySpielerHaendeMapping.clear();
		System.out.println("Der Schiedsrichter hat die nächste Runde freigegeben!");
		notifyAll();
	}
}
