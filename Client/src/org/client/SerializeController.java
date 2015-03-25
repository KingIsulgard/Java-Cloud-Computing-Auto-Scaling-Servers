package org.client;

import java.io.*;

import org.client.SimpleRunnable;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

/*
 * Deze klasse wordt gebruikt voor het serializeren van SimpleRunnable objecten voor te versturen naar de load balancer.
 */
public class SerializeController {
	public static String serialize(SimpleRunnable object) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(object);
		oos.flush();
		
		byte[] bytes = baos.toByteArray();
		char[] chars = Hex.encodeHex(bytes);
		
		return new String(chars);
	}
}
