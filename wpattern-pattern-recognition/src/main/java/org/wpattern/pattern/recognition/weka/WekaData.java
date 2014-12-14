package org.wpattern.pattern.recognition.weka;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.inject.Named;

import org.apache.log4j.Logger;
import org.wpattern.pattern.recognition.weka.interfaces.IWekaData;

import weka.core.Instances;

@Named
public class WekaData implements IWekaData {

	private static final Logger LOGGER = Logger.getLogger(WekaData.class);

	@Override
	public Instances retrieveDataSet(String filename) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(String.format("Loading the dataset [%s].", filename));
		}

		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));

			Instances dataset = new Instances(reader);

			dataset.setClassIndex(dataset.numAttributes() - 1);

			return dataset;
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);

			return null;
		}
	}

}
