package org.haw.bs.praktikum2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SimRace {
	private static final int DEFAULT_ANZAHL_AUTOS  = 5;
	private static final int DEFAULT_ANZAHL_RUNDEN = 10;
	private static final int DEFAULT_RUNDEN_ZEIT   = 100;
	
	private static void simulateRace(int autos, int runden, int rundenZeitMax) {
		// Autos und Threads initialisieren
		List<Car> cars = new ArrayList<Car>(autos);
		List<Thread> threads = new ArrayList<Thread>(autos);
		for(int i=0; i<autos; i++) {
			Car car = new Car("Auto " + i, runden, rundenZeitMax);
			cars.add(car);
			threads.add(new Thread(car));
		}
		Accident accident = new Accident(threads, rundenZeitMax, rundenZeitMax*runden);
		Thread accidentThread = new Thread(accident);
		
		// Alle Autos losfahren lassen
		accidentThread.start();
		for(Thread thread : threads) {
			thread.start();
		}
		
		// Warten, bis alle Autos fertig sind
		for(Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {}
		}
		accidentThread.interrupt();
		
		if(!accident.isAufgetretenWaehrendRennen()) {
			// Sortieren nach kleinster Gesamtfahrtzeit
			Collections.sort(cars, new Comparator<Car>() {
				@Override
				public int compare(Car o1, Car o2) {
					return (int)(o1.getGesamtfahrtzeit()-o2.getGesamtfahrtzeit());
				}
			});
			
			// Ergebnisausgabe
			System.out.println("***** Endstand *****");
			for(int i=0; i<cars.size(); i++) {
				Car car = cars.get(i);
				System.out.println((i+1) + ". Platz: " + car.getName() + " (" + car.getGesamtfahrtzeit() + ")");
			}
		} else {
			System.out.println("Das Rennen musste aufgrund eines Unfalls abgebrochen werden!");
		}
	}
	
	private static void usage() {
		System.out.println(
				SimRace.class.getSimpleName() + " [AUTOS, RUNDEN, ZEITMAX]\n" +
				"\n" +
				" AUTOS\tDie Anzahl der zu simulierenden Autos\n" +
				" RUNDEN\tDie Anzahl der zu fahrenden Runden\n" +
				" ZEITMAX\tDie maximale Zeit, die ein Auto f�r eine Runde braucht\n"
		);
	}
	
	public static void main(String... args) {
		if(args.length == 3) {
			try {
				int autos = Integer.parseInt(args[0]);
				int runden = Integer.parseInt(args[1]);
				int zeitMax = Integer.parseInt(args[2]);
				simulateRace(autos, runden, zeitMax);
			} catch(NumberFormatException e) {
				System.out.println("Die Parameter m�ssen Zahlen sein");
			}
		} else if(args.length == 0) {
			simulateRace(DEFAULT_ANZAHL_AUTOS, DEFAULT_ANZAHL_RUNDEN, DEFAULT_RUNDEN_ZEIT);
		} else {
			usage();
		}
	}
}
