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

var leinOptions = document.getElementsByClassName("lein");
var bootOptions = document.getElementsByClassName("boot");
var buildToolDiv = document.getElementById("build-tool-div");
var buildTool = document.getElementById("build-tool");

function hideOpts(optArray) {
    for (opt of optArray) {
        opt.style.display = "none";
    }
}

function showOpts(optArray) {
    for (opt of optArray) {
        opt.style.display = "inline";
    }
}

function setBuildTool(toolName) {
    if (toolName === "boot") {
        hideOpts(leinOptions);
        showOpts(bootOptions);
    } else {
        hideOpts(bootOptions);
        showOpts(leinOptions);
    }
    saveCookie("build-tool", toolName);
}

buildTool.addEventListener("change", function () {
    setBuildTool(buildTool.options[buildTool.selectedIndex].value);
}, false);
setBuildTool(buildTool.options[buildTool.selectedIndex].value);


function saveCookie(name, value) {
    document.cookie = name + "=" + value + "; path=/";
}

function readCookie(name) {
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for(c of ca) {
        while (c.charAt(0)==' ') c = c.substring(1,c.length);
        if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
    }
    return null;
}

var cookie = readCookie("build-tool");
setBuildTool(cookie);
buildTool.selectedIndex = cookie === "lein" ? 0 : 1;
