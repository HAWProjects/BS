package org.haw.bs.praktikum3.mensa;

import java.util.concurrent.locks.ReentrantLock;

public class Kasse {
	private String myName;
	private ReentrantLock myLock;
	private int myWarteschlange;
	
	public Kasse(String name) {
		myName = name;
		myLock = new ReentrantLock(true);
		myWarteschlange = 0;
	}
	
	public String getName() {
		return myName;
	}
	
	public void anstellen() throws InterruptedException {
		System.out.println(Thread.currentThread().getName() + " hat sich an " + myName + " angestellt!");
		myWarteschlange++;
		myLock.lockInterruptibly();
		System.out.println(Thread.currentThread().getName() + " darf an " + myName + " bezahlen!");
//		System.out.println("\t\t\t\t\t\t\t(" + laengeDerSchlange() + ") Studenten an " + myName);
	}
	
	public int laengeDerSchlange() throws InterruptedException {
		return myWarteschlange;
	}
	
	public void bezahlen() {
		System.out.println(Thread.currentThread().getName() + " hat an " + myName + " bezahlt!");
		myWarteschlange--;
		myLock.unlock();
	}
}
