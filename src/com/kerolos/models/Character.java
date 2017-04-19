package com.kerolos.models;

import com.kerolos.resources.CommonResources;

public class Character { 

	private String name;//character's name
	private CharacterTraits[] features;//character's features

	public Character(String name, CharacterTraits[] features) {
		this.name = name;

		this.features = new CharacterTraits[CommonResources.NUMBER_OF_FEATURES];
		for (int i = 0; i < CommonResources.NUMBER_OF_FEATURES; i++) {
			this.features[i] = new CharacterTraits(features[i]);
		}
	}

	public Character(Character character) {
		this.name = character.name;

		this.features = new CharacterTraits[CommonResources.NUMBER_OF_FEATURES];
		for (int i = 0; i < CommonResources.NUMBER_OF_FEATURES; i++) {
			this.features[i] = new CharacterTraits(character.features[i]);
		}
	}

	public String getName() {
		return name;
	}

	public CharacterTraits[] getFeatures() {
		return features;
	}
}
