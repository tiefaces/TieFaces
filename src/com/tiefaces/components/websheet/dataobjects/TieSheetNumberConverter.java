package com.tiefaces.components.websheet.dataobjects;

import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import com.tiefaces.components.websheet.TieWebSheetConstants;


@FacesConverter("tieSheetNumberConverter")
public class TieSheetNumberConverter implements Converter {

	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
		Double doubleValue = 0.0;
		String symbol = "";
		String strValue = (String) value;
		try {
			symbol = (String) component.getAttributes().get(TieWebSheetConstants.TIE_WEBSHEET_CELL_DATA_SYMBOL);
			
			if ( (symbol != null) && (symbol.equals("%") && strValue!=null) ) {
				strValue = strValue.trim();
				if (strValue.endsWith("%")) {
					doubleValue = Double.valueOf( strValue.substring(0, strValue.length() - 1)) / 100;
					strValue = doubleValue.toString();
			}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return strValue;

	}

	
	private String fmtNumber(double d)
	{
	    if(d == (long) d)
	        return String.format("%d",(long)d);
	    else
	        return String.format("%.2f",d);
	}	
	
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {

		String strValue = null;
		String symbol = "";
		try {
			
			strValue = (String) value;
			symbol = (String) component.getAttributes().get(TieWebSheetConstants.TIE_WEBSHEET_CELL_DATA_SYMBOL);
			
			if ( (symbol != null) && (symbol.equals("%")) && (value != null) && !((String) value).isEmpty() ) {
				
				Double doubleValue = Double.valueOf( (String) value) * 100;
				strValue = fmtNumber(doubleValue)+"%";
			}	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
			return strValue;
	}
				
}