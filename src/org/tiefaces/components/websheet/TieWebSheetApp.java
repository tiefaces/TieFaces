/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package org.tiefaces.components.websheet;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

/**
 * Application scope bean.
 * Hold instance for share objects across sessions.
 * @author Jason Jiang
 *
 */
@ManagedBean(eager = true)
@ApplicationScoped
public class TieWebSheetApp {
	
/* remove script engine.	
	private ScriptEngine engine;
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
*/	

}
