/**
 * @ngdoc function
 * @name yapp.controller:MainCtrl
 * @description # MainCtrl Controller of yapp
 */
angular.module('yapp').controller('LoginCtrl',  ['$scope', '$rootScope', '$location', 'AuthenticationService',
	    function ($scope, $rootScope, $location, AuthenticationService) {
    // reset login status
        AuthenticationService.ClearCredentials();
 
        $scope.submit = function () {
            $scope.dataLoading = true;
            AuthenticationService.Login($scope.openstack_server, $scope.username, $scope.password, function(response) {
                if(response.data) {
                    AuthenticationService.SetCredentials($scope.openstack_server, $scope.username, $scope.password);
                	$location.path('/dashboard');
            } else {
                $scope.error = response.message;
                $scope.dataLoading = false;
                alert(response.message);
            }
        });
    };
    
	function statusChangeCallback(response) 
    {
				console.log('statusChangeCallback');
				console.log(response);
				// The response object is returned with a status field that lets the
				// app know the current login status of the person.
				// Full docs on the response object can be found in the documentation
				// for FB.getLoginStatus().
				if (response.status === 'connected') {
					// Logged into your app and Facebook.
					testAPI();
                    
                    
				} else if (response.status === 'not_authorized') {
					// The person is logged into Facebook, but not your app.
					document.getElementById('status').innerHTML = 'Please log ' +
						'into this app.';
				} //else {
					// The person is not logged into Facebook, so we're not sure if
					// they are logged into this app or not.
					//document.getElementById('status').innerHTML = 'Please log ' +
						//'into Facebook.';
			//	}
			
        }
            
window.checkLoginState=function() 
{
    FB.getLoginStatus(function(response)
    {
			statusChangeCallback(response);
    });
       
}
        
window.fbAsyncInit= function()
{
	FB.init({
		appId      : '1822332461383151',
		cookie     : true,  // enable cookies to allow the server to access
												// the session
		xfbml      : true,  // parse social plugins on this page
		version    : 'v2.8' // use graph api version 2.8
	});

    FB.getLoginStatus(function(response) 
    {
		statusChangeCallback(response); 
	}); 
};

// Load the SDK asynchronously
(function(d, s, id) {
	var js, fjs = d.getElementsByTagName(s)[0];
	if (d.getElementById(id)) return;
	js = d.createElement(s); js.id = id;
	js.src = "//connect.facebook.net/en_US/sdk.js";
	fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));

// Here we run a very simple test of the Graph API after login is
// successful.  See statusChangeCallback() for when this call is made.
function testAPI() 
{
	console.log('Welcome!  Fetching your information.... ');
	FB.api('/me', function(response) {
		console.log('Successful login for: ' + response.name);
        //window.location = "#/dashboard/overview";
        $scope.dataLoading = true;
        AuthenticationService.FBLogin($scope.openstack_server, $scope.username, $scope.password, response.id, function(response) {
        	 if(response.status == "true"){
                AuthenticationService.SetCredentials($scope.openstack_server, $scope.username, $scope.password, $scope.arg1, $scope.arg2);
            	$location.path('/dashboard');
        } else {
            $scope.error = response.message;
            $scope.dataLoading = false;
            alert(response.message);
        }
    });
	});
}


function onSuccess(googleUser) {
    console.log('Logged in as: ' + googleUser.getBasicProfile().getName());
  }
  function onFailure(error) {
    console.log(error);
  }
  function renderButton() {
    gapi.signin2.render('my-signin2', {
	  'scope': 'profile email',
	  'width': 140,
	  'height': 40,
	  'longtitle': false,
	  'theme': 'dark',
	  'onsuccess': onSuccess,
	  'onfailure': onFailure
    });
  }
 
}]);
 