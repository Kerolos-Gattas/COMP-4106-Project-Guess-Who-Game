package com.kerolos.bfs;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import com.kerolos.models.CharacterTraits;
import com.kerolos.models.Character;

//Class Function: this class finds an optimal solution to the guess who game using BFS search
public class BFS {

	private Queue<GameState> q; // queue to store bfs game states
	private ArrayList<CharacterTraits> questions; // a list of all the unique questions that the AI can ask
	private GameState goalState;
	private Character goalCharacter;

	public BFS(Character goalCharacter) {
		q = new LinkedList<GameState>();
		questions = new ArrayList<CharacterTraits>();
		// In order to solve the bfs graph we have to know the goal we are
		// trying to reach, thus we are storing the goal here.
		this.goalCharacter = goalCharacter;
	}

	public void buildBFStree(GameState state) {

		intializeQuestions(state.getCharacters());// gather all the unique questions
													
		state.setQuesions(questions);
		q.add(state);

		while (!q.isEmpty()) {
			GameState tempState = q.remove();// remove the first game state and process it
												
			if (nextState(tempState)) {// generate next game states and check if goal is reached										 
				break;
			}
		}
	}

	// Generate all the available next states for the game. Each state contains
	// a different question that the AI can ask with its results
	private boolean nextState(GameState state) {

		for (int i = 0; i < state.getQuestions().size(); i++) {// loop through all the available questions

			// questions and characters for the new game state
			ArrayList<CharacterTraits> tempQuestions = new ArrayList<CharacterTraits>(state.getQuestions());
			ArrayList<Character> tempCharacters = new ArrayList<Character>(state.getCharacters());

			String answer; // store the answer for the question here

			if (checkQuestion(state.getQuestions().get(i))) {// check to see if the feature of the goal character matches the question (i.e. black hair)
				removeFalseFeatures(state.getQuestions().get(i), tempCharacters);//remove all the characters that do not have the feature (i.e. not black hair)
				answer = "Yes";
			} else {
				removeTrueFeatures(state.getQuestions().get(i), tempCharacters);// remove all the characters that have the feature (i.e. black hair)
				answer = "No";
			}

			tempQuestions.remove(i); // remove the question that was asked

			GameState tempState = new GameState(tempCharacters, state.getQuestions().get(i), tempQuestions, state);// create new game state 
			tempState.setAnswer(answer);
			q.add(tempState);// add new state to the queue
 
			if (tempState.getCharacters().size() == 1) {// if there is only one character left then we have reached the goal
				goalState = tempState;
				return true;
			}

		}

		return false;
	}

	// remove all characters that do not match the feature in the question
	private void removeFalseFeatures(CharacterTraits question, ArrayList<Character> charactersLeft) {

		// store characters that are going to be removed, this has to be done
		// since it does not work if we remove it during the loop bec all the
		// elements will shift
		ArrayList<Character> tempCharacters = new ArrayList<Character>();

		for (int i = 0; i < charactersLeft.size(); i++) {
			for (int j = 0; j < charactersLeft.get(i).getFeatures().length; j++) {
				if (charactersLeft.get(i).getFeatures()[j].getname().equals(question.getname())) {
					if (!charactersLeft.get(i).getFeatures()[j].getvalue().equals(question.getvalue())) {// if feature does not match the question then remove character
						tempCharacters.add(charactersLeft.get(i));
					}
					break;
				}
			}
		}

		for (int i = 0; i < tempCharacters.size(); i++) {
			charactersLeft.remove(tempCharacters.get(i));
		}
	}

	// remove all characters that match the feature in the question
	private void removeTrueFeatures(CharacterTraits question, ArrayList<Character> charactersLeft) {

		// store characters that are going to be removed, this has to be done
		// since it does not work if we remove it during the loop bec all the
		// elements will shift
		ArrayList<Character> tempCharacters = new ArrayList<Character>();

		for (int i = 0; i < charactersLeft.size(); i++) {
			for (int j = 0; j < charactersLeft.get(i).getFeatures().length; j++) {
				if (charactersLeft.get(i).getFeatures()[j].getname().equals(question.getname())) {
					if (charactersLeft.get(i).getFeatures()[j].getvalue().equals(question.getvalue())) {// if the feature matches the question then remove character 
						tempCharacters.add(charactersLeft.get(i));
					}
					break;
				}
			}
		}

		for (int i = 0; i < tempCharacters.size(); i++) {
			charactersLeft.remove(tempCharacters.get(i));
		}
	}

	// Determine the answer for the question being asked
	private boolean checkQuestion(CharacterTraits question) {

		for (int i = 0; i < goalCharacter.getFeatures().length; i++) {
			if (goalCharacter.getFeatures()[i].getname().equals(question.getname())) {
				if (goalCharacter.getFeatures()[i].getvalue().equals(question.getvalue())) {// if the feature of the goal character matches the question return true
					return true;
				}
				break;
			}
		}

		return false;
	}

	// Loop through all the characters and generate the unique set of questions
	// that the AI can ask
	private void intializeQuestions(ArrayList<Character> characters) {
		for (int i = 0; i < characters.size(); i++) {
			for (int j = 0; j < characters.get(i).getFeatures().length; j++) {
				if (!containsQuestion(characters.get(i).getFeatures()[j])) {
					questions.add(characters.get(i).getFeatures()[j]);
				}
			}
		}
	}

	// check if the set of questions already contains the feature
	private boolean containsQuestion(CharacterTraits feature) {
		for (int i = 0; i < questions.size(); i++) {
			if (questions.get(i).getname().equals(feature.getname())) {
				if (questions.get(i).getvalue().equals(feature.getvalue()) || questions.get(i).getvalue().equals("Yes")
						|| questions.get(i).getvalue().equals("No")) {//Since yes and no features are the same question reversed, we only include one of them
																	  //(i.e. has beard vs does not have a beard both will return the same set of characters)
					return true;
				}
			}
		}
		return false;
	}

	public GameState getGoalState() {
		return goalState;
	}
}
