/**
 * Created by david on 2014-4-1
 * */
'use strict';
angular.module('clock',['clock.system']);
angular.module('clock.system',['clock.base']);

angular.module('clock.base',['ngCookies','ngResource','ui.bootstrap','ui.router','ngAnimate']);
