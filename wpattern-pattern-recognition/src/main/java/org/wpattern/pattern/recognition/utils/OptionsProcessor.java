package org.wpattern.pattern.recognition.utils;

public class OptionsProcessor {

	private static final String LOGGER_OPTION = "-loggerFilename";

	private static final String DEFAULT_LOGGER_FILENAME = String.format("wpattern-pattern-recognition-logger-%s.log", System.currentTimeMillis());

	public static String setLoggerFilename(String args[]) {
		String loggerFilename = DEFAULT_LOGGER_FILENAME;

		for (int i = 0; i < args.length; i++) {
			if ((args[i].toLowerCase().compareTo(LOGGER_OPTION.toLowerCase()) == 0) && (args.length > (i + 1))) {
				loggerFilename = args[i + 1];
				break;
			}
		}

		System.setProperty("logger_filename", loggerFilename);

		return loggerFilename;
	}

}
