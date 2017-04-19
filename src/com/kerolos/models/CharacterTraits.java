package com.kerolos.models;

public class CharacterTraits {

	//character trait/feature value and name (i.e. name = hairColor, value = Black)
	private String name;
	private String value;

	public CharacterTraits(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public CharacterTraits(CharacterTraits characterTrait) {
		this.name = characterTrait.name;
		this.value = characterTrait.value;
	}

	public String getname() {
		return name;
	}

	public String getvalue() {
		return value;
	}
}
