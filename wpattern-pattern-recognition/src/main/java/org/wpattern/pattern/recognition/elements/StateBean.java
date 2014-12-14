package org.wpattern.pattern.recognition.elements;

import java.util.Random;

import weka.core.Instances;
import ec.simple.SimpleEvolutionState;

public class StateBean extends SimpleEvolutionState {

	private static final long serialVersionUID = 201306131114L;

	private Instances instances;

	private Random random;

	public Instances getInstances() {
		return this.instances;
	}

	public void setInstances(Instances instances) {
		this.instances = instances;
	}

	public Random getRandom() {
		return this.random;
	}

	public void setRandom(Random random) {
		this.random = random;
	}

}
