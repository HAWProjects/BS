package eu.haw.bs.prk2;

import java.util.Random;

public class Car extends Thread implements Comparable<Car> {
	private Random rand;
	private long raceTime;
	private String carName;
	private boolean reachedFinish;

	public Car(String name) {
		rand = new Random();
		raceTime = 0;
		carName = name;
	}

	public void run() {
		for (int i = 0; i < Start_Race.laps && !isInterrupted(); ++i) {
			int lapTime = rand.nextInt(100);
			raceTime = raceTime + lapTime;
			try {
				System.out.println(this.getCarName() + " schlaeft");
				Thread.sleep(lapTime);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				System.out.println("Thread wurde unterbrochen: interrupt");
			}
		}
		reachedFinish = true;
		System.err.println(this.getName() + " beendet");
	}

	public long getRaceTime() {
		return raceTime;
	}

	public String getCarName() {
		return carName;
	}
	public boolean isFinished(){
		return reachedFinish;
	}

	@Override
	public int compareTo(Car otherCar) {
		return (int) (this.raceTime - otherCar.raceTime);
		// int result = 0;
		// if (this.raceTime < otherCar.raceTime)
		// {
		// result = -1;
		// }
		// else if (this.raceTime == otherCar.raceTime)
		// {
		// result = 0;
		// }
		// else if (this.raceTime > otherCar.raceTime)
		// {
		// result = 1;
		// }
		// return result;
	}
}