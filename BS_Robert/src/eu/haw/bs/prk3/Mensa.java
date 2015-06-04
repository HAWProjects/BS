package eu.haw.bs.prk3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Mensa {

	private final int KASSENANZAHL;

	private ReentrantLock kassenLock;

	private List<Kasse> kassenListe;


	public Mensa(int kassenAnzahl) {
		KASSENANZAHL = kassenAnzahl;

		this.kassenLock = new ReentrantLock();

		this.kassenListe = new ArrayList<Kasse>();

		for (int i = 1; i <= KASSENANZAHL; i++) {
			Kasse kasse = new Kasse("Kasse " + i);
			kassenListe.add(kasse);
		}

	}

	public void StarteSimulation(int studentenAnzahl) {

		List<Student> studentenListe = new ArrayList<Student>();

		System.err.println("-------------------- START -------------------");

		//erzeuget StudentenThrread und übergibt Mensa
		for (int i = 0; i < studentenAnzahl; i++) {

			Student student = new Student(this);
			student.setName("Student " + (i+1));
			studentenListe.add(student);
			student.start();

		}

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
		}

		System.err.println("--------------------ENDE-------------------");

		for (Student student : studentenListe) {
			student.interrupt();
		}

	}

	public List<Kasse> gibKassenListe() {

		return this.kassenListe;

	}

	public Kasse gibkuerzestenWarteschlange() {
		kassenLock.lock();
		Kasse minKasse = null;
		int min = Integer.MAX_VALUE;
		List<Kasse> kl = gibKassenListe();

		for (Kasse kasse : kl) {
			int schlangeLaenge = kasse.warteSchlangeLaenge();

			if (schlangeLaenge < min) {
				min = schlangeLaenge;
				minKasse = kasse;
			}
		}

		minKasse.aendernWarteschlangeLaenge(1);

		System.out.println(Thread.currentThread().getName() + " ist bei "
				+ minKasse.getName() + " mit Laenge: "
				+ minKasse.warteSchlangeLaenge());
		kassenLock.unlock();
		return minKasse;

	}

	public void aendernWarteschlangeLaenge(Kasse kasse) {

		kassenLock.lock();

		kasse.aendernWarteschlangeLaenge(-1);

		kassenLock.unlock();
	}

	public static void main(String[] args) {
		(new Mensa(3)).StarteSimulation(15);
	}

}
