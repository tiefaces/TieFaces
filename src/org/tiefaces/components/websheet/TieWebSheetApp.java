/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * Application scope bean.
 * Hold instance for share objects across sessions.
 * @author Jason Jiang
 *
 */
@ManagedBean(eager = true)
@ApplicationScoped
public class TieWebSheetApp {
	/** script engine. */
	private ScriptEngine engine;

	/** initialize. */
	@PostConstruct
	public final void init() {

		setEngine(new ScriptEngineManager().getEngineByName("JavaScript"));

	}

	public final ScriptEngine getEngine() {
		return engine;
	}

	public final void setEngine(final ScriptEngine pEngine) {
		this.engine = pEngine;
	}

}
