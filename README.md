# BrowserID Verifier   [![Build Status](https://snap-ci.com/user454322/browserid-verifier/branch/master/build_image)](https://snap-ci.com/user454322/browserid-verifier/branch/master)

This library allows to easily authenticate Java web application's users by veryfing BrowserID assertions.


BrowserID Verifier is a simple Java verifier library for the [BrowserID protocol](https://github.com/mozilla/id-specs/blob/prod/browserid/index.md) but, it has been tested only with [Mozilla Persona](https://login.persona.org/about).

The only external dependencies are SLF4J and json.org<sup>1</sup>. 


To use it, just write something like:
```java
BrowserIDResponse loginRepsonse = verifier.verify(assertion, AUDIENCE);
```

Find a simple, yet complete [live sample here](https://browseridverifiersample-user454322.rhcloud.com) with its [source code](https://github.com/user454322/browserid-verifier/tree/master/sample/app).

## How to use it
### 1. Add it as a dependency
 Its Maven coordinate is `info.modprobe:browserid-verifier:<version>`; in a pom file it would look like:
```xml
   <dependency>
      <groupId>info.modprobe</groupId>
      <artifactId>browserid-verifier</artifactId>
      <version>0.2</version>
    </dependency>
```

[Example](https://github.com/user454322/browserid-verifier/blob/master/sample/app/pom.xml#L19-23)



### 2. Use it
In the server side:
```java
final Verifier verifier = new Verifier();
final BrowserIDResponse personaResponse = verifier.verify(assertion, audience);
final Status status = personaResponse.getStatus();

if (status == Status.OK) {
	/* Authentication with Persona was successful */
	final String email = personaResponse.getEmail();
	log.info("Signing in {}", email);
	HttpSession session;
	if ((session = req.getSession(false)) != null) {
		// Prevent session hijacking
		session.invalidate();
	}
	session = req.getSession(true);	
	session.setAttribute("email", email);

} else {
	/* Authentication with Persona failed */
	log.info("Sign in failed: {}", personaResponse.getReason());
}
```
[Example](https://github.com/user454322/browserid-verifier/blob/master/sample/app/src/main/java/info/modprobe/browserid/sample/servlet/In.java#L42-63)


　　 

In the client side:
```javascript
<button type="button" onclick="navigator.id.request();" >Sign in - Sign up</button>
<button type="button" onclick="navigator.id.logout();" >Sign out</button>
....
	<script src="https://login.persona.org/include.js"></script>

	<script type="text/javascript">
		var currentUser = '${sessionScope.email}';
		if(!currentUser) {
			// If falsy set it to the literal null
			currentUser = null;
		}

		navigator.id.watch({
			loggedInUser : currentUser,
			onlogin : function(assertion) {				
				loginRequest = $.ajax({
					type : 'POST',
					url : 'in',
					data : {
						assertion : assertion
					}
				});
				loginRequest.done(function(res, status, xhr) {
					window.location.reload();
				});
				loginRequest.fail(function(xhr, status, error) {
					navigator.id.logout();
					alert("Login error: " + error);
				});
			},

			onlogout : function() {
				logoutRequest = $.ajax({
					type : 'POST',
					url : 'out'
				});
				logoutRequest.done(function(res, status, xhr) {
					window.location.reload();
				});
				logoutRequest.fail(function(xhr, status, error) {
					alert("Logout error: " + error);
				});
			}

		});
	</script>

```
[Example](https://github.com/user454322/browserid-verifier/blob/master/sample/app/src/main/webapp/index.jsp#L42-84)

<br />

1. This implies that [the use of the software is restricted for Good](http://www.json.org/license.html).

<br />

<sub>This project is based in code from https://github.com/mozilla/browserid-cookbook</sub>



