'use strict';
//设置日期的默认toJSON

Date.prototype.toJSON = function(){
	return this.getTime();
};

// Setting up route
angular.module('clock').config(['$stateProvider','$urlRouterProvider',function($stateProvider,$urlRouterProvider){
	// For unmatched routes:
	$urlRouterProvider.when('/user/:name','/user/:name').otherwise('/');
	$stateProvider.state('home',{
		url: '/',
		templateUrl: '/html/home.html',
		title: '首页'
	});
}]).config(['$locationProvider',function($locationProvider){
	$locationProvider.hashPrefix('!');		
	 //$locationProvider.html5Mode(true);
}]).config(['$httpProvider',function($httpProvider){
	$httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';		
	$httpProvider.interceptors.push('interceptor401');
	$httpProvider.interceptors.push('interceptorGeneralVo');

}]).constant('pageSize',10)
.run(['$rootScope','$state','$stateParams',function($rootScope,$state,$stateParams){
	$rootScope.$state = $state;
	$rootScope.$stateParams = $stateParams;
	$rootScope.$on('$stateChangeError', 
			function(event, toState, toParams, fromState, fromParams, error){ 
		console.log('$stateChangeError',arguments);
	});
	$rootScope.$on('$stateNotFound', 
			function(event, unfoundState, fromState, fromParams){ 
				console.log('$stateNotFound');
			    console.log(unfoundState.to); // "lazy.state"
			    console.log(unfoundState.toParams); // {a:1, b:2}
			    console.log(unfoundState.options); // {inherit:false} + default options
			})
}]);
