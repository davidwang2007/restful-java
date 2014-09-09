/**
 * user configuration javascript file
 * @author davidwang
 * @time 2014-7-27 15:15:01
 */
;(function(module){
	'use strict';
	module.config(['$stateProvider','$urlRouterProvider',function($stateProvider,$urlRouterProvider){
		$stateProvider.state('user',{
			abstract: true,
			url: '/user',
			templateUrl: '/html/user.html',
			resolve: {
				users: ['User',function(User){
					return User.query();
				}]
			},
			controller: ['$scope','users',function($scope,users){
				$scope.users = users;
			}]
		}).state('user.list',{
			url: '',
			templateUrl: '/html/user.list.html',
			controller: ['$scope',function($scope){
				$scope.$parent.$watch('filterName',function(newValue,oldValue){
					$scope.filterName = newValue;
				});
			}]
		}).state('user.detail',{
			url: '/{name:[a-zA-Z0-9]{1,20}}',
			views: {
				'':{
					templateUrl: '/html/user.detail.html',
					controller: ['$scope', '$stateParams', 'users','$state',
					             function (  $scope,   $stateParams,   users,$state) {
						$scope.user = users.filter(function(user){return user.name === $stateParams.name;})[0];//utils.findById($scope.users, $stateParams.id);
						$scope.edit = function(){
							console.log('you want to edit ',$scope.user.name);
							//$state.go('^.edit',{name: $scope.user.name});
							$state.go('user.list');
						}
					}]
				},
				'userTip':{
					templateProvider: ['$stateParams',function ($stateParams) {
						return '<div class="well">用户 : 【' + $stateParams.name+ '】 详情</div>';
					}]
				}
			}
		}).state('user.edit',{
			url: '/{name:[a-zA-Z0-9]{1,20}}/edit',
			views:{
				'':{
					templateUrl: '/html/user.edit.html',
					controller: ['$scope', '$stateParams', 'users',
					             function (  $scope,   $stateParams,   users) {
						$scope.user = users.filter(function(user){return user.name === $stateParams.name;})[0];//utils.findById($scope.users, $stateParams.id);
					}]
				},
				'userTip':{
					templateProvider: ['$stateParams',function($stateparams){
						return '<div class="well">用户 : 【' + $stateParams.name+ '】 编辑</div>';
					}]
				}
			}
		});
	}]);
	
})(angular.module('clock'));