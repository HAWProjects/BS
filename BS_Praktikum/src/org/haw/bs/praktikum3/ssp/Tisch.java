package org.haw.bs.praktikum3.ssp;

public interface Tisch {
	
	void putHand(Spieler spieler, Hand hand) throws InterruptedException;
	
	Hand getHand(Spieler spieler) throws InterruptedException;
	
	void aufraeumen() throws InterruptedException;
}
