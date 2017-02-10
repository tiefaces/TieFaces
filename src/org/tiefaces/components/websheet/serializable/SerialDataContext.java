/*
 * Copyright 2015 TieFaces.
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
	 * 
	 */
	public SerialDataContext() {
		super();
		LOG.log(Level.INFO, "serial data context constructor");
	}

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
		LOG.log(Level.INFO, "serial data context start convert map object to json string");
		Gson objGson = new GsonBuilder().setPrettyPrinting().create();
		this.mapToJson = objGson.toJson(this.dataContext);
		LOG.log(Level.INFO, "serial data context start default write objects");
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
			LOG.log(Level.INFO,
					"serial data context start default read objects");
			in.defaultReadObject();
			
			LOG.log(Level.INFO, "serial data context start convert json string to map");
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
	 * @return the dataContext
	 */
	public final Map<String, Object> getDataContext() {
		return dataContext;
	}

	/**
	 * @param dataContext the dataContext to set
	 */
	public final void setDataContext(Map<String, Object> dataContext) {
		this.dataContext = dataContext;
	}

	
	
}
