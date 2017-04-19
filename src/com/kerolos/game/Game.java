package com.kerolos.game;

import java.util.ArrayList;
import java.util.Random;

import com.kerolos.models.Player;
import com.kerolos.resources.CommonResources;
import com.kerolos.bfs.BFS;
import com.kerolos.bfs.GameState;
import com.kerolos.decisionTree.Decisiontree;
import com.kerolos.decisionTree.SampleData;
import com.kerolos.models.Character;
import com.kerolos.models.CharacterTraits;

//This class handles the game flow and the AI techniques
public class Game {

	private Player player1;
	private Player player2;

	// stores all the characters in the game
	private Character[] characters;

	// sample data for the decision tree
	private SampleData sampleData;
	private Decisiontree player1decisionTree;

	// BFS variables
	private BFS bfs;
	private GameState intialState;
	private boolean bfsGuessCharacter;

	private boolean gameOver;

	public Game() {
		characters = new Character[CommonResources.NUMBER_OF_CHARACTERS];
		initializeCharacters();
		initializePlayers();
		sampleData = new SampleData();
		player1decisionTree = new Decisiontree(this.player1.getValidCharacters());
		bfs = new BFS(this.player1.getHiddenCharacter());
		intialState = new GameState(this.player2.getValidCharacters());
		gameOver = false;
		bfsGuessCharacter = false;
	}

	public void StartGame() {
		AIvsAI();
	}

	private void AIvsAI() {
		//generate sample data for the decision tree
		sampleData.generateSampleData(player1.getValidCharacters());
		player1decisionTree.buildDecisiontree(sampleData.getSampleData(), sampleData.getPositiveThreshold());

		//solve the game using bfs
		bfs.buildBFStree(intialState);
		ArrayList<GameState> goalStates = new ArrayList<GameState>();
		goalStates.addAll(bfs.getGoalState().getPrevStates());
		goalStates.add(bfs.getGoalState());
		
		int index = 1;//this index is used to traverse the bfs tree to reach the goal

		//each player takes one turn in asking a question or guessing a character
		while (!gameOver) {
			player1Turn();

			if(gameOver)//if player 1 wins stop the game
				break;
			
			//if the goal character is reached then guess the character else ask a question
			if (!bfsGuessCharacter)
				player2Turn(goalStates, index);
			else
				bfsGuessCharacter(goalStates, index - 1);
			
			index++;
		}
	}

	private void player1Turn() {

		//guess the character
		if (player1.getValidCharacters().size() == 1) {
			System.out.println(
					"\nPlayer 1 is guessing the character is: " + player1.getValidCharacters().get(0).getName());
			System.out.println("The right character is: " + player2.getHiddenCharacter().getName());
			if (player1.getValidCharacters().get(0).getName().equals(player2.getHiddenCharacter().getName())) {
				System.out.println("Player 1 guessed correctly");
				System.out.println("Game Over, Player 1 won");
			} else {
				System.out.println("Player 1 guessed wrong");
				System.out.println("Game Over, Player 2 won");
			}
			gameOver = true;
			return;
		}

		//ask a question
		System.out.print("Player 1 asks: " + player1decisionTree.getRoot().getChosenQuestion().getname() + ":"
				+ player1decisionTree.getRoot().getChosenQuestion().getvalue());

		for (int i = 0; i < player2.getHiddenCharacter().getFeatures().length; i++) {

			//find the answer to the question and remove the appropriate characters
			if (player1decisionTree.getRoot().getChosenQuestion().getname()
					.equals(player2.getHiddenCharacter().getFeatures()[i].getname())) {

				if (player1decisionTree.getRoot().getChosenQuestion().getvalue()
						.equals(player2.getHiddenCharacter().getFeatures()[i].getvalue())) {

					removeFalseFeatures(player1decisionTree.getRoot().getChosenQuestion(),
							player1.getValidCharacters());
					player1decisionTree.traverseTree("Yes");
					System.out.println(", Answer is Yes");

				} else {
					removeTrueFeatures(player1decisionTree.getRoot().getChosenQuestion(), player1.getValidCharacters());
					player1decisionTree.traverseTree("No");
					System.out.println(", Answer is No");
				}
				break;
			}

		}
	}

	//Travers the bfs tree and ask questions
	private void player2Turn(ArrayList<GameState> goalStates, int index) {

		System.out.print("Player 2 asks: " + goalStates.get(index).getChosenQuestion().getname() + ":"
				+ goalStates.get(index).getChosenQuestion().getvalue());
		System.out.println(", Answer is " + goalStates.get(index).getAnswer());
		if (goalStates.get(index).getCharacters().size() == 1)//if there is one character left then next turn guess the character
			bfsGuessCharacter = true;

	}

