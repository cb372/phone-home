
// namespace
var PhoneHome = {};

PhoneHome.PH = (function() {
    // private
    var _options = { 
        app: "(unspecified)",
        password: "not so secret"
        swallowErrors: true
    };

    var _serverUrl = "";

    var sendError = function(e) {
        try {
            var payload = {
                app: _options.app,
                url: document.URL,
                userAgent: navigator.userAgent,
                error: {
                    name: e.name,
                    message: e.message,
                    file: e.fileName, // FF only
                    line: e.lineNumber, // FF only
                },
                customFields: _options.customFields // may or may not be defined

            };

            alert(JSON.stringify(payload));
            xhr = new XMLHttpRequest();
            xhr.open("POST", _serverUrl, true);
            xhr.setRequestHeader("X-PhoneHome-Auth", _options.password);
            xhr.setRequestHeader("Content-Type", "application/json");
            xhr.send(JSON.stringify(payload));
        } catch(e) { }
    }

    return {
        // public

        init: function(serverUrl, options) {
            _serverUrl = serverUrl;
            for (var property in options) {
                if (options.hasOwnProperty(property)) {
                    _options[property] = options[property];
                }
            }
        },

        wrap: function(f) {
            try {
                f();
            } catch (e) {
                sendError(e);
                if (!_opts.swallowErrors) {
                    throw e;
                }
            }      
        }
    }
})();

