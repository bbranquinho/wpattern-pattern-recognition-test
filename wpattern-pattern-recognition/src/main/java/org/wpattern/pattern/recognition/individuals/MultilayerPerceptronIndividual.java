package org.wpattern.pattern.recognition.individuals;

import org.wpattern.pattern.recognition.elements.AbstractIndividual;
import org.wpattern.pattern.recognition.elements.StateBean;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;
import ec.EvolutionState;

public class MultilayerPerceptronIndividual extends AbstractIndividual {

	private static final long serialVersionUID = 201306251555L;

	@Override
	public double classify(StateBean state, Instances removedAttributeInstances) throws Exception {
		MultilayerPerceptron multilayerPerceptronClassifier = new MultilayerPerceptron();

		removedAttributeInstances.randomize(state.getRandom());

		Evaluation evaluation = new Evaluation(removedAttributeInstances);
		evaluation.crossValidateModel(multilayerPerceptronClassifier, removedAttributeInstances, this.getNumFolds(), state.getRandom());

		return evaluation.pctCorrect();
	}

	@Override
	public void setupIndividual(EvolutionState state, int subpop, int thread) {
	}

}
