package com.rgt.training.models;

public class Patrons {
	private int patronId;
	private String name;

	public Patrons(int patronId, String name) {
		this.patronId = patronId;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public int getPatronId() {
		return patronId;
	}
}
