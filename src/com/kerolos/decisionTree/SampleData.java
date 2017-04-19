package com.kerolos.decisionTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.kerolos.models.Character;
import com.kerolos.models.CharacterTraits;
import com.kerolos.resources.CommonResources;

//This class generates and store the sample data. Each sample contains a series of random questions that the AI can ask
//The sample stops when the characters left are equal to 1
public class SampleData {

	private List<List<CharacterTraits>> sampleData;
	private int positiveThreshold;//A threshold for the positive samples

	public SampleData() {
		sampleData = new ArrayList<List<CharacterTraits>>();
		positiveThreshold = 0;
	}

	//Generate random sample data, each sample is a series of questions
	public void generateSampleData(ArrayList<Character> characters) {

		Random rand = new Random();

		for (int i = 0; i < CommonResources.NUMBER_OF_SAMPLES; i++) {
			//store the characters that are left in this arrrayList
			ArrayList<Character> charactersLeft = new ArrayList<Character>();
			for (int j = 0; j < characters.size(); j++) {
				charactersLeft.add(new Character(characters.get(j)));
			}
			
			sampleData.add(new ArrayList<CharacterTraits>());
			
			//generate a random character that is the winner
			int randWinCharacter = rand.nextInt(charactersLeft.size()) + 0;
			Character winCharacter = charactersLeft.get(randWinCharacter);
			
			//this counter is used to stop the loop in case of an infinte loop
			int counterLimit = 0;

			outerloop: while (charactersLeft.size() > 1) {
				int charactersLeftSize = charactersLeft.size();

				//pick a random question, the questions are being represented by the feature name and value (i.e. hairColor:black)
				int randCharacter = rand.nextInt(charactersLeft.size()) + 0;
				int randFeature = rand.nextInt(charactersLeft.get(randCharacter).getFeatures().length) + 0;
				
				//if the current sample already contains the question then skip this iteration of the loop
				for (int g = 0; g < sampleData.get(i).size(); g++) {
					if (sampleData.get(i).get(g).getname()
							.equals(charactersLeft.get(randCharacter).getFeatures()[randFeature].getname())) {
						if (sampleData.get(i).get(g).getvalue()
								.equals(charactersLeft.get(randCharacter).getFeatures()[randFeature].getvalue())
								|| sampleData.get(i).get(g).getvalue().equals("Yes")
								|| sampleData.get(i).get(g).getvalue().equals("No")) {
							continue outerloop;
						}
					}
				}

				//add the random question to the sample i
				sampleData.get(i).add(charactersLeft.get(randCharacter).getFeatures()[randFeature]);

				//base on the answer to the question remove the appropriate characters
				if (winCharacter.getFeatures()[randFeature].getvalue()
						.equals(charactersLeft.get(randCharacter).getFeatures()[randFeature].getvalue())) {
					removeFalseFeatures(charactersLeft.get(randCharacter).getFeatures()[randFeature], charactersLeft);
					
				} else {
					removeTrueFeatures(charactersLeft.get(randCharacter).getFeatures()[randFeature], charactersLeft);
				}
				
				//if no characters were removed increase the counter
				if (charactersLeft.size() == charactersLeftSize) {
					counterLimit++;
				}

				//if the counter reaches 20 in this sample then break this loop and start a new sample
				if (counterLimit == 20) {
					break;
				}
			}
		}
		//calculate the positive threshold
		calculateAverageQuestions();
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

	//calculate the positive threshold for the sample data based on the number of questions of each sample
	private void calculateAverageQuestions() {
		int count = 0;

		for (int i = 0; i < sampleData.size(); i++) {
			count += sampleData.get(i).size();
		}
		
		//the average of all the samples
		positiveThreshold = (count / CommonResources.NUMBER_OF_SAMPLES);

	}

	public List<List<CharacterTraits>> getSampleData() {
		return sampleData;
	}

	public int getPositiveThreshold() {
		return positiveThreshold;
	}
}
