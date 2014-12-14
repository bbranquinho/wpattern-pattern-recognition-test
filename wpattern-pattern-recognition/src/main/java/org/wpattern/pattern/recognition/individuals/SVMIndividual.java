package org.wpattern.pattern.recognition.individuals;

import org.wpattern.pattern.recognition.elements.AbstractIndividual;
import org.wpattern.pattern.recognition.elements.StateBean;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.core.Instances;
import ec.EvolutionState;

public class SVMIndividual extends AbstractIndividual {

	private static final long serialVersionUID = 2916222140639692944L;

	@Override
	public double classify(StateBean state, Instances removedAttributeInstances) throws Exception {
		SMO smoClassifier = new SMO();

		removedAttributeInstances.randomize(state.getRandom());

		Evaluation evaluation = new Evaluation(removedAttributeInstances);
		evaluation.crossValidateModel(smoClassifier, removedAttributeInstances, this.getNumFolds(), state.getRandom());

		return evaluation.pctCorrect();
	}

	@Override
	public void setupIndividual(EvolutionState state, int subpop, int thread) {
	}

}
