package org.wpattern.pattern.recognition.weka;

import java.io.File;
import java.util.Random;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.wpattern.pattern.recognition.weka.interfaces.IWekaData;
import org.wpattern.pattern.recognition.weka.interfaces.IWekaProcessor;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.core.Instances;
import weka.filters.Filter;

@Named
public class WekaProcessor implements IWekaProcessor {

	private static final int SYSTEM_ERROR = -1;

	private Logger logger = Logger.getLogger(this.getClass());

	@Inject
	private IWekaData wekaData;

	private Instances instances;

	private Classifier classifier;

	private String wekaFilter = null;

	private int numExecutions = 1;

	private int numFolds = 2;

	private int k = 1;

	private Random random = new Random();

	public WekaProcessor() {

	}

	@Override
	public void setup(String[] options) throws Exception {
		this.processOptions(options.clone());
	}

	@Override
	public void process() {
		double highestFitness, lowestFitness, averageFitness;
		int count = 0;

		if (this.classifier instanceof IBk) {
			((IBk) this.classifier).setKNN(this.k);
		}

		try {
			averageFitness = 0.0;
			highestFitness = Double.MIN_VALUE;
			lowestFitness = Double.MAX_VALUE;

			if (this.logger.isInfoEnabled()) {
				this.logger.info(String.format("Classifing: Counter [%s], Class [%s], Number of Executions [%s], Number of Folds [%s].",
						count++, this.classifier.getClass(), this.numExecutions, this.numFolds));
			}

			long startTime = System.currentTimeMillis();

			for (int i = 0; i < this.numExecutions; i++) {
				this.instances.randomize(this.random);

				Evaluation evaluation = new Evaluation(this.instances);
				evaluation.crossValidateModel(this.classifier, this.instances, this.numFolds, this.random);

				double percentageCorrect = evaluation.pctCorrect();
				averageFitness += percentageCorrect;

				if (percentageCorrect < lowestFitness) {
					lowestFitness = percentageCorrect;
				}

				if (percentageCorrect > highestFitness) {
					highestFitness = percentageCorrect;
				}
			}

			averageFitness /= this.numExecutions;

			if (this.logger.isInfoEnabled()) {
				this.logger.info(String.format("Time spent classifying [%s].", System.currentTimeMillis() - startTime));
				this.logger.info(String.format("Classification: Class [%s], Lowest [%s], Average [%s], Highest [%s]", this.classifier.getClass(),
						lowestFitness, averageFitness, highestFitness));
			}
		} catch (Exception e) {
			this.logger.error(e.getMessage(), e);
		} catch (Throwable t) {
			this.logger.error(t.getMessage(), t);
		}
	}

	private void processOptions(String options[]) throws Exception {
		Class<?> individualClass = null;

		for (int i = 0; i < options.length; i++) {
			switch (options[i]) {
			case "-dataset": // Required
				if ((i + 1) >= options.length) {
					this.logger.error("Dataset not defined.");
					System.exit(SYSTEM_ERROR);
				}

				this.instances = this.wekaData.retrieveDataSet(this.getFullFilename(options[++i]));
				break;

			case "-filter": // Optional (default = none)
				if ((i + 1) < options.length) {
					this.wekaFilter = options[++i];
				}
				break;

			case "-numExecutions": // Optional (default = 1)
				if ((i + 1) < options.length) {
					this.numExecutions = Integer.parseInt(options[++i]);
				}
				break;

			case "-numFolds": // Optional (default = 2)
				if ((i + 1) < options.length) {
					this.numFolds = Integer.parseInt(options[++i]);
				}
				break;

			case "-classifier": // Required
				if ((i + 1) >= options.length) {
					this.logger.error("Classifier not defined.");
					System.exit(SYSTEM_ERROR);
				}

				individualClass = Class.forName(options[++i]);
				break;

			case "-k": // Optional (default = 1)
				if ((i + 1) < options.length) {
					this.k = Integer.parseInt(options[++i]);
				}
				break;
			}
		}

		this.classifier = (Classifier) individualClass.newInstance();

		this.applyFilter();
	}

	private void applyFilter() {
		if ((this.wekaFilter != null) && (!this.wekaFilter.isEmpty())) {
			if (this.logger.isInfoEnabled()) {
				this.logger.info(String.format("Applying filter [%s].",
						this.wekaFilter));
			}

			try {
				Class<?> filterClass = Class.forName(this.wekaFilter);
				Filter filter = (Filter) filterClass.newInstance();
				filter.setInputFormat(this.instances);

				this.instances = Filter.useFilter(this.instances, filter);
			} catch (Exception e) {
				this.logger.error(e.getMessage(), e);
			}
		}
	}

	private String  getFullFilename(String filename) {
		File file = new File(filename);

		if (file.exists() && file.isFile()) {
			return file.getAbsolutePath();
		}

		String path = System.getProperty("user.dir") + "\\" + filename;

		if (this.logger.isDebugEnabled()) {
			this.logger.debug(String.format("Ckeck if exist the file %s.", path));
		}

		file = new File(path);

		if (file.exists() && file.isFile()) {
			return file.getAbsolutePath();
		}

		path = System.getProperty("user.dir") + "\\dataset\\" + filename;

		if (this.logger.isDebugEnabled()) {
			this.logger.debug(String.format("Ckeck if exist the file %s.", path));
		}

		file = new File(path);

		if (file.exists() && file.isFile()) {
			return file.getAbsolutePath();
		}

		path = System.getProperty("user.dir") + "\\src\\main\\resources\\dataset\\" + filename;

		if (this.logger.isDebugEnabled()) {
			this.logger.debug(String.format("Ckeck if exist the file %s.", path));
		}

		file = new File(path);

		if (file.exists() && file.isFile()) {
			return file.getAbsolutePath();
		}

		this.logger.fatal("File not founded " + filename);

		System.exit(SYSTEM_ERROR);

		return null;
	}

}
