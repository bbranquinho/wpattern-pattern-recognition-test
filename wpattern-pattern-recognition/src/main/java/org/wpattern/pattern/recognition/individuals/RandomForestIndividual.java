package org.wpattern.pattern.recognition.individuals;

import org.wpattern.pattern.recognition.elements.AbstractIndividual;
import org.wpattern.pattern.recognition.elements.StateBean;

import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import ec.EvolutionState;

public class RandomForestIndividual extends AbstractIndividual {

	private static final long serialVersionUID = 201306251557L;

	@Override
	public double classify(StateBean state, Instances removedAttributeInstances) throws Exception {
		RandomForest randomForestClassifier = new RandomForest();

		removedAttributeInstances.randomize(state.getRandom());

		Evaluation evaluation = new Evaluation(removedAttributeInstances);
		evaluation.crossValidateModel(randomForestClassifier, removedAttributeInstances, this.getNumFolds(), state.getRandom());

		return evaluation.pctCorrect();
	}

	@Override
	public void setupIndividual(EvolutionState state, int subpop, int thread) {
	}

}
