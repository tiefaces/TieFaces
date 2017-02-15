/*
 * Copyright 2017 TieFaces.
 * Licensed under MIT
 */
package org.tiefaces.components.websheet.serializable;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.EncryptedDocumentException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * serialize workbook.
 * 
 * @author Jason Jiang
 *
 */
public class SerialDataContext implements Serializable {

	/** logger. */
	private static final Logger LOG = Logger
			.getLogger(SerialDataContext.class.getName());

	/**
	 * serial id.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * data context map is transient.
	 */
	private transient Map<String, Object> dataContext;

	/** convert map to json string. */
	private String mapToJson;
	

	/**
	 * save the workbook before serialize.
	 * 
	 * @param out
	 *            outputstream.
	 * @throws IOException
	 *             io exception.
	 */
	private void writeObject(final java.io.ObjectOutputStream out)
			throws IOException {
		Gson objGson = new GsonBuilder().setPrettyPrinting().create();
		this.mapToJson = objGson.toJson(this.dataContext);
		out.defaultWriteObject();
	}

	/**
	 * load the workbook from saving.
	 * 
	 * @param in
	 *            inputstream.
	 * @throws IOException
	 *             io exception.
	 */
	private void readObject(final java.io.ObjectInputStream in)
			throws IOException {
		try {
			in.defaultReadObject();
			Gson objGson = new GsonBuilder().setPrettyPrinting().create();
			Type listType = new TypeToken<Map<String, Object>>() {
			}.getType();		
			this.dataContext = objGson.fromJson(mapToJson, listType);
		} catch (EncryptedDocumentException | ClassNotFoundException e) {
			LOG.log(Level.SEVERE,
					" error in readObject of serialWorkbook : "
							+ e.getLocalizedMessage(),
					e);
		}
	}

	/**
	 * Gets the data context.
	 *
	 * @return the dataContext
	 */
	public final Map<String, Object> getDataContext() {
		return dataContext;
	}

	/**
	 * Sets the data context.
	 *
	 * @param dataContext
	 *            the dataContext to set
	 */
	public final void setDataContext(Map<String, Object> dataContext) {
		this.dataContext = dataContext;
	}

	
	
}
