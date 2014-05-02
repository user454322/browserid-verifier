# BrowserID Verifier [![Build Status](https://travis-ci.org/user454322/browserid-verifier.png?branch=master)](https://travis-ci.org/user454322/browserid-verifier)



BrowserID Verifier is a simple verifier for the [BrowserID protocol](https://github.com/mozilla/id-specs/blob/prod/browserid/index.md).
However, it has been tested only with [Mozilla Persona](https://login.persona.org/about).

The only external dependencies are SLF4J and minimal-json. 
Since minimal-json is only used to parse the response, it might become unnecessary in the future.


The usage is something like this:
```java
BrowserIDResponse loginRepsonse = verifier.verify(assertion, AUDIENCE);
```

Find a simple, yet complete [live sample here](http://browserid-verifier.user454322.cloudbees.net) with its [source code](https://github.com/user454322/browserid-verifier/tree/master/sample).

## Usage
### 1. Add it as a dependency
 Its Maven coordinate is `info.modprobe:browserid-verifier:<version>` in a pom file it would look like:
```xml
   <dependency>
      <groupId>info.modprobe</groupId>
      <artifactId>browserid-verifier</artifactId>
      <version>0.1-SNAPSHOT</version>
    </dependency>
```

It is hosted in oss.sonatype.org repository, so it is necessary to add the repository:
```xml
   <repository>
     <id>browserid-snapshots</id>
     <name>browserid-snapshots</name>
     <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
   </repository>
```
[Example](https://github.com/user454322/browserid-verifier/blob/master/sample/pom.xml)

<sub>Eventually it is going to be in [Maven Central Repository](http://search.maven.org).</sub>


　　 


### 2. Use
In the server side:
```java
BrowserIDResponse loginRepsonse = verifier.verify(assertion, audience);
Status status = loginRepsonse.getStatus();
if (status == Status.OK) {
  HttpSession session = request.getSession(true);
  session.setAttribute("email", email);
  //...
}  else {
  log.info("Sign in failed...");
  //...
}
```
[Example](https://github.com/user454322/browserid-verifier/blob/master/sample/src/main/java/info/modprobe/browserid/sample/servlet/In.java)


　　 

In the client side:
```javascript
<button type="button" onclick="navigator.id.request();" >Sign in - Sign up</button>
<button type="button" onclick="navigator.id.logout();" >Sign out</button>
....
	<script src="https://login.persona.org/include.js"></script>

	<script type="text/javascript">
		var currentUser = '${sessionScope.email}';
		if(!currentUser){
			/* If falsy set it to the literal null */
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
[Example](https://github.com/user454322/browserid-verifier/blob/master/sample/src/main/webapp/index.jsp)

This project is based in code from https://github.com/mozilla/browserid-cookbook



