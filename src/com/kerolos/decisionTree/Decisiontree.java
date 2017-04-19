package com.kerolos.decisionTree;

import java.util.ArrayList;
import java.util.List;

import com.kerolos.models.CharacterTraits;
import com.kerolos.resources.CommonResources;
import com.kerolos.models.Character;

//This class builds and traverses the decision tree
public class Decisiontree {

	private ArrayList<CharacterTraits> questions; //store all the questions that the AI can ask
	private Node root;

	public Decisiontree(ArrayList<Character> characters) {
		questions = new ArrayList<CharacterTraits>();

		// Loop through all the characters and generate the unique set of questions
		// that the AI can ask
		for (int i = 0; i < characters.size(); i++) {
			for (int j = 0; j < CommonResources.NUMBER_OF_FEATURES; j++) {
				if (!containsQuestion(characters.get(i).getFeatures()[j])
						&& !characters.get(i).getFeatures()[j].getvalue().equals("No")) {
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

	public void buildDecisiontree(List<List<CharacterTraits>> sampleData, int positiveThreshold) {

		root = new Node(sampleData, questions, null);
		root.buildNode(positiveThreshold);
	}

	public Node getRoot() {
		return root;
	}

	public void traverseTree(String answer) {
		if (answer.equals("Yes")) {
			root = root.getYesChild();
		} else {
			root = root.getNoChild();
		}
	}
}
