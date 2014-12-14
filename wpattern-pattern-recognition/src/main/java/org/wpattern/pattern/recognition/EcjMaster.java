package org.wpattern.pattern.recognition;

import org.wpattern.pattern.recognition.utils.OptionsProcessor;

import ec.Evolve;


public class EcjMaster {

	// Argument used to load a checkpoint => -checkpoint FILENAME
	// Example of parameters to start a master => -file ./src/main/resources/ecj/test_01/master.params
	public static void main(String[] args) {
		OptionsProcessor.setLoggerFilename(args);

		Evolve.main(args);
	}

}
