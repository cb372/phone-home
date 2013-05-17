
// namespace
var PhoneHome = {};

PhoneHome.PH = (function() {
    // private
    var options = { };

    var sendError = function(e) {
        try {
            var payload = {
                app: "my amazing app",
                url: document.URL,
                errorName: e.name,
                errorMessage: e.message,
                errorFile: e.fileName, // FF only
                errorLine: e.lineNumber, // FF only
                userAgent: navigator.userAgent
            };
            alert(JSON.stringify(payload));
            xhr = new XMLHttpRequest();
            xhr.open("POST", "http://localhost:8080/errors", true);
            xhr.setRequestHeader("X-PhoneHome-Auth", "sesame");
            xhr.setRequestHeader("Content-Type", "application/json");
            xhr.send(JSON.stringify(payload));
        } catch(e) { }
    }

    return {
        // public

        init: function(opts) {
        
        },

        wrap: function(f) {
            try {
                f();
            } catch (e) {
                sendError(e);
            }      
        }
    }
})();

