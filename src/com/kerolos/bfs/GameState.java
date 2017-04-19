package com.kerolos.bfs;

import java.util.ArrayList;

import com.kerolos.models.CharacterTraits;
import com.kerolos.models.Character;

//This class represents the game state for the BFS search
public class GameState {

	private ArrayList<Character> characters;// store the list of characters
	private CharacterTraits chosenQuestion;//store the question that is being asked in this node
	private ArrayList<GameState> prevStates;//store the prevStates
	private ArrayList<CharacterTraits> questions;//store the list of all the available questions
	private String answer;//store the answer to the question

	public GameState(ArrayList<Character> characters, CharacterTraits chosenQuestion,
			ArrayList<CharacterTraits> questions, GameState prevState) {
		this.characters = characters;
		this.chosenQuestion = chosenQuestion;
		this.questions = questions;

		this.prevStates = new ArrayList<GameState>();
		for (int i = 0; i < prevState.prevStates.size(); i++) {
			this.prevStates.add(prevState.prevStates.get(i));//add all previous states
		}
		this.prevStates.add(prevState);//add the last previous state
	}

	public GameState(ArrayList<Character> characters) {
		this.characters = characters;
		prevStates = new ArrayList<GameState>();
		questions = new ArrayList<CharacterTraits>();
		chosenQuestion = null;
	}

	public ArrayList<Character> getCharacters() {
		return characters;
	}

	public ArrayList<CharacterTraits> getQuestions() {
		return questions;
	}

	public ArrayList<GameState> getPrevStates() {
		return prevStates;
	}

	public CharacterTraits getChosenQuestion() {
		return chosenQuestion;
	}

	public void setQuesions(ArrayList<CharacterTraits> questions) {
		this.questions = questions;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getAnswer() {
		return answer;
	}
}
