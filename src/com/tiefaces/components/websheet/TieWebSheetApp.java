/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package com.tiefaces.components.websheet;


import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;


@ManagedBean(eager=true)
@ApplicationScoped
public class TieWebSheetApp {

	private ScriptEngine engine;
	
	@PostConstruct
	public void init() {
		
		setEngine(new ScriptEngineManager().getEngineByName("JavaScript"));  
		
	}

	public ScriptEngine getEngine() {
		return engine;
	}

	public void setEngine(ScriptEngine engine) {
		this.engine = engine;
	}
	
}
