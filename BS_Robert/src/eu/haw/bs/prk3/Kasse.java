package eu.haw.bs.prk3;

import java.util.concurrent.locks.ReentrantLock;

public class Kasse {

	private String name;
	private ReentrantLock bezahlLock;

	private int warteschlange;

	// private Semaphore warteSema;
	public Kasse(String name) {

		this.bezahlLock = new ReentrantLock();

		this.setName(name);
		this.warteschlange = 0;
	}

	/**
	 * @throws InterruptedException
	 * 
	 */
	public void sichInDerSchlangeAnStellen() throws InterruptedException {

		// Student versucht zu bezahlen, falls er in der Reihe ist.
		bezahlLock.lock();
		if (!Thread.currentThread().isInterrupted()) {
			// Bezahlen
			System.out.println("                                                         " + Thread.currentThread().getName() + " darf bezahlen! ");

			//
			try {
				//
				bezahlt();
			} catch (InterruptedException e) {
				bezahlLock.unlock();
				throw new InterruptedException();
			}

			//
			System.out.println("                                                                                     " + Thread.currentThread().getName()
					+ " hat bezahlt und die Kasse verlassen!");
		}
		//

		bezahlLock.unlock();

	}

	public void bezahlt() throws InterruptedException {
		// int dauer = (int) (1000 * Math.random());
		int dauer = 300;
		Thread.sleep(dauer);
	}

	public String getName() {
		return name;
	}

	public synchronized void setName(String name) {
		this.name = name;
	}

	public int warteSchlangeLaenge() {
		return warteschlange;
	}

	public void aendernWarteschlangeLaenge(int aenderung) {

		this.warteschlange += aenderung;
	}
}
