package com.tiefaces.components.websheet.service;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.DateTimeConverter;
import javax.faces.convert.FacesConverter;

@FacesConverter("tieCalendaConverter")
public class DateTimeCustomConverter implements Converter{
 @Override
 public Object getAsObject(FacesContext context, UIComponent component, String value) {
  if(value == null){
   return null;
  }
  String pattern = (String) component.getAttributes().get("pattern");
  SimpleDateFormat formatter = new SimpleDateFormat(pattern, getLocale(context, component));
  try{
	  return formatter.parse(value);
  }catch(Exception e){
	  throw new ConverterException(e.getLocalizedMessage());	  
  }
 }
 
 public String getAsString(FacesContext context, UIComponent component, Object value) {
  if(value == null){
   return "";
  }
  if (value instanceof String) {
	  return (String) value;
  }
  if (context == null || component == null) {
   throw new NullPointerException();
  }
  
  try {
	   String pattern = (String) component.getAttributes().get("pattern");
	   SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, getLocale(context, component));
	   return dateFormat.format(value);

	  } catch  (Exception e) {
	   throw new ConverterException(e.getLocalizedMessage());
  }
 }
 
 private Locale getLocale(FacesContext context, UIComponent component) {

	 String locale = (String) component.getAttributes().get("locale");
	 
     if (locale == null) {
    	 return context.getViewRoot().getLocale();
     }
     return stringToLocale(locale);

 }
 
 private Locale stringToLocale(String s) {
	    StringTokenizer tempStringTokenizer = new StringTokenizer(s,",");
	    if(tempStringTokenizer.hasMoreTokens());
	    String l = (String) tempStringTokenizer.nextElement();
	    if(tempStringTokenizer.hasMoreTokens());
	    String c = (String) tempStringTokenizer.nextElement();
	    return new Locale(l,c);
	}
}