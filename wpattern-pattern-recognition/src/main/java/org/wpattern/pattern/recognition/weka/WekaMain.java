package org.wpattern.pattern.recognition.weka;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.wpattern.pattern.recognition.utils.OptionsProcessor;
import org.wpattern.pattern.recognition.weka.interfaces.IWekaProcessor;

public class WekaMain {

	public static void main(String[] args) throws Exception {
		OptionsProcessor.setLoggerFilename(args);

		ApplicationContext context = new ClassPathXmlApplicationContext( "/ctx-wpattern-pattern-recognition.xml");

		IWekaProcessor wekaProcessor = context.getBean(IWekaProcessor.class);

		wekaProcessor.setup(args);
		wekaProcessor.process();
	}

}
