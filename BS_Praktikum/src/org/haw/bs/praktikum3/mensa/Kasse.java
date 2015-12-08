package org.haw.bs.praktikum3.mensa;

import java.util.concurrent.locks.ReentrantLock;

public class Kasse {
	private String myName;
	private ReentrantLock myLock;
	private int myWarteschlange;
	
	public Kasse(String name) {
		myName = name;
		myLock = new ReentrantLock(true);
	}
	
	public String getName() {
		return myName;
	}
	
	public void bezahlen() throws InterruptedException {
		myLock.lockInterruptibly();
		try {
			System.out.println("[* " + laengeDerSchlange() + "] " + Thread.currentThread().getName() + " bezahlt an " + myName + "!");
			Thread.sleep(1000);
		} finally {
			myLock.unlock();
		}
	}
	
	public int laengeDerSchlange() {
		return myWarteschlange;
	}
	
	public void anstellen() {
		myWarteschlange++;
		System.out.println("[+ " + laengeDerSchlange() + "] " + Thread.currentThread().getName() + " hat sich an " + myName + " angestellt!");
	}
	
	public void verlassen() {
		myWarteschlange--;
		System.out.println("[- " + laengeDerSchlange() + "] " + Thread.currentThread().getName() + " hat " + myName + " verlassen!");
	}
}
