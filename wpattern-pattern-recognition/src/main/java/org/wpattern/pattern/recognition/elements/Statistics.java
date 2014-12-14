package org.wpattern.pattern.recognition.elements;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import ec.EvolutionState;
import ec.simple.SimpleStatistics;
import ec.util.Parameter;

public class Statistics extends SimpleStatistics {

	private static final long serialVersionUID = 201307010311L;

	private static final Logger LOGGER = Logger.getLogger(Statistics.class);

	private static final String GRAPH_FILENAME_PARAMETER = "wpattern.graph.filename";

	private static final String GRAPH_WIDTH_PARAMETER = "wpattern.graph.width";

	private static final String GRAPH_HEIGHT_PARAMETER = "wpattern.graph.height";

	private static final int DEFAULT_GRAPH_WIDTH = 800;

	private static final int DEFAULT_GRAPH_HEIGHT = 600;

	private String graphFilename;

	private int graphWidth;

	private int graphHeight;

	private double[][] fitnessByEvaluation;

	public Statistics() {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Statistics instantiated.");
		}
	}

	@Override
	public void postInitializationStatistics(EvolutionState state) {
		super.postInitializationStatistics(state);

		this.fitnessByEvaluation = new double[state.population.subpops.length][state.numGenerations];
		this.graphFilename = state.parameters.getString(new Parameter(GRAPH_FILENAME_PARAMETER), null);

		if (this.graphFilename != null) {
			this.graphWidth = state.parameters.getIntWithDefault(new Parameter(GRAPH_WIDTH_PARAMETER), null, DEFAULT_GRAPH_WIDTH);
			this.graphHeight = state.parameters.getIntWithDefault(new Parameter(GRAPH_HEIGHT_PARAMETER), null, DEFAULT_GRAPH_HEIGHT);
		}
	}

	@Override
	public void postEvaluationStatistics(EvolutionState state) {
		super.postEvaluationStatistics(state);

		for (int i = 0; i < this.best_of_run.length; i++) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Best individual: " + this.best_of_run[i]);
			}

			this.fitnessByEvaluation[i][state.generation] = this.best_of_run[i].fitness.fitness();
		}
	}

	@Override
	public void finalStatistics(EvolutionState state, int result) {
		super.finalStatistics(state, result);

		if (!(state instanceof StateBean)) {
			return;
		}

		StateBean stateBean = (StateBean)state;

		if (LOGGER.isInfoEnabled()) {
			for (int i = 0; i < this.best_of_run.length; i++) {
				AbstractIndividual bestIndividual = (AbstractIndividual) this.best_of_run[i];
				String message = "Best Individual [Population = %s][Fitness = %s][Attributes = ";
				boolean first = true;

				for (int j = 0; j < bestIndividual.size(); j++) {
					if (bestIndividual.genome[j]) {
						message += (first ? "" : ", ") + stateBean.getInstances().attribute(j).name();
						first = false;
					}
				}

				LOGGER.info(String.format(message + "]", i, bestIndividual.fitness.fitnessToStringForHumans()));
			}
		}

		this.showGraphics();
	}

	private void showGraphics() {
		if (this.graphFilename == null) {
			return;
		}

		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries timeSeries[] = new XYSeries[this.fitnessByEvaluation.length];
		double maxValue = Double.MIN_VALUE;
		double minValue = Double.MAX_VALUE;

		for (int i = 0; i < this.fitnessByEvaluation.length; i++) {
			timeSeries[i] = new XYSeries("Population " + i);

			for (int j = 0; j < this.fitnessByEvaluation[i].length; j++) {
				timeSeries[i].add(j, this.fitnessByEvaluation[i][j]);

				if (this.fitnessByEvaluation[i][j] > maxValue) {
					maxValue = this.fitnessByEvaluation[i][j];
				}

				if (this.fitnessByEvaluation[i][j] < minValue) {
					minValue = this.fitnessByEvaluation[i][j];
				}
			}

			dataset.addSeries(timeSeries[i]);
		}

		JFreeChart lineChartObject = ChartFactory.createXYLineChart("Fitness by Generation", "Generation", "Fitness", dataset, PlotOrientation.VERTICAL, true, true, false);

		lineChartObject.getXYPlot().getDomainAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		lineChartObject.getXYPlot().getRangeAxis().setRange(new Range(minValue - 1.0, maxValue + 1.0));

		File lineChart = new File(this.graphFilename);

		try {
			ChartUtilities.saveChartAsPNG(lineChart, lineChartObject, this.graphWidth, this.graphHeight);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

}
