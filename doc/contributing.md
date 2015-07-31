## Introduction
This text overviews how this library is built and tested, so anyone interested can contribute.
Questions and feedback is more than welcome! 

## Requirements
* JDK 7
* Maven 3
* Ruby 2.1.2
 * rspec 3.1.0
 * capybara 2.4.4
 * selenium-webdriver 2.44.0

## Building the library
`cd lib`
It is a Maven project, so do:
*  `mvn verify` to verify
*  `mvn test` to execute the unit tests
*  `mvn package` to create a jar file of the library
*  `mvn install` to install the library into the local Maven repository

and so on.

## Building the test app
There is a simple web app to test the library, to see it in action do:
 1. `cd sample/app`
 1. `mvn jetty:run`
 1. Browse to https://127.0.0.1:8443/

## Executing the integration tests
The integration tests are specified using Rspec with Capybara and Selenium, to run the integration tests do:
 1. `cd sample/integration-test/`
 1. Make sure Ruby 2.1.2 is being used
 1. `gem install rspec -v 3.1.0`
 1. `gem install capybara -v 2.4.4`
 1. `gem install selenium-webdriver -v 2.44.0`
 1.  Adjust  `config.yaml`
 1. `rspec spec/features/user_signs_in_spec.rb`

## Deploy pipeline
Snapshots of the library pass through a [Snap CI pipeline](https://snap-ci.com/user454322/browserid-verifier/branch/master) that ends in automatically deploying the library to the Sonatype Maven's repository.

The build, test and deploy process is trigered with every commit to the master branch, the process fails if any step in the pipeline fails. This ensures that every build has been analyzed, compiled, tested and works.
![Build process](process.png?raw=true "Build Process")
The pipeline consist of:
 1. Commit to master branch.
 1. MVN_VERIFY_LIB `mvn verify`. This is a general check.
 1. MVN_INSTALL_LIB `mvn install`. Here, the code is compiled, analyzed with [FindBugs](http://findbugs.sourceforge.net), unit tests are run, a jar file is created and installed to the local repository.
 1. BUILD_APP The sample app is built.
 1. DEPLOY_APP The app is deployed to the cloud (OpenShift) and accessible at https://browseridverifiersample-user454322.rhcloud.com.
 1. INTEGRATION_TEST Integration test (RSpec Selenium) is run.
 1. APP_PERFORMANCE (TODO). Application tests can be done with JMeter; testing the library is a bit tricky but [caliper](https://github.com/google/caliper) might be useful.
 1. MVN_DEPLOY_LIB The library is deployed to the [Sonatype Maven's repository](https://oss.sonatype.org/content/repositories/snapshots/info/modprobe/browserid-verifier).

## Code style
Java code is formatted with the help of [Eclipse built-in formatter](../Eclipse_built-in-codestyle.xml) .
Ruby code follows  [GitHub Styleguide](https://github.com/styleguide/ruby)



