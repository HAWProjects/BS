package org.haw.bs.praktikum3.ssp;

public enum Hand {
	SCHERE,
	STEIN,
	PAPIER;
	
	public boolean schlaegt(Hand other) {
		if(this==SCHERE && other==PAPIER) {
			return true;
		} else if(this==STEIN && other==SCHERE) {
			return true;
		} else if(this==PAPIER && other==STEIN) {
			return true;
		} else {
			return false;
		}
	}
}
