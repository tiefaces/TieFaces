package org.tiefaces.components.websheet.dataobjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * static snapshot for current map object.
 * 
 * This is mainly used for save the snapshot of context for each 
 * @author Jason Jiang
 *
 */
public class MapSnapShot {
	
	private List<MapObject> snapList = new ArrayList<MapObject>();
	
	public MapSnapShot(Map<String, Object> context) {
		
		for (Map.Entry<String, Object> entry : context.entrySet())
		{
			snapList.add(new MapObject(entry.getKey(), entry.getValue()));
		}
	}
	
	public List<MapObject> getSnapList() {
		return snapList;
	}
	
}
