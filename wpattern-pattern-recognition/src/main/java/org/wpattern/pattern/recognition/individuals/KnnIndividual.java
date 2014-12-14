package org.wpattern.pattern.recognition.individuals;

import org.wpattern.pattern.recognition.elements.AbstractIndividual;
import org.wpattern.pattern.recognition.elements.StateBean;

import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.core.Instances;
import ec.EvolutionState;
import ec.util.Parameter;

public class KnnIndividual extends AbstractIndividual {

	private static final long serialVersionUID = 201306250137L;

	private static final String K_PARAMETER = "pop.subpop.%s.species.k";

	private int k;

	@Override
	public double classify(StateBean state, Instances removedAttributeInstances) throws Exception {
		IBk knnClassifier = new IBk(this.k);

		removedAttributeInstances.randomize(state.getRandom());

		Evaluation evaluation = new Evaluation(removedAttributeInstances);
		evaluation.crossValidateModel(knnClassifier, removedAttributeInstances, this.getNumFolds(), state.getRandom());

		return evaluation.pctCorrect();
	}

	@Override
	public void setupIndividual(EvolutionState state, int subpop, int thread) {
		this.k = state.parameters.getInt(new Parameter(String.format(K_PARAMETER, subpop)), null);
	}

}
