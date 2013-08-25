var PM_onLoadFunctions = new Array();

/**
 * Register a function to excecute on page load.
 * @param func The function
 */
function jpmLoad(func) {
    PM_onLoadFunctions.push(func);
}