package com.tiefaces.components.common.lookup;

import java.util.HashMap;
import java.util.Map;
import org.primefaces.context.RequestContext;
import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import com.tiefaces.common.TIEConstants;

@FacesComponent("tiedialogLookupComponent")
public class TieDialogLookupComponent extends UINamingContainer {

	public void openDialog() 
	{
	       Map<String,Object> options = new HashMap<String, Object>();   
	        options.put("modal", true);   
	        options.put("draggable", false);   
	        options.put("resizable", false);
	        options.put("contentHeight", "'100%'");
	        options.put("contentWidth", "'100%'");
	        options.put("height", getAttributes().get("dialogHeight"));
	        options.put("width", getAttributes().get("dialogWidth"));	        
	        
	        //hack 
	        //there's no way to pass object to dialog window
	        //so here just save it to session
	        //after retrieved in dialog , will clean it
	        
            Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            sessionMap.put(TIEConstants.TIE_DIALOG_ATTRS, getAttributes());	        
	        RequestContext.getCurrentInstance().openDialog("tieDialogTableLookup", options, null);
	}       

	}
