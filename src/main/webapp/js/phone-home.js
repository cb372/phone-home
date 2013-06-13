;(function() {
    "use strict";

    if (typeof window.PHONEHOME !== 'object' || typeof window.PhoneHome !== 'object') {
        // Namespace
        var PHONEHOME = {};

        PHONEHOME.PhoneHome = (function() {
            // private
            var _options = {
                app: "(unspecified)",
                authString: "not so secret",
                customFields: {},
                swallowErrors: true,
                errorSamplingRate: 1.0,
                timingSamplingRate: 1.0,
                messageSamplingRate: 1.0
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
                    customFields: _options.customFields
                };
            };

            var _sendXHR = function(url, payload) {
                var xhr;
                if (window.XMLHttpRequest)
                    xhr = new XMLHttpRequest();
                else
                    xhr = new ActiveXObject("Microsoft.XMLHTTP");
                xhr.open("POST", url, true);
                xhr.setRequestHeader("X-PhoneHome-Auth", _options.authString);
                xhr.setRequestHeader("Content-Type", "application/json");
                xhr.send(JSON.stringify(payload));
            };

            var _sample = function(f, samplingRate) {
                if (Math.random() < samplingRate) {
                    f();
                }
            };

            // A best-effort attempt to extract error name and message
            // from a string of the form "errorName: error message",
            // as passed to the window.onerror handler.
            var _parseErrorMessage = function(errorMessage) {
                var separator = ": ";
                var i = errorMessage.indexOf(separator);
                if (i > -1 && i < 30) {
                    return {
                        name: errorMessage.slice(0, i),
                        message: errorMessage.slice(i + separator.length)
                    };
                } else {
                    return undefined;
                }
            };

            var _sendError = function(e) {
                try {
                    _sample(function() {
                        var payload = _buildCommonPayload();
                        payload.error = {
                            name: e.name,
                            message: e.message,
                            file: e.fileName, // FF only
                            line: e.lineNumber // FF only
                        };
                        _sendXHR(_serverUrl + "ph/errors", payload);
                    }, _options.errorSamplingRate);
                } catch(e) {
                    _debugLog(e);
                }
            };

            var _sendMessage = function(message) {
                try {
                    _sample(function() {
                        var payload = _buildCommonPayload();
                        payload.message = message;
                        _sendXHR(_serverUrl + "ph/messages", payload);
                    }, _options.messageSamplingRate);
                } catch(e) {
                    _debugLog(e);
                }
            };

            var _sendTiming = function() {
                try {
                    _sample(function() {
                        if (typeof window.performance !== "undefined") {
                            var t = window.performance.timing;
                            var payload = _buildCommonPayload();
                            payload.timing = {
                                network: t.responseEnd - t.fetchStart,
                                requestResponse: t.responseEnd - t.requestStart,
                                dom: t.domComplete - t.domLoading,
                                pageLoad: t.loadEventEnd - t.responseEnd,
                                total: t.loadEventEnd - t.navigationStart
                            };
                            _sendXHR(_serverUrl + "ph/timings", payload);
                        }
                    }, _options.timingSamplingRate);
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

                addOnErrorHandler: function() {
                    window.onerror = function(errorMsg, errorFile, errorLine) {
                        var parseResult = _parseErrorMessage(errorMsg);
                        var err;
                        if (parseResult !== undefined) {
                            err = {
                                name: parseResult.name,
                                message: parseResult.message,
                                fileName: errorFile,
                                lineNumber: errorLine
                            };
                        } else {
                            err = {
                                name: "onerror",
                                message: errorMsg,
                                fileName: errorFile,
                                lineNumber: errorLine
                            };
                        }
                        _sendError(err)
                    };
                },

                sendMessage: function(message) {
                    _sendMessage(message);
                },

                sendTiming: function() {
                    _sendTiming();
                }
            }
        })();

        // Add namespace to global
        window.PHONEHOME = PHONEHOME;

        // Also add PhoneHome object to global namespace. A bit naughty, but convenient.
        window.PhoneHome = PHONEHOME.PhoneHome;

    }
})();
