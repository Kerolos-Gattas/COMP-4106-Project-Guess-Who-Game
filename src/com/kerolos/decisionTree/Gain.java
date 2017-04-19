package com.kerolos.decisionTree;

import java.util.List;

import com.kerolos.models.CharacterTraits;
import com.kerolos.resources.CommonResources;

//This class calculates the entropy and the information gain for the questions
public class Gain {

	//Calculate Entropy(S)
	private static double calculateEntropy_S(int positiveThreshold, List<List<CharacterTraits>> sampleData) {
		double positive = 0;
		double negative = 0;

		for (int i = 0; i < sampleData.size(); i++) {
			if (sampleData.get(i).size() <= positiveThreshold)//if the sample size is less or equal than the positiveThreshold then the sample is positive and vice versa
				positive++;
			else
				negative++;
		}

		double posProp = positive / CommonResources.NUMBER_OF_SAMPLES;
		double negProp = negative / CommonResources.NUMBER_OF_SAMPLES;

		double entropy = (-posProp) * (Math.log(posProp) / Math.log(2)) - (negProp) * (Math.log(negProp) / Math.log(2));

		return entropy;
	}

	//Calculate the question entropy
	private static double[] calculateQuestionEntropy(CharacterTraits feature, List<List<CharacterTraits>> sampleData,
			int positiveThreshold) {

		//these variables are used to keep track of how many positive and negative samples with yes and no answers to the question
		double positiveYes = 0;
		double negativeYes = 0;
		double positiveNo = 0;
		double negativeNo = 0;

		
		for (int i = 0; i < sampleData.size(); i++) {
			boolean found = false;
			for (int j = 0; j < sampleData.get(i).size(); j++) {
				if (sampleData.get(i).get(j).getname().equals(feature.getname())
						&& sampleData.get(i).get(j).getvalue().equals(feature.getvalue())) {//if the question matches the feature then the answer is yes
					found = true;
					if (sampleData.get(i).size() <= positiveThreshold) {
						positiveYes++;
					} else {
						negativeYes++;
					}
					break;
				}
			}

			if (!found) {//if the question does not matche the feature then the answer is no
				if (sampleData.get(i).size() <= positiveThreshold) {
					positiveNo++;
				} else {
					negativeNo++;
				}
			}
		}

		
		double posYesProp = positiveYes / (positiveYes + negativeYes);
		double negYesProp = negativeYes / (positiveYes + negativeYes);
		double posNoProp = positiveNo / (positiveNo + negativeNo);
		double negNoProp = negativeNo / (positiveNo + negativeNo);

		//both entropy values: yes and no (i.e. entropy(yes), entropy(no))
		double[] entropy = new double[2];

		entropy[0] = (-posYesProp) * (Math.log(posYesProp) / Math.log(2))
				- (negYesProp) * (Math.log(negYesProp) / Math.log(2));
		entropy[1] = (-posNoProp) * (Math.log(posNoProp) / Math.log(2))
				- (negNoProp) * (Math.log(negNoProp) / Math.log(2));

		return entropy;
	}

	//Calculate the information gain for the question
	public static double calculateInfoGain(CharacterTraits feature, List<List<CharacterTraits>> sampleData,
			int positiveThreshold) {

		double featureExists = 0;

		//check to see if the asked feature in the question is in the sample or not
		for (int i = 0; i < sampleData.size(); i++) {
			for (int j = 0; j < sampleData.get(i).size(); j++) {
				if (sampleData.get(i).get(j).getname().equals(feature.getname())
						&& sampleData.get(i).get(j).getvalue().equals(feature.getvalue())) {
					featureExists++;
					break;
				}
			}
		}

		double featureNotExists = CommonResources.NUMBER_OF_SAMPLES - featureExists;
		double entropy_S = calculateEntropy_S(positiveThreshold, sampleData);
		double[] questionEntropy = calculateQuestionEntropy(feature, sampleData, positiveThreshold);

		double gain = entropy_S - ((featureExists / CommonResources.NUMBER_OF_SAMPLES) * questionEntropy[0])
				- ((featureNotExists / CommonResources.NUMBER_OF_SAMPLES) * questionEntropy[1]);

		return gain;
	}
}
