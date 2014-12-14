package org.wpattern.pattern.recognition.elements;

import org.apache.log4j.Logger;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleFitness;
import ec.simple.SimpleProblemForm;

public class Evaluator extends Problem implements SimpleProblemForm {

	private static final long serialVersionUID = 6184492510370441350L;

	private static final Logger LOGGER = Logger.getLogger(Evaluator.class);

	static int count = 0;

	@Override
	public void evaluate(final EvolutionState state, final Individual individual, final int subpopulation, final int threadnum) {
		if (individual.evaluated) {
			return;
		}

		if ((!(individual instanceof AbstractIndividual)) || !(state instanceof StateBean)) {
			String errorMessage = "Individual or State are not valid!";
			LOGGER.fatal(errorMessage);
			state.output.fatal(errorMessage, null);
		}

		// 1. Cast to correct class individual and state.
		AbstractIndividual individualBean = (AbstractIndividual) individual;
		StateBean stateBean = (StateBean) state;

		// 2. Remove not used attributes.
		String removeIndexAttribute = "";

		for (int i = 1; i <= individualBean.size(); i++) {
			if (individualBean.genome[i - 1]) {
				if (!removeIndexAttribute.isEmpty()) {
					removeIndexAttribute += ",";
				}

				removeIndexAttribute += i;
			}
		}

		// 3. Classify and take the percentage of correct classification.
		try {
			double averageFitness = 0.0;
			individualBean.setHighestFitness(Double.MIN_VALUE);
			individualBean.setLowestFitness(Double.MAX_VALUE);
			individualBean.abstractSetupIndividual(stateBean, subpopulation, threadnum);
			individualBean.setupIndividual(stateBean, subpopulation, threadnum);

			if (LOGGER.isInfoEnabled()) {
				LOGGER.info(String.format("Evaluating individual: Counter [%s], Class [%s], Number of Executions [%s], Number of Folds [%s].",
						count++, individualBean.getClass(), individualBean.getNumExecutions(), individualBean.getNumFolds()));
			}

			long startTime = System.currentTimeMillis();

			for (int i = 0; i < individualBean.getNumExecutions(); i++) {
				double percentageCorrect = individualBean.classify(stateBean, this.removeAttributes(stateBean.getInstances(), removeIndexAttribute));
				averageFitness += percentageCorrect;

				if (percentageCorrect < individualBean.getLowestFitness()) {
					individualBean.setLowestFitness(percentageCorrect);
				}

				if (percentageCorrect > individualBean.getHighestFitness()) {
					individualBean.setHighestFitness(percentageCorrect);
				}
			}

			averageFitness /= individualBean.getNumExecutions();
			individualBean.setAverageFitness(averageFitness);

			((SimpleFitness) individualBean.fitness).setFitness(stateBean, (float) averageFitness, averageFitness >= 100.0);

			individual.evaluated = true;

			if (LOGGER.isInfoEnabled()) {
				LOGGER.info(String.format("Time spent classifying [%s].", System.currentTimeMillis() - startTime));
				LOGGER.info(String.format("Individual classified: Class [%s], Lowest [%s], Average [%s], Highest [%s]", individualBean.getClass(),
						individualBean.getLowestFitness(), individualBean.getAverageFitness(), individualBean.getHighestFitness()));
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			state.output.fatal(e.getMessage(), null);
		} catch (Throwable t) {
			LOGGER.error(t.getMessage(), t);
			state.output.fatal(t.getMessage(), null);
		}
	}

	@Override
	public void describe(EvolutionState state, Individual ind, int subpopulation, int threadnum, int log) {
	}

	private Instances removeAttributes(Instances instances, String removeIndexAttribute) throws Exception {
		Remove removeAttributes = new Remove();

		removeAttributes.setAttributeIndices(removeIndexAttribute);
		removeAttributes.setInputFormat(instances);

		Instances removedInstances = Filter.useFilter(instances, removeAttributes);
		removedInstances.setClassIndex(removedInstances.numAttributes() - 1);

		return removedInstances;
	}

}
