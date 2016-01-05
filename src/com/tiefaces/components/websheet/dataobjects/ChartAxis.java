package com.tiefaces.components.websheet.dataobjects;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTCatAx;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTValAx;

public class ChartAxis {

	private boolean debug = true;

	private void debug(String msg) {
		if (debug) {
			System.out.println("debug chartAxis: " + msg);
		}
	}
	
	private String position;
	
	private String orientation;
	
	private String title;

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getOrientation() {
		return orientation;
	}

	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ChartAxis(CTCatAx ctCatAx) {
		super();
		try {
			this.position = ctCatAx.getAxPos().getVal().toString();
		} catch (Exception ex) 
		{
			debug("cannot get axpos from CtCatAx");
		}
		try {
			this.orientation = ctCatAx.getScaling().getOrientation().getVal().toString();
		} catch (Exception ex) 
		{
			debug("cannot get scaling.orientation from CtCatAx");
		}
		try {
			this.title = ctCatAx.getTitle().getTx().getRich().getPList().get(0).getRList().get(0).getT();
		} catch (Exception ex) 
		{
			debug("cannot get title from CtCatAx");
		}
	}

	public ChartAxis(CTValAx ctValAx) {
		super();
		try {
			this.position = ctValAx.getAxPos().getVal().toString();
		} catch (Exception ex) 
		{
			debug("cannot get AxPos from CtValAx");
		}
		try {
			this.orientation = ctValAx.getScaling().getOrientation().getVal().toString();
		} catch (Exception ex) 
		{
			debug("cannot get scaling.orientation from CtValAx");
		}
		try {
			this.title = ctValAx.getTitle().getTx().getRich().getPList().get(0).getRList().get(0).getT();
		} catch (Exception ex) 
		{
			debug("cannot get title from CtValAx");
		}
	}
	

}
