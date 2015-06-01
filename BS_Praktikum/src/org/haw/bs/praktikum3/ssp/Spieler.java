package org.haw.bs.praktikum3.ssp;

public class Spieler extends Thread {
	private Tisch myTisch;
	
	public Spieler(String name, Tisch tisch) {
		super(name);
		myTisch = tisch;
	}
	
	@Override
	public void run() {
		try {
			while(!Thread.interrupted()) {
				myTisch.putHand(this, waehleHand());
			}
		} catch(InterruptedException e) {}
	}
	
	private Hand waehleHand() {
		Hand[] haende = Hand.values();
		return haende[(int)(Math.random()*haende.length)];
	}
}
