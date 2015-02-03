if (window.innerWidth <= 1000) {
    var article = document.getElementById("doc-article");
    var sidebar = document.getElementById("doc-sidebar");
    var sidebarContent = document.getElementById("doc-sidebar-content");
    var expander = document.getElementById("expander");
    var articleSmallClass = "col-md-9 article";
    var articleLargeClass = "col-md-11 article";

    var sidebarSmallClass = "col-md-1 sidebar";
    var sidebarLargeClass = "col-md-3 sidebar";

    var collapseIcon = "fa fa-angle-double-right";
    var expandIcon = "fa fa-angle-double-left";

    var toggleSidebar = function () {
        if (article.className == articleLargeClass) {
            article.className = articleSmallClass;
            sidebar.className = sidebarLargeClass;
            sidebar.style.maxWidth = null;
            sidebarContent.style.display = "block";
            expander.className = collapseIcon;
        } else {
            article.className = articleLargeClass;
            sidebar.className = sidebarSmallClass;
            sidebar.style.maxWidth = "100px";
            expander.className = expandIcon;
            sidebarContent.style.display = "none";

        }
    };
    expander.addEventListener('touchstart', toggleSidebar);
}