$(document).keypress(function(e){
    if(e.which == 13){
        var attemptText = $("#attempt").text();
        if (attemptText){
            $("#attempt_form").submit();
        }
    }
});
$(document).mouseup(function(){
	let sel = null;
	let context = '';
	let addLetters = 4;
    if (window.getSelection) {
        sel = window.getSelection();
    } else if (document.getSelection) {
        sel = document.getSelection();
    } else if (document.selection) {
        sel = document.selection.createRange().text;
    }
    if (sel != null) {
        let fullText = sel.anchorNode.data;
        if (sel.anchorOffset > sel.extentOffset) {
            let extStart = sel.extentOffset > addLetters ? sel.extentOffset - addLetters : 0;
            let extEnd = sel.anchorOffset < fullText.length - addLetters ? sel.anchorOffset + addLetters : fullText.length;
            $("#attempt").text(sel);
            $("#context").val(fullText.substring(extStart, extEnd));
        } else if (sel.anchorOffset < sel.extentOffset) {
            let extStart = sel.anchorOffset > addLetters ? sel.anchorOffset - addLetters : 0;
            let extEnd = sel.extentOffset < fullText.length - addLetters ? sel.extentOffset + addLetters : fullText.length;
            $("#attempt").text(sel);
            $("#context").val(fullText.substring(extStart, extEnd));
        }
    }
});