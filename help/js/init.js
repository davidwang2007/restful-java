'use strict';
angular.element(document).ready(function(){
	if(window.location.hash !== '#!')	window.location.hash = '#!';
	angular.bootstrap(document,['clock']);
});
