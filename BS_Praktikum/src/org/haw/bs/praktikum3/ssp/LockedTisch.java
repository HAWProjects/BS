package org.haw.bs.praktikum3.ssp;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockedTisch implements Tisch {
	private Map<Spieler, Hand> mySpielerHaendeMapping;
	private Lock myLock;
	private Condition myGespieltCondition;
	private Condition myAufgeraeumtCondition;
	
	public LockedTisch() {
		mySpielerHaendeMapping = new HashMap<>();
		myLock = new ReentrantLock(true);
		myGespieltCondition = myLock.newCondition();
		myAufgeraeumtCondition = myLock.newCondition();
	}
	
	@Override
	public void putHand(Spieler spieler, Hand hand) throws InterruptedException {
		myLock.lockInterruptibly();
		try {
			while(mySpielerHaendeMapping.containsKey(spieler)) {
				System.out.println(Thread.currentThread().getName() + " wartet auf seinen nächsten Zug!");
				myAufgeraeumtCondition.await();
			}
			System.out.println(Thread.currentThread().getName() + " hat " + hand + " gespielt!");
			mySpielerHaendeMapping.put(spieler, hand);
			myGespieltCondition.signal();
		} finally {
			myLock.unlock();
		}
	}
	
	@Override
	public Hand getHand(Spieler spieler) throws InterruptedException {
		myLock.lockInterruptibly();
		while(mySpielerHaendeMapping.size() != 2) {
			myGespieltCondition.await();
		}
		return mySpielerHaendeMapping.get(spieler);
	}
	
	@Override
	public void aufraeumen() throws InterruptedException {
		try {
			mySpielerHaendeMapping.clear();
			System.out.println("Der Schiedsrichter hat die nächste Runde freigegeben!");
			myAufgeraeumtCondition.signalAll();
		} finally {
			myLock.unlock();
		}
	}
}
