package eu.haw.bs.prk2;

import java.util.List;
import java.util.Random;

public class Accident extends Thread {
	private Random rand;
	private List<Car> carList;

	public Accident(List<Car> carList) {
		rand = new Random();
		this.carList = carList;
	}

	public void run() {
		int accidentTime = rand.nextInt(Start_Race.laps * 100);
		try {
			Thread.sleep(accidentTime);
			System.err.println("accident");
			for (Car car : carList) {
				car.interrupt();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
