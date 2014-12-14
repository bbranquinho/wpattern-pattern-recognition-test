package org.wpattern.pattern.recognition.weka.interfaces;

import weka.core.Instances;

public interface IWekaData {

	public Instances retrieveDataSet(String filename);

}
