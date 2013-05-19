
if (typeof PhoneHome !== 'object') {
    var PhoneHome = (function() {
        // private
        var _options = { 
            app: "(unspecified)",
            password: "not so secret",
            swallowErrors: true
        };

        var _serverUrl = "http://example.com/";

        var _debugLog = function(e) {
            try {
                if (console && console.debug) {
                    console.debug(e);
                }
            } catch (err) { }
        };

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
        };

        var _sendXHR = function(url, payload) {
            if (window.XMLHttpRequest)
                xhr = new XMLHttpRequest();
            else
                xhr = new ActiveXObject("Microsoft.XMLHTTP");
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
                _sendXHR(_serverUrl + "errors", payload)
            } catch(e) { 
                _debugLog(e);
            }
        };

        var _sendMessage = function(message) {
            try {
                var payload = _buildCommonPayload();
                payload.message = message;
                _sendXHR(_serverUrl + "messages", payload)
            } catch(e) { 
                _debugLog(e);
            }
        };

        var _sendTiming = function() {
            try {
                if (window.performance) {
                    var t = window.performance.timing;
                    var payload = _buildCommonPayload();
                    payload.timing = {
                        network: t.responseEnd - t.fetchStart,
                        requestResponse: t.responseEnd - t.requestStart,
                        dom: t.domComplete - t.domLoading,
                        pageLoad: t.loadEventEnd - t.responseEnd,
                        total: t.loadEventEnd - t.navigationStart
                    };
                    _sendXHR(_serverUrl + "timings", payload)
                }
            } catch(e) {
                _debugLog(e);
            }
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
            },

            sendTiming: function() {
                _sendTiming();
            }
        }
    })();
}
