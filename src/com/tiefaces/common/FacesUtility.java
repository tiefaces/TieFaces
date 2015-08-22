/*
 * Copyright 2015 TieFaces.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.tiefaces.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.faces.context.FacesContext;

public final class FacesUtility {

	private static final int DEFAULT_STREAM_BUFFER_SIZE = 10240;
	

	public static Set<String> getResourcePaths(String path) {
		return getResourcePaths(getContext(), path);
	}	

	public static Set<String> getResourcePaths(FacesContext context, String path) {
		return context.getExternalContext().getResourcePaths(path);
	}	
	
	public static InputStream getResourceAsStream( String path) {
		return getResourceAsStream(getContext(), path);
	}

	public static InputStream getResourceAsStream(FacesContext context, String path) {
		return context.getExternalContext().getResourceAsStream(path);
	}
	
	public static Map<String, Object> getMetadataAttributes() {
		return getMetadataAttributes(getContext());
	}
	public static Map<String, Object> getMetadataAttributes(FacesContext context) {
		return context.getViewRoot().getAttributes();
	}
	
	public static <T> T evaluateExpressionGet(String expression) {
		return evaluateExpressionGet(getContext(), expression);
	}

	@SuppressWarnings("unchecked")
	public static <T> T evaluateExpressionGet(FacesContext context, String expression) {
		if (expression == null) {
			return null;
		}

		return (T) context.getApplication().evaluateExpressionGet(context, expression, Object.class);
	}
	
	public static FacesContext getContext() {
		return FacesContext.getCurrentInstance();
	}

	public static byte[] toByteArray(InputStream input) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		stream(input, output);
		return output.toByteArray();
	}
	public static long stream(InputStream input, OutputStream output) throws IOException {
		try (ReadableByteChannel inputChannel = Channels.newChannel(input);
			WritableByteChannel outputChannel = Channels.newChannel(output))
		{
			ByteBuffer buffer = ByteBuffer.allocateDirect(DEFAULT_STREAM_BUFFER_SIZE);
			long size = 0;

			while (inputChannel.read(buffer) != -1) {
				buffer.flip();
				size += outputChannel.write(buffer);
				buffer.clear();
			}

			return size;
		}
	}
	
	public static String removePrefixPath(final String prefix, final String resource) {
		String normalizedResource = resource;
		if (normalizedResource.startsWith(prefix)) {
			normalizedResource = normalizedResource.substring(prefix.length() - 1);
		}

		return normalizedResource;
	}	
	
	
	public static boolean evalInputType(String input, String type) {
		
		Scanner scanner = new Scanner(input);
		if (type.equalsIgnoreCase("Integer")) {
			return scanner.hasNextInt();
		} else if (type.equalsIgnoreCase("Double")) {
			return scanner.hasNextDouble();
		} else if (type.equalsIgnoreCase("Boolean")) {
			return scanner.hasNextBoolean();
		} else if (type.equalsIgnoreCase("Byte")) {
			return scanner.hasNextByte();
		}else if (type.toLowerCase().startsWith("text")) {
			return true;
		}
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T findBean(String beanName) {
	    FacesContext context = FacesContext.getCurrentInstance();
	    return (T) context.getApplication().evaluateExpressionGet(context, "#{" + beanName + "}", Object.class);
	}
	
    public static <T> T evaluateExpression(String expression, Class<? extends T> expected) {
        return evaluateExpression(FacesContext.getCurrentInstance(), expression, expected);
    }

    public static <T> T evaluateExpression(FacesContext context, String expression, Class<? extends T> expected) {
        return context.getApplication().evaluateExpressionGet(context, expression, expected);
    }	
	
	public static  String strJoin(short[] aArr, String sSep) {
	    StringBuilder sbStr = new StringBuilder();
	    for (int i = 0, il = aArr.length; i < il; i++) {
	        if (i > 0)
	            sbStr.append(sSep);
	        sbStr.append(aArr[i]);
	    }
	    return sbStr.toString();
	}
    
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}	
}
