package org.haw.bs.praktikum2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SimRace extends Thread implements AccidentListener_I {
	private static final int DEFAULT_ANZAHL_AUTOS  = 5;
	private static final int DEFAULT_ANZAHL_RUNDEN = 10;
	private static final int DEFAULT_RUNDEN_ZEIT   = 100;
	
	private int myAutos;
	private int myRunden;
	
	private List<Car> myCars;
	private Accident myAccident;
	
	public SimRace(int autos, int runden, int rundenZeitMax) {
		myAutos = autos;
		myRunden = runden;
		
		// Autos und Threads initialisieren
		myCars = new ArrayList<Car>(autos);
		for(int i=0; i<autos; i++) {
			Car car = new Car("Auto " + i, runden, rundenZeitMax);
			myCars.add(car);
		}
		myAccident = new Accident(this, rundenZeitMax, rundenZeitMax*runden);
	}
	
	@Override
	public void run() {
		// Alle Autos losfahren lassen
		System.out.println("Starte Rennen mit " + myAutos + " Autos für " + myRunden + " Runden");
		myAccident.start();
		for(Car car : myCars) {
			car.start();
		}
		
		// Warten, bis alle Autos fertig sind
		try {
			for(Car car : myCars) {
				car.join();
			}
			// Alle Autos im Ziel
			myAccident.interrupt();
			
			// Sortieren nach kleinster Gesamtfahrtzeit
			Collections.sort(myCars, new Comparator<Car>() {
				@Override
				public int compare(Car o1, Car o2) {
					return (int)(o1.getGesamtfahrtzeit()-o2.getGesamtfahrtzeit());
				}
			});
			
			// Ergebnisausgabe
			System.out.println("***** Endstand *****");
			for(int i=0; i<myCars.size(); i++) {
				Car car = myCars.get(i);
				System.out.println((i+1) + ". Platz: " + car.getName() + " (" + car.getGesamtfahrtzeit() + ")");
			}
		} catch(InterruptedException e) {
			for(Car car : myCars) {
				car.interrupt();
			}
			System.out.println("Das Rennen musste aufgrund eines Unfalls abgebrochen werden!");
		}
	}
	
	@Override
	public void onAccident() {
		interrupt();
	}
	
	private static void usage() {
		System.out.println(SimRace.class.getSimpleName() + " [AUTOS, RUNDEN, ZEITMAX]\n"
		                 + "\n"
		                 + " AUTOS\tDie Anzahl der zu simulierenden Autos\n"
		                 + " RUNDEN\tDie Anzahl der zu fahrenden Runden\n"
		                 + " ZEITMAX\tDie maximale Zeit, die ein Auto für eine Runde braucht\n");
	}
	
	public static void main(String... args) {
		if(args.length == 3) {
			try {
				int autos = Integer.parseInt(args[0]);
				int runden = Integer.parseInt(args[1]);
				int zeitMax = Integer.parseInt(args[2]);
				new SimRace(autos, runden, zeitMax).start();
			} catch(NumberFormatException e) {
				System.out.println("Die Parameter müssen Zahlen sein");
			}
		} else if(args.length == 0) {
			new SimRace(DEFAULT_ANZAHL_AUTOS, DEFAULT_ANZAHL_RUNDEN, DEFAULT_RUNDEN_ZEIT).start();
		} else {
			usage();
		}
	}
}
