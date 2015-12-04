function clickLabel() {
	console.log(' this class = '+this.attr('class'));
	this.click();
}
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
    
