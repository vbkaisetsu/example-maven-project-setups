# GWT Example

## devel

	mvn clean install -DskipITs
	mvn gwt:run
	mvn gwt:debug

Updating the java source file and hitting "reload" in your browser
is all it takes to reload the java source code changes!

Since GWT 2.7 gwt:debug is only for debugging the server side implementation.
For the (javascript) client, use the development tools built into the chrome browser.
With the source code mappings you can debug the original java sources from
within the chrome javascript debugger.

## prod

To build the final javascript code, use the 'prod' profile:

	mvn clean install -Pprod

## chromedriver

Make sure you have the latest chromedriver installed and it is available on
your path.

## fast compilation

The 'fast' profile is intended to limit javascript compilation to one browser
and one language.

	mvn clean install -Pprod,fast

## find updates

	mvn versions:display-dependency-updates
	mvn versions:display-plugin-updates

## session handling

sandbox.jsp forces the existence of a session, ie. the session (id) itself is
decoupled from the authorization state.
