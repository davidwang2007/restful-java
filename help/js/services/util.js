/**
 * util helper
 * @author D.W.
 */
'use strict';
angular.module('clock.base')
	.factory('utils',['$log','$rootScope',function($log,$rootScope){
		
		//考虑到提示框时有可能单击回退与前进操作，这时候要取消
		var canJump = true;//指示是否可回退
		$rootScope.$on('$stateChangeStart', function(event, toState, toParams, fromState, fromParams){ 
			canJump || event.preventDefault(); 
		});
		var body = angular.element('body');
		
		return {
			/**根据ID找出实体类*/
			findById: function(arr,id){
				var $arr = arr.filter(function(entity){
					return entity.id == id;
				});
				if($arr.length != 1){
					$log.warn(['try to find entity according id[',id,'],but result count = ',$arr.length]);
				}
				return $arr[0];
			},
			/**CAN CHANGE STATE[ROUTE]*/
			enableJump: function(){canJump = true;},
			/**CAN NOT CHANGE STATE[ROUTE]*/
			disableJump: function(){canJump = false;},
			/**CAN SCROLL THE DOCUMENT*/
			enableScroll: function(){body.removeClass('stop-scrolling');},
			/**CAN  NOT SCROLL THE DOCUMENT*/
			disableScroll: function(){body.addClass('stop-scrolling');}
		};
	}]);

