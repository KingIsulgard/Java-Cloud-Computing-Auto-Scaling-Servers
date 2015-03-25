package org.webapp.controllers;

import java.io.*;
import java.net.URLClassLoader;
import java.util.concurrent.Callable;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.webapp.model.ClassLoaderObjectInputStream;

/*
 * Deze klasse wordt gebruikt voor het deserializeren van Callable onjecten via de URLClassLoader en het geserializeerde object
 */
public class SerializeController {
	public static Callable<String> deserialize(URLClassLoader classLoader, String objectString) throws IOException, ClassNotFoundException, DecoderException {
		byte[] bytes = Hex.decodeHex(objectString.toCharArray());
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ClassLoaderObjectInputStream ois = new ClassLoaderObjectInputStream(classLoader, bais);
		return (Callable<String>) ois.readObject();
	}
}
