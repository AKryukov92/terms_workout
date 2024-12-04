function smartGetSelection(){
    if (window.getSelection) {
        return window.getSelection();
    } else if (document.getSelection) {
        return document.getSelection();
    } else if (document.selection) {
        return document.selection.createRange().text;
    }
    return '';
}
function getContext(sel) {
    if (sel != null) {
        let fullText = sel.anchorNode.data;
        if (sel.anchorOffset > sel.extentOffset) {
            let extStart = sel.extentOffset > addLetters ? sel.extentOffset - addLetters : 0;
            let extEnd = sel.anchorOffset < fullText.length - addLetters ? sel.anchorOffset + addLetters : fullText.length;
            return fullText.substring(extStart, extEnd);
        } else if (sel.anchorOffset < sel.extentOffset) {
            let extStart = sel.anchorOffset > addLetters ? sel.anchorOffset - addLetters : 0;
            let extEnd = sel.extentOffset < fullText.length - addLetters ? sel.extentOffset + addLetters : fullText.length;
            return fullText.substring(extStart, extEnd);
        }
    }
    return '';
}

$("#fixMin").click(function(){
    let sel = smartGetSelection();
    $("#min_attempt").text(sel.toString());
    $("#max_context").val(getContext(sel));
});
$("#fixMax").click(function(){
    let sel = smartGetSelection();
    $("#max_attempt").text(sel.toString());
    $("#max_context").val(getContext(sel));
});

$("#needle").change(function(){
    $.ajax({
        url:"/highlight",
        data: {
            haystack_id:$("#haystack_id").val(),
            needle:$("#needle").val()
        },
        success: function(response){
            $("#haystack").html(response);
        }
    })
})