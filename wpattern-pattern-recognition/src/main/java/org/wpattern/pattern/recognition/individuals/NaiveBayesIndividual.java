package org.wpattern.pattern.recognition.individuals;

import org.wpattern.pattern.recognition.elements.AbstractIndividual;
import org.wpattern.pattern.recognition.elements.StateBean;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;
import ec.EvolutionState;

public class NaiveBayesIndividual extends AbstractIndividual {

	private static final long serialVersionUID = -6938533759650699535L;

	@Override
	public double classify(StateBean state, Instances removedAttributeInstances) throws Exception {
		NaiveBayes naiveBayesClassifier = new NaiveBayes();

		removedAttributeInstances.randomize(state.getRandom());

		Evaluation evaluation = new Evaluation(removedAttributeInstances);
		evaluation.crossValidateModel(naiveBayesClassifier, removedAttributeInstances, this.getNumFolds(), state.getRandom());

		return evaluation.pctCorrect();
	}

	@Override
	public void setupIndividual(EvolutionState state, int subpop, int thread) {
	}

}
