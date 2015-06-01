package eu.haw.bs.prk3;

import java.util.ArrayList;

public class Kasse extends Thread
{
	ArrayList<?> kassenschlange;
	
	public Kasse(){
		this.kassenschlange = new ArrayList<>();
	}
}
