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

$("#fixMin").click(function(){
    $("#min_attempt").text(smartGetSelection());
});
$("#fixMax").click(function(){
    $("#max_attempt").text(smartGetSelection());
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