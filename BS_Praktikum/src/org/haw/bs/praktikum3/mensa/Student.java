package org.haw.bs.praktikum3.mensa;

public class Student extends Thread {
	private Mensa myMensa;
	
	public Student(String name, Mensa mensa) {
		super(name);
		myMensa = mensa;
	}
	
	@Override
	public void run() {
		try {
			while(!Thread.interrupted()) {
				Kasse anstellkasse = null;
				for(Kasse kasse : myMensa.getKassen()) {
					if(anstellkasse == null || anstellkasse.laengeDerSchlange() > kasse.laengeDerSchlange()) {
						anstellkasse = kasse;
					}
				}
				anstellkasse.anstellen();
				
				// Anstehzeit
				Thread.sleep((int)(Math.random()*1000));
				
				anstellkasse.bezahlen();
				
				// Essen
				Thread.sleep((int)(Math.random()*5000));
			}
		} catch(InterruptedException e) {}
	}
}
