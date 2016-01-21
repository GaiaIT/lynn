requirejs.config({
    baseUrl: '/js',
    paths: {
        jquery: 'lib/jquery',
        backbone: 'lib/backbone'
    },
    shim: {
        'backbone': {
            deps: ['lib/underscore', 'jquery'],
            exports: 'Backbone'
        },
        'underscore': {
            exports: '_'
        }
    }
});

requirejs(["lynn"], function(Lynn) {
    console.log("Hello :D");

    var pattern = new RegExp("/fortune\/(.*)");
    var matcher = location.pathname.match(pattern);
    var symbolUrl = matcher !== null ? '/symbol/persisted/' + matcher[1] : '';

    var symbol = new Lynn.SymbolModel({url: symbolUrl});
    var symbolView = new Lynn.SymbolView({model: symbol});
});
