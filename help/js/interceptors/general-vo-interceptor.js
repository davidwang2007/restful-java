/**
 * general vo interceptor 
 * @author David Wang
 * @time 2014-7-27 15:39:42
 */
;(function(module){
	'use strict';
	module.factory('interceptorGeneralVo',['$q','$window','$timeout','pageSize',function($q,$window,$timeout,pageSize){
		return {
			'request': function(config){
				return $q.when(config);
			},
			'response': function(config){
				//console.log(config.config.headers,config.headers('Content-Type'));
				var data = config.data;
				if(typeof data === 'object'){//表示返回的是json格式数据
					//data.push({id:1,username:'davidwang',realname:'王圣卫'});
					//console.log(data);
				}
				return $q.when(config);
			},
			'responseError': function(rejection){
				console.log('responseError: ',rejection);	
				return $q.reject(rejection);
			},
			'requestError': function(rejection){
				console.log('requestError: ',rejection);	
				return $q.reject(rejection);
			}
		};		
	}]);
})(angular.module('clock.base'));