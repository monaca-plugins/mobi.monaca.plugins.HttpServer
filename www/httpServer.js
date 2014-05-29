var HttpServerPlugin = {};

(function() {
        
    var HttpServer = function(rootDirectory, port){
        this.rootDirectory = rootDirectory || ""; // server base root start from "www" folder
        this.port = port || 3000;
        var baseUrl = monaca.baseUrl.substr(7); // remove the prefix "file://""
        this.rootDirectoryAbsolutePath = baseUrl + this.rootDirectory;
    };        
    
    HttpServer.prototype.checkCordova = function(){
        if(!cordova){
            throw new Error("Cordova is not loaded! Please include cordova.js");            
        }
    }
            
    HttpServer.prototype.start = function(success, failure){              
        var self = this;
        this.checkCordova();
        var errorCallback = function(error) {
            this.runCallback(failure, error);
        }
        
        cordova.exec(
            function(message) {                
                self.runCallback(success, message);
            }, 
            function(error) {
                self.runCallback(failure, error);
            }, 
            "HttpServer",
            "start", 
            [self.rootDirectory, {port: self.port}]
        );
                
    };
    
    HttpServer.prototype.runCallback = function(callback, param){
        if(callback){
            callback(param);
        }  
    };
    
    HttpServer.prototype.stop = function(success, failure){
        var self = this;
        cordova.exec(
        function(message) {
            self.runCallback(success, message);
        }, 
        function(error) {
            self.runCallback(failure, error);
        }, 
        "HttpServer",
        "stop", []);
    };
        
    
    HttpServer.prototype.getAddress = function(success, failure){
        var self = this;
        cordova.exec(
            function(message) {
                self.runCallback(success, message);
            },
            function(error) {
                self.runCallback(failure, error);
            },
            "HttpServer",
           "getAddress", []
        );
    };
    
    HttpServer.prototype.getStatus = function(success, failure){
        var self = this;
        cordova.exec(
            function(serverRootPath) {
                self.runCallback(success, serverRootPath);
              },
            function(error) {
                self.runCallback(failure, error);
            },
            "HttpServer",
           "getStatus", []
        );
    };
    
    
    HttpServerPlugin = HttpServer;    
            
})();
            

module.exports = HttpServerPlugin;
