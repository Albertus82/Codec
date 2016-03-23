package it.albertus.codec.console;

import it.albertus.codec.Codec;
import it.albertus.codec.engine.CodecAlgorithm;
import it.albertus.codec.engine.CodecMode;
import it.albertus.codec.resources.Resources;
import it.albertus.util.NewLine;
import it.albertus.util.Version;

import java.io.File;
import java.nio.charset.Charset;

public class CodecConsole extends Codec {

	private static final char OPTION_CHARSET = 'c';
	private static final char OPTION_FILE = 'f';
	private static final String ARG_HELP = "--help";

	/* java -jar codec.jar e|d base64|md2|md5|...|sha-512 "text to encode" */
	public void execute(String[] args) {
		CodecMode mode = null;
		CodecAlgorithm algorithm = null;
		String charsetName = null;

		File inputFile = null;
		File outputFile = null;

		if (args.length < 3) {
			printHelp();
			return;
		}

		for (final String arg : args) {
			if (arg.equalsIgnoreCase(ARG_HELP)) {
				printHelp();
				return;
			}
		}

		/* Mode */
		final String modeArg = args[0].trim();
		for (final CodecMode cm : CodecMode.values()) {
			if (modeArg.equalsIgnoreCase(Character.toString(cm.getAbbreviation()))) {
				mode = cm;
				break;
			}
		}
		if (mode == null) {
			System.err.println(Resources.get("err.invalid.mode", args[0].trim()) + NewLine.SYSTEM_LINE_SEPARATOR);
			printHelp();
			return;
		}

		/* Algorithm */
		final String algorithmArg = args[1].trim();
		for (final CodecAlgorithm ca : CodecAlgorithm.values()) {
			if (ca.getName().equalsIgnoreCase(algorithmArg)) {
				algorithm = ca;
				break;
			}
		}
		if (algorithm == null) {
			System.err.println(Resources.get("err.invalid.algorithm", algorithmArg) + NewLine.SYSTEM_LINE_SEPARATOR);
			printHelp();
			return;
		}

		int expectedArgc = 3;

		/* Options */
		for (int i = 2; i < args.length; i++) {
			if (args[i].length() == 2 && args[i].charAt(0) == '-') {
				switch (Character.toLowerCase(args[i].charAt(1))) {
				case OPTION_CHARSET:
					if (args.length > i + 1 && i < args.length - 2) {
						charsetName = args[i + 1];
						expectedArgc += 2;
					}
					else {
						printHelp();
						return;
					}
					break;
				case OPTION_FILE:
					if (args.length > i + 2 && i < args.length - 2) {
						inputFile = new File(args[i + 1]).getAbsoluteFile();
						outputFile = new File(args[i + 2]).getAbsoluteFile();
						expectedArgc += 2;
					}
					else {
						printHelp();
						return;
					}
					break;
				default:
					System.err.println(Resources.get("err.invalid.option", args[i].charAt(1)) + NewLine.SYSTEM_LINE_SEPARATOR);
					printHelp();
					return;
				}
			}
		}

		getEngine().setAlgorithm(algorithm);
		getEngine().setMode(mode);
		if (charsetName != null) {
			try {
				getEngine().setCharset(Charset.forName(charsetName));
			}
			catch (Exception e) {
				System.err.println(Resources.get("err.invalid.charset", charsetName) + NewLine.SYSTEM_LINE_SEPARATOR);
				printHelp();
				return;
			}
		}

		/* Check arguments count */
		if (expectedArgc != args.length) {
			printHelp();
			return;
		}

		/* Execution */
		try {
			if (inputFile != null && outputFile != null) {
				final String result = getEngine().run(inputFile, outputFile);
				System.out.println(result != null ? result + " - " : "" + Resources.get("msg.file.process.ok.message"));
			}
			else {
				System.out.println(getEngine().run(args[args.length - 1]));
			}
		}
		catch (Exception e) {
			System.err.println(Resources.get("err.generic", e.getMessage()));
		}
	}

	private void printHelp() {
		/* Usage */
		final StringBuilder help = new StringBuilder(Resources.get("msg.help.usage", OPTION_CHARSET, OPTION_FILE));
		help.append(NewLine.SYSTEM_LINE_SEPARATOR).append(NewLine.SYSTEM_LINE_SEPARATOR);

		/* Modes */
		help.append(Resources.get("msg.help.modes")).append(NewLine.SYSTEM_LINE_SEPARATOR);
		for (final CodecMode mode : CodecMode.values()) {
			help.append("    ").append(mode.getAbbreviation()).append("    ").append(mode.getName()).append(NewLine.SYSTEM_LINE_SEPARATOR);
		}
		help.append(NewLine.SYSTEM_LINE_SEPARATOR);

		/* Algorithms */
		{
			final StringBuilder algorithms = new StringBuilder(Resources.get("msg.help.algorithms"));
			algorithms.append(' ');
			int cursorPosition = algorithms.length();
			final int offset = cursorPosition;
			for (final CodecAlgorithm algorithm : CodecAlgorithm.values()) {
				final String toPrint = algorithm.getName() + ", ";
				if (cursorPosition + toPrint.length() >= 80) {
					algorithms.append(NewLine.SYSTEM_LINE_SEPARATOR);
					for (int i = 0; i < offset; i++) {
						algorithms.append(' ');
					}
					cursorPosition = offset;
				}
				algorithms.append(toPrint);
				cursorPosition += toPrint.length();
			}
			help.append(algorithms.replace(algorithms.length() - 2, algorithms.length(), NewLine.SYSTEM_LINE_SEPARATOR));
			help.append(NewLine.SYSTEM_LINE_SEPARATOR);
		}

		/* Charsets */
		{
			final StringBuilder charsets = new StringBuilder(Resources.get("msg.help.charsets"));
			charsets.append(' ');
			int cursorPosition = charsets.length();
			final int offset = cursorPosition;
			for (final String charsetName : Charset.availableCharsets().keySet()) {
				final String toPrint = charsetName + ", ";
				if (cursorPosition + toPrint.length() >= 80) {
					charsets.append(NewLine.SYSTEM_LINE_SEPARATOR);
					for (int i = 0; i < offset; i++) {
						charsets.append(' ');
					}
					cursorPosition = offset;
				}
				charsets.append(toPrint);
				cursorPosition += toPrint.length();
			}
			charsets.replace(charsets.length() - 2, charsets.length(), NewLine.SYSTEM_LINE_SEPARATOR);
			charsets.append(' ').append(Resources.get("msg.help.default.charset", Charset.defaultCharset().name()));
			help.append(charsets);
			help.append(NewLine.SYSTEM_LINE_SEPARATOR).append(NewLine.SYSTEM_LINE_SEPARATOR);
		}

		/* Example */
		help.append(Resources.get("msg.help.example"));

		System.out.println(Resources.get("msg.application.name") + ' ' + Resources.get("msg.version", Version.getInstance().getNumber(), Version.getInstance().getDate()) + " [" + Resources.get("msg.website") + ']'
				+ NewLine.SYSTEM_LINE_SEPARATOR + NewLine.SYSTEM_LINE_SEPARATOR + help.toString().trim());
	}

}
