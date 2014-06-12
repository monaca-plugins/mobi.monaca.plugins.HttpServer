

var exec = require('cordova/exec');

var HttpServer = function(rootDirectory, port)
{
    this.rootDirectory = rootDirectory || "";
    this.port = port || 3000;
};

HttpServer.prototype.startHttpServer = function(onSuccess, onError)
{
    exec(onSuccess, onError, "HttpServer", "start", [this.rootDirectory, {port: this.port}]);
}

HttpServer.prototype.stopHttpServer = function(onSuccess, onError)
{
    exec(onSuccess, onError, "HttpServer", "stop", []);
}

HttpServer.prototype.getAddress = function(onSuccess, onError)
{
    exec(onSuccess, onError, "HttpServer", "getAddress", []);
}

HttpServer.prototype.getStatus = function(onSuccess, onError)
{
    exec(onSuccess, onError, "HttpServer", "getStatus", []);
}

module.exports  = new HttpServer();

