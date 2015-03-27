angular.module("ngWorksControllers", ['ui.router', 'angularFileUpload'])
.config(function($stateProvider, $urlRouterProvider, $httpProvider) {
	$urlRouterProvider.otherwise("/");

	$stateProvider
	.state("home", {
		url: "/",
		templateUrl: "home.html",
		controller: "homeController"
	})
	.state("login", {
		url: "/login",
		templateUrl: "login.html",
		controller: "navigationController"
	})
	.state("fileUpload", {
		url: "/fileUpload",
		templateUrl: "partials/fileUpload.part.html",
		controller: "fileUploadController"
	})
	$httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';

})
.controller('fileUploadController',['$scope', 'FileUploader', function($scope, FileUploader){
	var uploader = $scope.uploader = new FileUploader({
		url: 'resource/upload'
	});
	
	uploader.filters.push({
        name: 'customFilter',
        fn: function(item /*{File|FileLikeObject}*/, options) {
        	console.info(item.fileName);
            return this.queue.length < 10 && item.fileName.match(/\.(jpg|jpeg|png|gif)$/);
        }
    });
	// CALLBACKS

    uploader.onWhenAddingFileFailed = function(item /*{File|FileLikeObject}*/, filter, options) {
        console.info('onWhenAddingFileFailed', item, filter, options);
    };
    uploader.onAfterAddingFile = function(fileItem) {
        console.info('onAfterAddingFile', fileItem);
    };
    uploader.onAfterAddingAll = function(addedFileItems) {
        console.info('onAfterAddingAll', addedFileItems);
    };
    uploader.onBeforeUploadItem = function(item) {
        console.info('onBeforeUploadItem', item);
    };
    uploader.onProgressItem = function(fileItem, progress) {
        console.info('onProgressItem', fileItem, progress);
    };
    uploader.onProgressAll = function(progress) {
        console.info('onProgressAll', progress);
    };
    uploader.onSuccessItem = function(fileItem, response, status, headers) {
        console.info('onSuccessItem', fileItem, response, status, headers);
    };
    uploader.onErrorItem = function(fileItem, response, status, headers) {
        console.info('onErrorItem', fileItem, response, status, headers);
    };
    uploader.onCancelItem = function(fileItem, response, status, headers) {
        console.info('onCancelItem', fileItem, response, status, headers);
    };
    uploader.onCompleteItem = function(fileItem, response, status, headers) {
        console.info('onCompleteItem', fileItem, response, status, headers);
    };
    uploader.onCompleteAll = function() {
        console.info('onCompleteAll');
    };
	
}])
.controller('homeController', function($scope, $http) {
	$http.get('resource/home').success(function(data) {
		$scope.greeting = data;
	})
})
.controller('navigationController', function($rootScope, $scope, $http, $location) {

	var authenticate = function(callback) {

		$http.get('user').success(function(data) {
			if (data.name) {
				$rootScope.authenticated = true;
			} else {
				$rootScope.authenticated = false;
			}
			callback && callback();
		}).error(function() {
			$rootScope.authenticated = false;
			callback && callback();
		});
	}

	authenticate();
	$scope.credentials = {};

	$scope.login = function() {
		$http.post('login', $.param($scope.credentials), {
			headers : {
				"content-type" : "application/x-www-form-urlencoded"
			}
		}).success(function(data) {
			authenticate(function() {
				if ($rootScope.authenticated) {
					$location.path("/");
					$scope.error = false;
				} else {
					$location.path("/login");
					$scope.error = true;
				}
			});
		}).error(function(data) {
			$location.path("/login");
			$scope.error = true;
			$rootScope.authenticated = false;
		})
	};
	$scope.logout = function() {
		$http.post('logout', {}).success(function() {
			$rootScope.authenticated = false;
			$location.path("/");
		}).error(function(data) {
			$rootScope.authenticated = false;
		});
	};
});