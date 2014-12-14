package org.wpattern.pattern.recognition.elements;

import java.io.File;
import java.util.Random;

import org.apache.log4j.Logger;
import org.wpattern.pattern.recognition.weka.WekaData;
import org.wpattern.pattern.recognition.weka.interfaces.IWekaData;

import weka.filters.Filter;
import ec.EvolutionState;
import ec.simple.SimpleInitializer;
import ec.util.Output;
import ec.util.Parameter;

public class StateInitializer extends SimpleInitializer {

	private static final long serialVersionUID = 201306050336L;

	private static final Logger LOGGER = Logger.getLogger(StateInitializer.class);

	private static final String WEKA_DATASET_PARAMETER = "weka.dataset";

	private static final String GENOME_SIZE_PARAMETER = "pop.subpop.%s.species.genome-size";

	private static final String POPULATION_SIZE_PARAMETER = "pop.subpops";

	private static final String WEKA_FILTER_PARAMETER = "weka.filter";

	private StateBean stateBean;

	@Override
	public void setup(EvolutionState state, Parameter base) {
		super.setup(state, base);

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Initialize a new State.");
		}

		this.stateBean = (StateBean) state;

		IWekaData wekaData = new WekaData();
		this.stateBean.setRandom(new Random(System.currentTimeMillis()));
		this.stateBean.setInstances(wekaData.retrieveDataSet(this.getDatasetFilename(state)));

		String wekaFilter = state.parameters.getString(new Parameter(WEKA_FILTER_PARAMETER), null);

		if (wekaFilter != null) {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info(String.format("Applying filter [%s].", wekaFilter));
			}

			try {
				Class<?> filterClass = Class.forName(wekaFilter);
				Filter filter = (Filter) filterClass.newInstance();
				filter.setInputFormat(this.stateBean.getInstances());

				this.stateBean.setInstances(Filter.useFilter(this.stateBean.getInstances(), filter));
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}

		int populationSize = this.stateBean.parameters.getInt(new Parameter(POPULATION_SIZE_PARAMETER), null);

		for (int i = 0; i < populationSize; i++) {
			this.stateBean.parameters.setProperty(String.format(GENOME_SIZE_PARAMETER, i), "" + (this.stateBean.getInstances().numAttributes() - 1));
		}
	}

	private String getDatasetFilename(EvolutionState state) {
		String wekaDatasetParameter = state.parameters.getString(new Parameter(WEKA_DATASET_PARAMETER), null);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Reading parameter [%s] to load dataset [%s].", WEKA_DATASET_PARAMETER, wekaDatasetParameter));
		}

		File file = new File(wekaDatasetParameter);

		if (file.exists() && file.isFile()) {
			return file.getAbsolutePath();
		}

		String path = System.getProperty("user.dir") + "\\" + wekaDatasetParameter;

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Ckeck if exist the file %s.", path));
		}

		file = new File(path);

		if (file.exists() && file.isFile()) {
			return file.getAbsolutePath();
		}

		path = System.getProperty("user.dir") + "\\dataset\\" + wekaDatasetParameter;

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Ckeck if exist the file %s.", path));
		}

		file = new File(path);

		if (file.exists() && file.isFile()) {
			return file.getAbsolutePath();
		}

		path = System.getProperty("user.dir") + "\\src\\main\\resources\\dataset\\" + wekaDatasetParameter;

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Ckeck if exist the file %s.", path));
		}

		file = new File(path);

		if (file.exists() && file.isFile()) {
			return file.getAbsolutePath();
		}

		String message = String.format("File [%s] not founded.", wekaDatasetParameter);

		LOGGER.fatal(message);
		Output.initialError(message);

		return null;
	}

}
