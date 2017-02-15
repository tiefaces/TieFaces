var unsaved = false;
var savedStatus = false;

function setUnsavedState(state){
	unsaved = state;
}

function saveState(){
	savedStatus = unsaved;
	unsaved = false;
}

function restoreState(){
	unsaved = savedStatus;
}

var websheet_leave_warnning_msg = "Do you want to leave - data you have entered may not be saved?";
var confirmOnPageExit = function (e) 
{
	if(unsaved){
    // If we haven't been passed the event get the window.event
    e = e || window.event;

    // For IE6-8 and Firefox prior to version 4
    if (e) 
    {
        e.returnValue = websheet_leave_warnning_msg;
    }

    // For Chrome, Safari, IE8+ and Opera 12+
    return websheet_leave_warnning_msg;
	}
};
window.onbeforeunload = confirmOnPageExit;
