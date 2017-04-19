package com.kerolos.models;

import java.util.ArrayList;

public class Player {

	private Character hiddenCharacter;//hidden character that the opposing player has to guess to win the game
	private ArrayList<Character> validCharacters;//characters that are left to guess

	public Player(Character hiddenCharacter, Character[] characters) {
		this.hiddenCharacter = hiddenCharacter;
		validCharacters = new ArrayList<Character>();
		for (int i = 0; i < characters.length; i++) {
			if (!characters[i].getName().equals(hiddenCharacter.getName())) {
				validCharacters.add(characters[i]);
			}
		}
	}

	public ArrayList<Character> getValidCharacters() {
		return validCharacters;
	}

	public Character getHiddenCharacter() {
		return hiddenCharacter;
	}
}
