package eu.haw.bs.prk3;


public class Student extends Thread {

	//
	private Mensa mensa;

	/**
	 * 
	 * 
	 * @param kassenListe
	 */
	public Student(Mensa mensa) {
		this.mensa = mensa;
	}

	@Override
	public void run() {
		try {
			while (!isInterrupted()) {

				Kasse kasse = this.mensa.gibkuerzestenWarteschlange();

				//
				kasse.sichInDerSchlangeAnStellen();
				
				//
				mensa.aendernWarteschlangeLaenge(kasse);
				
				//
				essenUndWeiterLeben();
			}
		} catch (InterruptedException e) {
			System.err.println(this.getName() + " wurde unterbrochen!");
		}

	}

	public void essenUndWeiterLeben() throws InterruptedException {

		int dauer = (int) (1000 * Math.random());

		Thread.sleep(dauer);

	}
	

}
