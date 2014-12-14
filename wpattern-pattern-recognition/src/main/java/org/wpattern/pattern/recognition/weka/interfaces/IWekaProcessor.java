package org.wpattern.pattern.recognition.weka.interfaces;

public interface IWekaProcessor {

	public void setup(String[] options) throws Exception;

	public void process();

}
