/**
 * header controller
 * 生成页面菜单
 * author: D.W.
 * date: 2014-4-28 14:04:15
 * */

;(function(sys){
	'use strict';
	sys.controller('HeaderController',['$http','$scope',function($http,$scope){
		$scope.username = 'D.W.';
	}]);
})(angular.module('clock.system'));