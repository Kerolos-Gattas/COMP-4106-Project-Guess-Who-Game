package com.kerolos.decisionTree;

import java.util.ArrayList;
import java.util.List;

import com.kerolos.models.CharacterTraits;

public class Node {

	private List<List<CharacterTraits>> sampleData;
	private ArrayList<CharacterTraits> questions;// Contains all the unique questions that the AI can  ask	
	// yes and no children, the content depends on the answer to the question
	private Node yesChild;
	private Node noChild;	
	private CharacterTraits chosenQuestion; // store the question that the AI is  going to ask


	public Node(List<List<CharacterTraits>> sampleData, ArrayList<CharacterTraits> questions, Node parent) {
		
		this.sampleData = new ArrayList<List<CharacterTraits>>();
		for (int i = 0; i < sampleData.size(); i++) {
			this.sampleData.add(sampleData.get(i));
		}

		this.questions = new ArrayList<CharacterTraits>();
		for (int i = 0; i < questions.size(); i++) {
			this.questions.add(questions.get(i));
		}

		chosenQuestion = null;
		yesChild = null;
		noChild = null;
	}

	//build the nodes yes and no according to the information gain for the question
	public void buildNode(int positiveThreshold) {
		
		//if the sample size is less than 1 or the questions list is empty then stop creating childern
		if (sampleData.size() > 1 && !questions.isEmpty()) {
			
			//Calculate information gain for all the questions
			double[] questionsInfoGain = new double[questions.size()];
			for (int i = 0; i < questions.size(); i++) {
				questionsInfoGain[i] = Gain.calculateInfoGain(questions.get(i), sampleData, positiveThreshold);
			}

			int maxIndex = 0;
			double maxGain = 0;
			//get the highest information gain
			for (int i = 0; i < questionsInfoGain.length; i++) {
				if (maxGain < questionsInfoGain[i]) {
					maxIndex = i;
					maxGain = questionsInfoGain[i];
				}
			}

			//remove the question to be asked
			chosenQuestion = questions.remove(maxIndex);

			//Temporary variables to store remaining sample
			List<List<CharacterTraits>> sampleDataYes = new ArrayList<List<CharacterTraits>>();
			List<List<CharacterTraits>> sampleDataNo = new ArrayList<List<CharacterTraits>>();
			for (int i = 0; i < sampleData.size(); i++) {
				sampleDataYes.add(sampleData.get(i));
				sampleDataNo.add(sampleData.get(i));
			}

			//create yes and no children
			yesChild = new Node(removeFalseFeatures(chosenQuestion, sampleDataYes), questions, this);
			noChild = new Node(removeTrueFeatures(chosenQuestion, sampleDataNo), questions, this);
			
			//build yes and no children
			yesChild.buildNode(positiveThreshold);
			noChild.buildNode(positiveThreshold);
		}
	}

	// remove all samples that do not match the feature in the question
	private List<List<CharacterTraits>> removeFalseFeatures(CharacterTraits question,
			List<List<CharacterTraits>> sampleData) {

		// store samples that are going to be removed, this has to be done
		// since it does not work if we remove it during the loop bec all the
		// elements will shift
		ArrayList<Integer> removeIndex = new ArrayList<Integer>();

		for (int i = 0; i < sampleData.size(); i++) {
			boolean found = false;
			for (int j = 0; j < sampleData.get(i).size(); j++) {
				if (question.getname().equals(sampleData.get(i).get(j).getname())
						&& question.getvalue().equals(sampleData.get(i).get(j).getvalue())) {
					found = true;
					break;
				}
			}
			if (!found)
				removeIndex.add(i);
		}

		for (int i = removeIndex.size() - 1; i > -1; i--) {
			sampleData.remove((int) removeIndex.get(i));
		}

		return sampleData;
	}

	// remove all samples that match the feature in the question
	private List<List<CharacterTraits>> removeTrueFeatures(CharacterTraits question,
			List<List<CharacterTraits>> sampleData) {

		// store samples that are going to be removed, this has to be done
		// since it does not work if we remove it during the loop bec all the
		// elements will shift
		ArrayList<Integer> removeIndex = new ArrayList<Integer>();

		for (int i = 0; i < sampleData.size(); i++) {
			for (int j = 0; j < sampleData.get(i).size(); j++) {
				if (question.getname().equals(sampleData.get(i).get(j).getname())
						&& question.getvalue().equals(sampleData.get(i).get(j).getvalue())) {
					removeIndex.add(i);
					break;
				}
			}
		}

		for (int i = removeIndex.size() - 1; i > -1; i--) {
			sampleData.remove((int) removeIndex.get(i));
		}

		return sampleData;
	}

	public Node getYesChild() {
		return yesChild;
	}

	public Node getNoChild() {
		return noChild;
	}

	public CharacterTraits getChosenQuestion() {
		return chosenQuestion;
	}
}
