$(document).ready(function(){
    $('pre').each(function(i, block) {
        hljs.highlightBlock(block);
    });
});
