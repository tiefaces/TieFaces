package com.tiefaces.components.common.lookup;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;

import com.tiefaces.common.TIEConstants;

@ManagedBean
@ViewScoped
public class TieDialogLookupBean {

	private Map<String, Object> attrs;

	public TieDialogLookupBean() {
		super();
	}

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void postinit() {
		Map<String, Object> sessionMap = FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap();
		
		attrs = (Map<String, Object>) sessionMap
				.get(TIEConstants.TIE_DIALOG_ATTRS);
		sessionMap.remove(TIEConstants.TIE_DIALOG_ATTRS);
	}

	public Map<String, Object> getAttrs() {
		return attrs;
	}

	public void selectResultFromDialog(Object result) {
		RequestContext.getCurrentInstance().closeDialog(result);
	}

	
}
