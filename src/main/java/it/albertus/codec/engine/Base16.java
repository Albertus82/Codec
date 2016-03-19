package it.albertus.codec.engine;

import it.albertus.codec.resources.Resources;
import it.albertus.util.NewLine;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import org.apache.openjpa.lib.util.Base16Encoder;

public class Base16 {

	private static final CharSequence ALPHABET = "0123456789ABCDEF";

	public static String encode(final byte[] byteArray) {
		return Base16Encoder.encode(byteArray);
	}

	public static byte[] decode(String encoded) {
		encoded = encoded.toUpperCase().replace(NewLine.CRLF.toString(), "");
		if (encoded.matches("[" + ALPHABET + "]*")) {
			return Base16Encoder.decode(encoded);
		}
		else {
			throw new IllegalArgumentException(Resources.get("err.invalid.input"));
		}
	}

	public static void encode(final InputStream input, final OutputStream output) throws IOException {
		final int bufferSize = 78 / 2;
		final byte[] buffer = new byte[bufferSize];
		int count;
		while ((count = input.read(buffer)) != -1) {
			final byte[] toWrite = Base16Encoder.encode(count == bufferSize ? buffer : Arrays.copyOfRange(buffer, 0, count)).getBytes();
			output.write(toWrite);
			output.write(NewLine.CRLF.toString().getBytes());
		}
		output.flush();
	}

	public static void decode(final InputStream input, final OutputStream output) throws IOException {
		final int bufferSize = 2 * 4096;
		final byte[] buffer = new byte[bufferSize];
		int count;
		while ((count = input.read(buffer)) != -1) {
			output.write(Base16Encoder.decode(new String(count == bufferSize ? buffer : Arrays.copyOfRange(buffer, 0, count)).replace(NewLine.CRLF.toString(), "")));
		}
		output.flush();
	}

}
