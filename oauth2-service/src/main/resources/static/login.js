// $("[role='tab']").click(function () {
//     debugger;
//     var tabTitle = $(this);
//     var tabIndex = tabTitle.index();
//     if (!tabTitle.hasClass("ant-tabs-tab-active")) {
//         tabTitle.attr("aria-selected", true).addClass("ant-tabs-tab-active");
//         tabTitle.siblings().attr("aria-selected", false).removeClass("ant-tabs-tab-active");
//         var selectedTabPanel = $($("[role='tabpanel']")[tabIndex]);
//         selectedTabPanel.attr("aria-hidden", false).removeClass("ant-tabs-tabpane-inactive").addClass("ant-tabs-tabpane-active");
//         selectedTabPanel.siblings().attr("aria-hidden", true).removeClass("ant-tabs-tabpane-active").addClass("ant-tabs-tabpane-inactive");
//     }
// });

$(".ant-checkbox").click(function () {
    var autoLogin = $(this);
    if (autoLogin.hasClass("ant-checkbox-checked")) {
        autoLogin.removeClass("ant-checkbox-checked");
    } else {
        autoLogin.addClass("ant-checkbox-checked");
    }
});
