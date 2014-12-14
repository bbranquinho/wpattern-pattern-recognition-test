package org.wpattern.pattern.recognition.elements;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import weka.core.Instances;
import ec.EvolutionState;
import ec.util.Parameter;
import ec.vector.BitVectorIndividual;

public abstract class AbstractIndividual extends BitVectorIndividual {

	private static final long serialVersionUID = 201306250149L;

	private static final String NUM_EXECUTIONS_PARAMETER = "pop.subpop.%s.species.num-executions";

	private static final String NUM_FOLDS_PARAMETER = "pop.subpop.%s.species.num-folds";

	private int numExecutions;

	private int numFolds;

	private double highestFitness;

	private double lowestFitness;

	private double averageFitness;

	public abstract double classify(StateBean state, Instances removedAttributeInstances) throws Exception;

	public abstract void setupIndividual(EvolutionState state, int subpop, int thread);

	public final void abstractSetupIndividual(EvolutionState state, int subpop, int thread) {
		this.numExecutions = state.parameters.getIntWithDefault(new Parameter(String.format(NUM_EXECUTIONS_PARAMETER, subpop)), null, 1);

		this.numFolds = state.parameters.getInt(new Parameter(String.format(NUM_FOLDS_PARAMETER, subpop)), null);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

	public void setNumExecutions(int numExecutions) {
		this.numExecutions = numExecutions;
	}

	public int getNumExecutions() {
		return this.numExecutions;
	}

	public int getNumFolds() {
		return this.numFolds;
	}

	public void setNumFolds(int numFolds) {
		this.numFolds = numFolds;
	}

	public double getHighestFitness() {
		return this.highestFitness;
	}

	public void setHighestFitness(double highestFitness) {
		this.highestFitness = highestFitness;
	}

	public double getLowestFitness() {
		return this.lowestFitness;
	}

	public void setLowestFitness(double lowestFitness) {
		this.lowestFitness = lowestFitness;
	}

	public double getAverageFitness() {
		return this.averageFitness;
	}

	public void setAverageFitness(double averageFitness) {
		this.averageFitness = averageFitness;
	}

}
