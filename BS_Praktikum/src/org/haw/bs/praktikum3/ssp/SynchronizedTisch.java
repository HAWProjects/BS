package org.haw.bs.praktikum3.ssp;

import java.util.HashMap;
import java.util.Map;

public class SynchronizedTisch implements Tisch {
	private Map<Spieler, Hand> mySpielerHaendeMapping;
	
	public SynchronizedTisch() {
		mySpielerHaendeMapping = new HashMap<>();
	}
	
	@Override
	public synchronized void putHand(Spieler spieler, Hand hand) throws InterruptedException {
		while(mySpielerHaendeMapping.containsKey(spieler)) {
			System.out.println(Thread.currentThread().getName() + " wartet auf seinen nächsten Zug!");
			wait();
		}
		System.out.println(Thread.currentThread().getName() + " hat " + hand + " gespielt!");
		mySpielerHaendeMapping.put(spieler, hand);
		notifyAll();
	}
	
	@Override
	public synchronized Hand getHand(Spieler spieler) throws InterruptedException {
		while(mySpielerHaendeMapping.size() != 2) {
			wait();
		}
		return mySpielerHaendeMapping.get(spieler);
	}
	
	@Override
	public synchronized void aufraeumen() throws InterruptedException {
		mySpielerHaendeMapping.clear();
		System.out.println("Der Schiedsrichter hat die nächste Runde freigegeben!");
		notifyAll();
	}
}
