# Phone Home

A Javascript client and Scalatra server for collecting client-side errors and diagnostics.

[LIVE DEMO](http://phone-home-demo.herokuapp.com/sample)

Phone Home allows you to collect the following information from your users' browsers:

* Exception reports (type of exception, exception message, file and line number if supported by browser)
* Timing information (if it is supported by the browser)
* Arbitrary informational messages
 
All messages will also include the browser's user agent, the URL of the current page, and any custom fields you want to add.

## How to use

### Server

First you need to have a PhoneHome server running somewhere. PhoneHome clients will send their messages to this server.

The following commands will start a server on port 8080.

````
git clone git://github.com/cb372/phone-home.git
./sbt container:start
````

TODO: document how to use sbt-start-script, how to run on Heroku

Once the server is running, open http://localhost:8080/sample in your browser.

### Client

The client is a Javascript library designed to be embedded into the pages of your site.

#### Initialization

Add links to the Javascript files in your `<head>` section:

    <script type="text/javascript" src="/javascripts/json2.js"></script>
    <script type="text/javascript" src="/javascripts/phone-home.js"></script>

This will add a `PhoneHome` object to the global namespace.

Before we can use the `PhoneHome` object, we need to initialize it. Pass the URL of the PhoneHome server, along with any other configuration options.

    <script type="text/javascript">
        PhoneHome.init("http://phone-home-server:8080/", {
                app: "my amazing app",
                authString: "sesame",
                customFields: { "foo": "bar", "baz": "hoge" },
                swallowErrors: false
            }        
        );
    </script>

Configuration options:

<table>
  <tr><th>Key</th><th>Default value</th><th>Description</th></tr>
  <tr><td>app</td><td>"(unspecified)"</td><td>A human-readable identifier for your application. Useful when you have multiple apps posting data to the same PhoneHome server.</td></tr>
  <tr><td>authString</td><td>"not so secret"</td><td>A string that is passed as an HTTP header with all POST requests to the PhoneHome server. If you set an authString on the server side, this should be set to the same value.</td></tr>
  <tr><td>customFields</td><td>{} (empty hash)</td><td>A hash of any other useful information that you want to include. Every time a message is posted to the PhoneHome server, these fields will be included.</td></tr>
  <tr><td>swallowErrors</td><td>true</td><td>After an exception is caught and sent to the PhoneHome server, should it be swallowed or not. If this is set to false, the exception will be rethrown.</td></tr>
</table>

#### Sending error reports

Say you have some Javascript that may throw an exception:

    <script type="text/javascript">
        for (var i=0; i<100; i++) {
            doSomethingRisky();
        }
    </script>

Of course, you don't *think* it'll throw any exceptions... but you just never know, especially when this code will have to run on dozens of different versions of IE, FireFox, Chrome, Safari and smartphone browsers.

Simply wrap this code in `PhoneHome.wrap()` and any exceptions will be caught and posted to the PhoneHome server as JSON, along with the URL being accessed, the user agent and various other useful debugging information.

    <script type="text/javascript">
        PhoneHome.wrap(function() {
            for (var i=0; i<100; i++) {
                doSomethingRisky();
            }
        });
    </script>

##### onerror Handler

Even if you try to `wrap()` all of your JS code, some errors may still slip through the net. You can make sure that absolutely all errors are phoned home by adding a `window.onerror` handler:

    <script type="text/javascript">
        PhoneHome.addOnErrorHandler();
    </script>

A couple of notes on error handling semantics:

* `window.onerror` is only called for *uncaught* errors. So any errors that you handle and swallow using `PhoneHome.wrap()` will not reach the `onerror` handler. This means that you don't need to worry about the same error being reported twice.

* Because `window.onerror` is just a listener, you cannot use it to swallow errors. Any errors that get this far will also reach your user's browser error console. If you want to swallow errors, use `PhoneHome.wrap()`.

#### Sending timing info

Call `PhoneHome.sendTiming()` to send information on how long the page took to load.

The following timings are included:

<table>
  <tr><th>Field</th><th>Meaning</th></tr>
  <tr><td>network</td><td>All network time, including DNS, TCP connection and HTTP request and response</td></tr>
  <tr><td>requestResponse</td><td>Time from starting the HTTP request to the end of the HTTP response</td></tr>
  <tr><td>dom</td><td>Time spent loading the DOM</td></tr>
  <tr><td>pageLoad</td><td>All time spent rendering the page, including loading the DOM</td></tr>
  <tr><td>total</td><td>Absolutely everything, from the user clicking a link the page being completely loaded</td></tr>
</table>

Note that some browsers (e.g. Safari) do not yet support the Navigation Timing API. In this case, the `sendTiming()` method will simply do nothing.

Of course, you should call `sendTiming()` after page load has completed. If you're using jQuery, wrap the call in a `document.ready()`:

    $(function() { PhoneHome.sendTiming(); });

If you prefer to do things old-skool, use an `onLoad()` event:

    window.onload = function() {
        setTimeout(function() { 
            PhoneHome.sendTiming(); 
        }, 0);
    }

#### Sending arbitrary messages

`PhoneHome` has a `sendMessage` method that you can use to send arbitrary log messages to the server:

    PhoneHome.sendMessage("Everything looks good!");
