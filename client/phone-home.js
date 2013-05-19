
// namespace
var PhoneHome = {};

PhoneHome.PH = (function() {
    // private
    var _options = { 
        app: "(unspecified)",
        password: "not so secret",
        swallowErrors: true
    };

    var _serverUrl = "http://example.com/";

    var _ensureTrailingSlash = function(url) {
        if (url[url.length - 1] == '/')
            return url;
        else
            return url + '/';
    };

    var _buildCommonPayload = function() {
        return {
            app: _options.app,
            url: document.URL,
            userAgent: navigator.userAgent,
            customFields: _options.customFields // may or may not be defined
        };
    }

    var _sendXHR = function(url, payload) {
        xhr = new XMLHttpRequest();
        xhr.open("POST", url, true);
        xhr.setRequestHeader("X-PhoneHome-Auth", _options.password);
        xhr.setRequestHeader("Content-Type", "application/json");
        xhr.send(JSON.stringify(payload));
    };

    var _sendError = function(e) {
        try {
            var payload = _buildCommonPayload();
            payload.error = {
                name: e.name,
                message: e.message,
                file: e.fileName, // FF only
                line: e.lineNumber, // FF only
            };

            alert(JSON.stringify(payload));

            _sendXHR(_serverUrl + "errors", payload)
        } catch(e) { }
    };

    var _sendMessage = function(message) {
        try {
            var payload = _buildCommonPayload();
            payload.message = message;

            alert(JSON.stringify(payload));

            _sendXHR(_serverUrl + "messages", payload)
        } catch(e) { }
    };

    return {
        // public

        init: function(serverUrl, options) {
            _serverUrl = _ensureTrailingSlash(serverUrl);
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
                _sendError(e);
                if (!_options.swallowErrors) {
                    throw e;
                }
            }      
        },

        sendMessage: function(message) {
            _sendMessage(message);
        }
    }
})();

