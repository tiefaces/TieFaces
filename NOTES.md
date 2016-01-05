Primefaces CellEditor

Try to use cell editor. Didn't success. Leave the notes for future developments.

Issue 1: tab (fixed)

Primefaces CellEditor require all the column contain <p:editor> tag, otherwise the "tab" key doesn't support well in Chrome browsers.

Fixes is override the js coming with datatable as:

//<![CDATA[
//@Override to fix tab issue in primefaces cell editor
PrimeFaces.widget.DataTable = PrimeFaces.widget.DataTable.extend({

//@Override
tabCell : function(cell, forward) {
	
	var targetCell;
	var currentCell = cell;
	do {
		targetCell = currentCell;
		do {
			targetCell = forward ? targetCell.next() : targetCell.prev();
		} while ((targetCell.length !=0 ) && (targetCell.find('.ui-cell-editor').length == 0));
		
	    if(targetCell.length == 0) {
	        var tabRow = forward ? currentCell.parent().next() : currentCell.parent().prev();
	        if (tabRow.length == 0) {
	        tabRow = forward ? currentCell.parent().parent().children(':first') : currentCell.parent().parent().children(':last');
	        }
	        if (tabRow.find('.ui-cell-editor')) {
	        	targetCell = forward ? tabRow.children('td.ui-editable-column:first') : tabRow.children('td.ui-editable-column:last');
	        } else {
	        	targetCell = forward ? tabRow.children('td.ui-editable-column:last') : tabRow.children('td.ui-editable-column:first');
	        }	
	    };
	    currentCell = targetCell;
	} while ((targetCell.length !=0 ) && (targetCell.find('.ui-cell-editor').length == 0));
    this.showCellEditor(targetCell);
}

});

Issue 2:  "CellEdit" event cannot constructed: error is null point.

Issue 3: Since issue 2 cannot fixed, try not trigger "CellEdit" event, instead use "Change" event with <p:inputText>.

(1) override savecell javascript
saveCell: function(cell) {

	this.viewMode(cell);    
    this.currentCell = null;
}

(2) use "Change" event

         <p:cellEditor id="celledit#{loop.index}"
			rendered="#{(!cc.attrs.readOnly) and (!empty dataRow.cells[loop.index].inputType) and ( dataRow.cells[loop.index].inputComponent == 'number' ) }"
          >
               <f:facet name="output"><h:outputText id="celloutp#{loop.index}" for="cellinpu#{loop.index}" value="#{cc.attrs.webSheetBean.cellsMap[''.concat(dataRow.rowIndex).concat(':').concat(dataRow.cells[loop.index].columnIndex).concat(':format')]}"
               style="width:96%" styleClass="inputColumnWidth inputLabel"
               /></f:facet>
               <f:facet name="input">
               <p:inputText id="cellinpu#{loop.index}" 
               value="#{cc.attrs.webSheetBean.cellsMap[''.concat(dataRow.rowIndex).concat(':').concat(dataRow.cells[loop.index].columnIndex)]}"  
               style="width:96%" 
               styleClass="inputColumnWidth" >
               					<p:ajax event="change" process="@this"
               							update="@parent"
										listener="#{cc.attrs.webSheetBean.valueChangeEvent}" />
									<f:attribute name="data-row" value="#{dataRow.rowIndex}" />
									<f:attribute name="data-column"
										value="#{dataRow.cells[loop.index].columnIndex}" />
               </p:inputText> </f:facet>
           </p:cellEditor>		
 

After apply (1) and (2), focus issue came out: if change text in column A, then press "Tab", focus will went to column C instead of Column B ( which suppose to be).
