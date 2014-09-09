/**
 * user service configuration file
 * @author davidwang
 * @time 2014-7-27 15:24:40
 */
;(function(module){
	'user strict';
	module.factory('User',['$resource',function($resource){
		return $resource('/user/:id',{id:'@id'},{
			update: {method:'put'}
		});
	}]);
})(angular.module('clock.system'));