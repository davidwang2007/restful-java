/**
 * 自定义过滤器
 * */
'use strict';
angular.module('clock.system').factory('interceptor401',['$q','$window','$timeout','pageSize',function($q,$window,$timeout,pageSize){
	return {
		'request': function(config){
			if(config.params && config.params.needPage){
				config.params.pageIndex = config.params.pageIndex || 1;
				config.params.pageSize = config.params.pageSize || pageSize;
			}
			return $q.when(config);
		},
		'response': function(config){
			return $q.when(config);
		},
		'responseError': function(rejection){
			console.log('responseError: ',rejection);	
			if(rejection.status == 401){//表示没有权限
				$window.alert(rejection.data.message);
				$window.location.href='/';
			}
			return $q.reject(rejection);
		},
		'requestError': function(rejection){
			console.log('requestError: ',rejection);	
			return $q.reject(rejection);
		}
	};		
}]);
