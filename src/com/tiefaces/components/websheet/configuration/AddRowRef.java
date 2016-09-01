package com.tiefaces.components.websheet.configuration;

public class AddRowRef {
	
	EachCommand eachCommand;
	int index;
	public AddRowRef(EachCommand eachCommand, int index) {
		super();
		this.eachCommand = eachCommand;
		this.index = index;
	}
	public EachCommand getEachCommand() {
		return eachCommand;
	}
	public void setEachCommand(EachCommand eachCommand) {
		this.eachCommand = eachCommand;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	
	

}
