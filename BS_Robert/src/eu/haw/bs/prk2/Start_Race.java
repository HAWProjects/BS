package eu.haw.bs.prk2;

import java.util.ArrayList;
import java.util.Collections;

public class Start_Race {
	public static final int countCar = 5;
	public static final int laps = 12;

	private static ArrayList<Car> carList;

	public static void main(String[] args) throws InterruptedException {
		carList = new ArrayList<>();

		for (int i = 1; i <= countCar; i++) {
			Car car = new Car("Wagen:" + i);

			car.start();
			carList.add(car);
		}
		
		Accident ac = new Accident(carList);
		ac.start();

		// check ob theards beendet
		for (int i = 0; i < carList.size(); i++) {
			carList.get(i).join();
		}
		
		// Liste sortieren reihenfolge rennzeit!
		Collections.sort(carList);

		boolean accident = false;
		// ckeck ob alle Autos Ziel erreicht
		for (Car car : carList) {
			if (!car.isFinished()) {
				accident = true;
			}
		}
		System.out.println(accident);
		if (!accident) {
			// Ergebnis ausgeben
			for (int j = 1; j <= countCar; ++j) {
				System.out.println(j + ". " + "Platz: " + carList.get(j - 1).getCarName() + "  Zeit: " + carList.get(j - 1).getRaceTime());
			}
		} else {
			System.out.println("Das Rennen musste wegen eines Unfalls abgebrochen werden!");
		}
	}
}
