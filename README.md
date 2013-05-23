# Phone Home

A Javascript client and Scalatra server for collecting client-side errors and diagnostics.

Phone Home allows you to collect the following information from your users' browsers:

* Exception reports (type of exception, exception message, file and line number if supported by browser)
* Timing information (if it is supported by the browser)
* Arbitrary informational messages

## How to use

### Server

First you need to have a PhoneHome server running somewhere. PhoneHome clients will send their messages to this server.

The following commands will start a server on port 8080.

````
git clone git://github.com/cb372/phone-home.git
cd server
./sbt container:start
````

TODO: document how to use sbt-start-script, how to run on Heroku

### Client

The client is a Javascript library designed to be embedded into the pages of your site.

#### Initialization

Add links to the Javascript files in your `&lt;head&gt;` section:

    <script type="text/javascript" src="/javascripts/json2.js"></script>
    <script type="text/javascript" src="/javascripts/phone-home.js"></script>

This will add a `PhoneHome` object to the global namespace.

Before we can use the `PhoneHome` object, we need to initialize it. Pass the URL of the PhoneHome server, along with any other configuration options.

    <script type="text/javascript">
        PhoneHome.init("http://phone-home-server:8080/", {
                app: "my amazing app",
                password: "not-so-secret",
                customFields: { "foo": "bar", "baz": "hoge" }
            }        
        );
    </script>

Configuration options: TODO

#### Sending error reports

TODO

#### Sending timing info

TODO

#### Sending arbitrary messages

TODO