	//guess the character
	private void bfsGuessCharacter(ArrayList<GameState> goalStates, int index) {
		if (goalStates.get(index).getCharacters().size() == 1) {
			System.out.println("\nPlayer 2 is guessing the character is: "
					+ goalStates.get(index).getCharacters().get(0).getName());
			System.out.println("The right character is: " + player1.getHiddenCharacter().getName());
			if (goalStates.get(index).getCharacters().get(0).getName().equals(player1.getHiddenCharacter().getName())) {
				System.out.println("Player 2 guessed correctly");
				System.out.println("Game Over, Player 2 won");
			} else {
				System.out.println("Player 2 guessed wrong");
				System.out.println("Game Over, Player 1 won");
			}
			gameOver = true;
			return;
		}
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
					if (!charactersLeft.get(i).getFeatures()[j].getvalue().equals(question.getvalue())) {
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
					if (charactersLeft.get(i).getFeatures()[j].getvalue().equals(question.getvalue())) {
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


	private void initializePlayers() {
		Random rand = new Random();
		int n1 = rand.nextInt(23) + 0;
		int n2 = rand.nextInt(23) + 0;
		while (n2 == n1) {
			n2 = rand.nextInt(23) + 0;
		}
		
		//pick a random hidden character for each player that the other player has to guess to win the game
		this.player1 = new Player(characters[n1], characters);
		this.player2 = new Player(characters[n2], characters);
		System.out.println("Player 1's hidden character: " + characters[n1].getName());
		System.out.println("Player 2's hidden character: " + characters[n2].getName() + "\n");

	}

	//intialize the features of all the characters
	private void initializeCharacters() {
		CharacterTraits[] tempTraits = new CharacterTraits[CommonResources.NUMBER_OF_FEATURES];

		tempTraits[0] = new CharacterTraits("hairColor", "Black");
		tempTraits[1] = new CharacterTraits("hairLength", "Short");
		tempTraits[2] = new CharacterTraits("eyeColor", "Black");
		tempTraits[3] = new CharacterTraits("noseSize", "Large");
		tempTraits[4] = new CharacterTraits("mask", "No");
		tempTraits[5] = new CharacterTraits("lipSize", "Medium");
		tempTraits[6] = new CharacterTraits("glasses", "No");
		tempTraits[7] = new CharacterTraits("facialHair", "Yes");
		tempTraits[8] = new CharacterTraits("foreheadSize", "Large");
		tempTraits[9] = new CharacterTraits("moustache", "Yes");
		tempTraits[10] = new CharacterTraits("beard", "Yes");
		characters[0] = new Character("Paul", tempTraits);

		tempTraits[0] = new CharacterTraits("hairColor", "Blonde");
		tempTraits[1] = new CharacterTraits("hairLength", "Long");
		tempTraits[2] = new CharacterTraits("eyeColor", "Blue");
		tempTraits[3] = new CharacterTraits("noseSize", "Medium");
		tempTraits[4] = new CharacterTraits("mask", "No");
		tempTraits[5] = new CharacterTraits("lipSize", "Medium");
		tempTraits[6] = new CharacterTraits("glasses", "No");
		tempTraits[7] = new CharacterTraits("facialHair", "No");
		tempTraits[8] = new CharacterTraits("foreheadSize", "Small");
		tempTraits[9] = new CharacterTraits("moustache", "No");
		tempTraits[10] = new CharacterTraits("beard", "No");
		characters[1] = new Character("Jody", tempTraits);

		tempTraits[0] = new CharacterTraits("hairColor", "Not Valid");
		tempTraits[1] = new CharacterTraits("hairLength", "Bald");
		tempTraits[2] = new CharacterTraits("eyeColor", "Black");
		tempTraits[3] = new CharacterTraits("noseSize", "Large");
		tempTraits[4] = new CharacterTraits("mask", "No");
		tempTraits[5] = new CharacterTraits("lipSize", "Large");
		tempTraits[6] = new CharacterTraits("glasses", "No");
		tempTraits[7] = new CharacterTraits("facialHair", "Yes");
		tempTraits[8] = new CharacterTraits("foreheadSize", "Large");
		tempTraits[9] = new CharacterTraits("moustache", "No");
		tempTraits[10] = new CharacterTraits("beard", "No");
		characters[2] = new Character("Marcellus", tempTraits);

		tempTraits[0] = new CharacterTraits("hairColor", "Brown");
		tempTraits[1] = new CharacterTraits("hairLength", "Short");
		tempTraits[2] = new CharacterTraits("eyeColor", "Brown");
		tempTraits[3] = new CharacterTraits("noseSize", "Medium");
		tempTraits[4] = new CharacterTraits("mask", "No");
		tempTraits[5] = new CharacterTraits("lipSize", "Small");
		tempTraits[6] = new CharacterTraits("glasses", "No");
		tempTraits[7] = new CharacterTraits("facialHair", "No");
		tempTraits[8] = new CharacterTraits("foreheadSize", "Large");
		tempTraits[9] = new CharacterTraits("moustache", "No");
		tempTraits[10] = new CharacterTraits("beard", "No");
		characters[3] = new Character("Jimmie", tempTraits);

		tempTraits[0] = new CharacterTraits("hairColor", "Brown");
		tempTraits[1] = new CharacterTraits("hairLength", "Long");
		tempTraits[2] = new CharacterTraits("eyeColor", "Brown");
		tempTraits[3] = new CharacterTraits("noseSize", "Small");
		tempTraits[4] = new CharacterTraits("mask", "No");
		tempTraits[5] = new CharacterTraits("lipSize", "Medium");
		tempTraits[6] = new CharacterTraits("glasses", "No");
		tempTraits[7] = new CharacterTraits("facialHair", "No");
		tempTraits[8] = new CharacterTraits("foreheadSize", "Medium");
		tempTraits[9] = new CharacterTraits("moustache", "No");
		tempTraits[10] = new CharacterTraits("beard", "No");
		characters[4] = new Character("Esmeralda", tempTraits);

		tempTraits[0] = new CharacterTraits("hairColor", "Black");
		tempTraits[1] = new CharacterTraits("hairLength", "Short");
		tempTraits[2] = new CharacterTraits("eyeColor", "Blue");
		tempTraits[3] = new CharacterTraits("noseSize", "Small");
		tempTraits[4] = new CharacterTraits("mask", "No");
		tempTraits[5] = new CharacterTraits("lipSize", "Small");
		tempTraits[6] = new CharacterTraits("glasses", "No");
		tempTraits[7] = new CharacterTraits("facialHair", "No");
		tempTraits[8] = new CharacterTraits("foreheadSize", "Large");
		tempTraits[9] = new CharacterTraits("moustache", "No");
		tempTraits[10] = new CharacterTraits("beard", "No");
		characters[5] = new Character("Captain Koons", tempTraits);

		tempTraits[0] = new CharacterTraits("hairColor", "Black");
		tempTraits[1] = new CharacterTraits("hairLength", "Short");
		tempTraits[2] = new CharacterTraits("eyeColor", "Brown");
		tempTraits[3] = new CharacterTraits("noseSize", "Large");
		tempTraits[4] = new CharacterTraits("mask", "No");
		tempTraits[5] = new CharacterTraits("lipSize", "Large");
		tempTraits[6] = new CharacterTraits("glasses", "No");
		tempTraits[7] = new CharacterTraits("facialHair", "Yes");
		tempTraits[8] = new CharacterTraits("foreheadSize", "Small");
		tempTraits[9] = new CharacterTraits("moustache", "Yes");
		tempTraits[10] = new CharacterTraits("beard", "Yes");
		characters[6] = new Character("Jules", tempTraits);

		tempTraits[0] = new CharacterTraits("hairColor", "Black");
		tempTraits[1] = new CharacterTraits("hairLength", "Short");
		tempTraits[2] = new CharacterTraits("eyeColor", "Blue");
		tempTraits[3] = new CharacterTraits("noseSize", "Small");
		tempTraits[4] = new CharacterTraits("mask", "No");
		tempTraits[5] = new CharacterTraits("lipSize", "Medium");
		tempTraits[6] = new CharacterTraits("glasses", "Yes");
		tempTraits[7] = new CharacterTraits("facialHair", "No");
		tempTraits[8] = new CharacterTraits("foreheadSize", "Medium");
		tempTraits[9] = new CharacterTraits("moustache", "No");
		tempTraits[10] = new CharacterTraits("beard", "No");
		characters[7] = new Character("Buddy", tempTraits);

		tempTraits[0] = new CharacterTraits("hairColor", "Not Valid");
		tempTraits[1] = new CharacterTraits("hairLength", "Not Valid");
		tempTraits[2] = new CharacterTraits("eyeColor", "Blue");
		tempTraits[3] = new CharacterTraits("noseSize", "Medium");
		tempTraits[4] = new CharacterTraits("mask", "Yes");
		tempTraits[5] = new CharacterTraits("lipSize", "Not Valid");
		tempTraits[6] = new CharacterTraits("glasses", "No");
		tempTraits[7] = new CharacterTraits("facialHair", "No");
		tempTraits[8] = new CharacterTraits("foreheadSize", "Not Valid");
		tempTraits[9] = new CharacterTraits("moustache", "No");
		tempTraits[10] = new CharacterTraits("beard", "No");
		characters[8] = new Character("The Gimp", tempTraits);

		tempTraits[0] = new CharacterTraits("hairColor", "Orange");
		tempTraits[1] = new CharacterTraits("hairLength", "Long");
		tempTraits[2] = new CharacterTraits("eyeColor", "Black");
		tempTraits[3] = new CharacterTraits("noseSize", "Medium");
		tempTraits[4] = new CharacterTraits("mask", "No");
		tempTraits[5] = new CharacterTraits("lipSize", "Medium");
		tempTraits[6] = new CharacterTraits("glasses", "No");
		tempTraits[7] = new CharacterTraits("facialHair", "No");
		tempTraits[8] = new CharacterTraits("foreheadSize", "Small");
		tempTraits[9] = new CharacterTraits("moustache", "No");
		tempTraits[10] = new CharacterTraits("beard", "No");
		characters[9] = new Character("Yolanda", tempTraits);

		tempTraits[0] = new CharacterTraits("hairColor", "Brown");
		tempTraits[1] = new CharacterTraits("hairLength", "Long");
		tempTraits[2] = new CharacterTraits("eyeColor", "Black");
		tempTraits[3] = new CharacterTraits("noseSize", "Medium");
		tempTraits[4] = new CharacterTraits("mask", "No");
		tempTraits[5] = new CharacterTraits("lipSize", "Small");
		tempTraits[6] = new CharacterTraits("glasses", "No");
		tempTraits[7] = new CharacterTraits("facialHair", "No");
		tempTraits[8] = new CharacterTraits("foreheadSize", "Small");
		tempTraits[9] = new CharacterTraits("moustache", "No");
		tempTraits[10] = new CharacterTraits("beard", "No");
		characters[10] = new Character("Roger", tempTraits);

		tempTraits[0] = new CharacterTraits("hairColor", "Black");
		tempTraits[1] = new CharacterTraits("hairLength", "Long");
		tempTraits[2] = new CharacterTraits("eyeColor", "Blue");
		tempTraits[3] = new CharacterTraits("noseSize", "Medium");
		tempTraits[4] = new CharacterTraits("mask", "No");
		tempTraits[5] = new CharacterTraits("lipSize", "Small");
		tempTraits[6] = new CharacterTraits("glasses", "No");
		tempTraits[7] = new CharacterTraits("facialHair", "No");
		tempTraits[8] = new CharacterTraits("foreheadSize", "Small");
		tempTraits[9] = new CharacterTraits("moustache", "No");
		tempTraits[10] = new CharacterTraits("beard", "No");
		characters[11] = new Character("Fabienne", tempTraits);

		tempTraits[0] = new CharacterTraits("hairColor", "Brown");
		tempTraits[1] = new CharacterTraits("hairLength", "Short");
		tempTraits[2] = new CharacterTraits("eyeColor", "Blue");
		tempTraits[3] = new CharacterTraits("noseSize", "Medium");
		tempTraits[4] = new CharacterTraits("mask", "No");
		tempTraits[5] = new CharacterTraits("lipSize", "Small");
		tempTraits[6] = new CharacterTraits("glasses", "No");
		tempTraits[7] = new CharacterTraits("facialHair", "No");
		tempTraits[8] = new CharacterTraits("foreheadSize", "Medium");
		tempTraits[9] = new CharacterTraits("moustache", "No");
		tempTraits[10] = new CharacterTraits("beard", "No");
		characters[12] = new Character("Ringo", tempTraits);

		tempTraits[0] = new CharacterTraits("hairColor", "Brown");
		tempTraits[1] = new CharacterTraits("hairLength", "Long");
		tempTraits[2] = new CharacterTraits("eyeColor", "Blue");
		tempTraits[3] = new CharacterTraits("noseSize", "Small");
		tempTraits[4] = new CharacterTraits("mask", "No");
		tempTraits[5] = new CharacterTraits("lipSize", "Medium");
		tempTraits[6] = new CharacterTraits("glasses", "No");
		tempTraits[7] = new CharacterTraits("facialHair", "No");
		tempTraits[8] = new CharacterTraits("foreheadSize", "Small");
		tempTraits[9] = new CharacterTraits("moustache", "No");
		tempTraits[10] = new CharacterTraits("beard", "No");
		characters[13] = new Character("Raquel", tempTraits);

		tempTraits[0] = new CharacterTraits("hairColor", "Orange");
		tempTraits[1] = new CharacterTraits("hairLength", "Long");
		tempTraits[2] = new CharacterTraits("eyeColor", "Blue");
		tempTraits[3] = new CharacterTraits("noseSize", "Small");
		tempTraits[4] = new CharacterTraits("mask", "No");
		tempTraits[5] = new CharacterTraits("lipSize", "Medium");
		tempTraits[6] = new CharacterTraits("glasses", "No");
		tempTraits[7] = new CharacterTraits("facialHair", "Yes");
		tempTraits[8] = new CharacterTraits("foreheadSize", "Medium");
		tempTraits[9] = new CharacterTraits("moustache", "Yes");
		tempTraits[10] = new CharacterTraits("beard", "Yes");
		characters[14] = new Character("Lance", tempTraits);

		tempTraits[0] = new CharacterTraits("hairColor", "Grey");
		tempTraits[1] = new CharacterTraits("hairLength", "Short");
		tempTraits[2] = new CharacterTraits("eyeColor", "Blue");
		tempTraits[3] = new CharacterTraits("noseSize", "Small");
		tempTraits[4] = new CharacterTraits("mask", "No");
		tempTraits[5] = new CharacterTraits("lipSize", "Small");
		tempTraits[6] = new CharacterTraits("glasses", "No");
		tempTraits[7] = new CharacterTraits("facialHair", "No");
		tempTraits[8] = new CharacterTraits("foreheadSize", "Large");
		tempTraits[9] = new CharacterTraits("moustache", "No");
		tempTraits[10] = new CharacterTraits("beard", "No");
		characters[15] = new Character("Butch", tempTraits);

		tempTraits[0] = new CharacterTraits("hairColor", "Black");
		tempTraits[1] = new CharacterTraits("hairLength", "Long");
		tempTraits[2] = new CharacterTraits("eyeColor", "Blue");
		tempTraits[3] = new CharacterTraits("noseSize", "Small");
		tempTraits[4] = new CharacterTraits("mask", "No");
		tempTraits[5] = new CharacterTraits("lipSize", "Small");
		tempTraits[6] = new CharacterTraits("glasses", "No");
		tempTraits[7] = new CharacterTraits("facialHair", "No");
		tempTraits[8] = new CharacterTraits("foreheadSize", "Small");
		tempTraits[9] = new CharacterTraits("moustache", "No");
		tempTraits[10] = new CharacterTraits("beard", "No");
		characters[16] = new Character("Mia", tempTraits);

		tempTraits[0] = new CharacterTraits("hairColor", "Black");
		tempTraits[1] = new CharacterTraits("hairLength", "Short");
		tempTraits[2] = new CharacterTraits("eyeColor", "Brown");
		tempTraits[3] = new CharacterTraits("noseSize", "Medium");
		tempTraits[4] = new CharacterTraits("mask", "No");
		tempTraits[5] = new CharacterTraits("lipSize", "Medium");
		tempTraits[6] = new CharacterTraits("glasses", "No");
		tempTraits[7] = new CharacterTraits("facialHair", "No");
		tempTraits[8] = new CharacterTraits("foreheadSize", "Medium");
		tempTraits[9] = new CharacterTraits("moustache", "No");
		tempTraits[10] = new CharacterTraits("beard", "No");
		characters[17] = new Character("Marvin", tempTraits);

		tempTraits[0] = new CharacterTraits("hairColor", "Blonde");
		tempTraits[1] = new CharacterTraits("hairLength", "Short");
		tempTraits[2] = new CharacterTraits("eyeColor", "Blue");
		tempTraits[3] = new CharacterTraits("noseSize", "Medium");
		tempTraits[4] = new CharacterTraits("mask", "No");
		tempTraits[5] = new CharacterTraits("lipSize", "Medium");
		tempTraits[6] = new CharacterTraits("glasses", "No");
		tempTraits[7] = new CharacterTraits("facialHair", "No");
		tempTraits[8] = new CharacterTraits("foreheadSize", "Large");
		tempTraits[9] = new CharacterTraits("moustache", "No");
		tempTraits[10] = new CharacterTraits("beard", "No");
		characters[18] = new Character("Zed", tempTraits);

		tempTraits[0] = new CharacterTraits("hairColor", "Black");
		tempTraits[1] = new CharacterTraits("hairLength", "Short");
		tempTraits[2] = new CharacterTraits("eyeColor", "Black");
		tempTraits[3] = new CharacterTraits("noseSize", "Medium");
		tempTraits[4] = new CharacterTraits("mask", "No");
		tempTraits[5] = new CharacterTraits("lipSize", "Small");
		tempTraits[6] = new CharacterTraits("glasses", "No");
		tempTraits[7] = new CharacterTraits("facialHair", "No");
		tempTraits[8] = new CharacterTraits("foreheadSize", "Large");
		tempTraits[9] = new CharacterTraits("moustache", "No");
		tempTraits[10] = new CharacterTraits("beard", "No");
		characters[19] = new Character("Brett", tempTraits);

		tempTraits[0] = new CharacterTraits("hairColor", "Black");
		tempTraits[1] = new CharacterTraits("hairLength", "Long");
		tempTraits[2] = new CharacterTraits("eyeColor", "Black");
		tempTraits[3] = new CharacterTraits("noseSize", "Small");
		tempTraits[4] = new CharacterTraits("mask", "No");
		tempTraits[5] = new CharacterTraits("lipSize", "Small");
		tempTraits[6] = new CharacterTraits("glasses", "No");
		tempTraits[7] = new CharacterTraits("facialHair", "No");
		tempTraits[8] = new CharacterTraits("foreheadSize", "Medium");
		tempTraits[9] = new CharacterTraits("moustache", "No");
		tempTraits[10] = new CharacterTraits("beard", "No");
		characters[20] = new Character("Trudi", tempTraits);

		tempTraits[0] = new CharacterTraits("hairColor", "Black");
		tempTraits[1] = new CharacterTraits("hairLength", "Short");
		tempTraits[2] = new CharacterTraits("eyeColor", "Brown");
		tempTraits[3] = new CharacterTraits("noseSize", "Large");
		tempTraits[4] = new CharacterTraits("mask", "No");
		tempTraits[5] = new CharacterTraits("lipSize", "Medium");
		tempTraits[6] = new CharacterTraits("glasses", "No");
		tempTraits[7] = new CharacterTraits("facialHair", "Yes");
		tempTraits[8] = new CharacterTraits("foreheadSize", "Large");
		tempTraits[9] = new CharacterTraits("moustache", "Yes");
		tempTraits[10] = new CharacterTraits("beard", "Yes");
		characters[21] = new Character("Maynard", tempTraits);

		tempTraits[0] = new CharacterTraits("hairColor", "Black");
		tempTraits[1] = new CharacterTraits("hairLength", "Long");
		tempTraits[2] = new CharacterTraits("eyeColor", "Blue");
		tempTraits[3] = new CharacterTraits("noseSize", "Medium");
		tempTraits[4] = new CharacterTraits("mask", "No");
		tempTraits[5] = new CharacterTraits("lipSize", "Small");
		tempTraits[6] = new CharacterTraits("glasses", "No");
		tempTraits[7] = new CharacterTraits("facialHair", "No");
		tempTraits[8] = new CharacterTraits("foreheadSize", "Medium");
		tempTraits[9] = new CharacterTraits("moustache", "No");
		tempTraits[10] = new CharacterTraits("beard", "No");
		characters[22] = new Character("Vincent", tempTraits);

		tempTraits[0] = new CharacterTraits("hairColor", "Black");
		tempTraits[1] = new CharacterTraits("hairLength", "Short");
		tempTraits[2] = new CharacterTraits("eyeColor", "Brown");
		tempTraits[3] = new CharacterTraits("noseSize", "Medium");
		tempTraits[4] = new CharacterTraits("mask", "No");
		tempTraits[5] = new CharacterTraits("lipSize", "Small");
		tempTraits[6] = new CharacterTraits("glasses", "No");
		tempTraits[7] = new CharacterTraits("facialHair", "Yes");
		tempTraits[8] = new CharacterTraits("foreheadSize", "Large");
		tempTraits[9] = new CharacterTraits("moustache", "Yes");
		tempTraits[10] = new CharacterTraits("beard", "No");
		characters[23] = new Character("Winston", tempTraits);
	}
}
