package org.wpattern.pattern.recognition.individuals;

import org.wpattern.pattern.recognition.elements.AbstractIndividual;
import org.wpattern.pattern.recognition.elements.StateBean;

import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import ec.EvolutionState;

public class J48Individual extends AbstractIndividual {

	private static final long serialVersionUID = 201306251558L;

	@Override
	public double classify(StateBean state, Instances removedAttributeInstances) throws Exception {
		J48 j48Classifier = new J48();

		removedAttributeInstances.randomize(state.getRandom());

		Evaluation evaluation = new Evaluation(removedAttributeInstances);
		evaluation.crossValidateModel(j48Classifier, removedAttributeInstances, this.getNumFolds(), state.getRandom());

		return evaluation.pctCorrect();
	}

	@Override
	public void setupIndividual(EvolutionState state, int subpop, int thread) {
	}

}
