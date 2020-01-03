$(document).keypress(function(e){
    if(e.which == 13){
        var attemptText = $("#attempt").text();
        if (attemptText){
            $("#attempt_form").submit();
        }
    }
});
$(document).mouseup(function(){
	var t = '';
    if (window.getSelection) {
        t = window.getSelection();
    } else if (document.getSelection) {
        t = document.getSelection();
    } else if (document.selection) {
        t = document.selection.createRange().text;
    }
    $("#attempt").text(t);
});