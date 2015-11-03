/*
 * Copyright 2015 TieFaces.
 * Licensed under MIT
 */

package com.tiefaces.components.common.localeswitcher;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.tiefaces.common.TIEConstants;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class TieLocaleBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private Locale locale;
	private String localeCode;
	private String localePath;

	private List<SelectItem> localeList;

	private String switchLabel;

	public TieLocaleBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	@PostConstruct
	public void init() {
		localeCode = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestLocale().toString();
		if ((localeCode != null) && (localeCode.length() > 1))
			localeCode = localeCode.substring(0, 2);
		else
			localeCode = TIEConstants.LANG_ENG;

		setLocale(localeCode);
		localeList = new ArrayList<SelectItem>();
		localeList.add(new SelectItem(TIEConstants.LANG_ENG,
				TIEConstants.LANG_ENG_LABEL));
		localeList.add(new SelectItem(TIEConstants.LANG_FR,
				TIEConstants.LANG_FR_LABEL));
	}

	public String getLocaleCode() {
		return localeCode;
	}

	public void setLocaleCode(String localeCode) {
		this.localeCode = localeCode;
	}

	public String getLocalePath() {
		if (localeCode.equalsIgnoreCase(TIEConstants.LANG_ENG))
			localePath = "";
		else
			localePath = "_" + localeCode.toLowerCase();
		return localePath;
	}

	public void setLocalePath(String localePath) {
		this.localePath = localePath;
	}

	public String getSwitchLabel() {
		switchLabel = findSwitchLabel();
		return switchLabel;
	}

	public void setSwitchLabel(String switchLabel) {
		this.switchLabel = switchLabel;
	}

	public List<SelectItem> getLocaleList() {
		return localeList;
	}

	public void setLocaleList(List<SelectItem> localeList) {
		this.localeList = localeList;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	private String findSwitchLabel() {

		if (locale == null)
			init();
		for (SelectItem s : localeList) {
			if (!locale.toString().equalsIgnoreCase(s.getValue().toString())) {
				return s.getLabel();
			}
		}
		return "";
	}

	public void localeChanged(ValueChangeEvent e) {
		if (e != null && e.getNewValue() != null) {
			String newValue = e.getNewValue().toString();
			for (SelectItem s : localeList) {
				if (s.getValue().toString().equalsIgnoreCase(newValue)) {
					setLocale(newValue);
					return;
				}
			}
		}
	}

	private void setLocale(String newValue) {
		Locale newLocale = new Locale(newValue);
		this.setLocaleCode(newValue);
		this.setLocale(newLocale);
		FacesContext.getCurrentInstance().getViewRoot().setLocale(newLocale);
	}

	public void switchAction() {
		if (locale == null)
			init();
		String newValue = null;
		for (SelectItem s : localeList) {
			newValue = (String) s.getValue();
			if (!locale.toString().equalsIgnoreCase(newValue)) {
				setLocale(newValue);
				return;
			}
		}

	}

}
