define(["visualization", 'backbone', "jquery", "lib/underscore"], function(Visual, Backbone, $, async) {

    var SymbolModel = Backbone.Model.extend({
        constructor: function(args) {
            this.url = args.url;
            Backbone.Model.apply(this, arguments);
        },
        defaults: {
            errors: null,
            subject: null,
            hexagram: null,
            d3Data: null,
            url: null
        }
    });

    var SymbolView = Backbone.View.extend({
        el: 'body',

        initialize: function() {
            console.log(this.model);
            this.listenTo(this.model, 'sync', this.render);
            this.model.fetch();
        },

        render: function() {
            console.log(this.model);
            new Visual(this.el).renderFortune(this.model);
        }
    });

    var GalleryCollection = Backbone.Collection.extend({
        constructor: function(args) {
            this.url = args.url;
            Backbone.Collection.apply(this, arguments);
        },
        model: SymbolModel
    });

    var GalleryView = Backbone.View.extend({
        el: 'body',

        initialize: function() {
            this.listenTo(this.collection, 'sync', this.render);
            this.collection.fetch();
        },

        render: function() {
            console.log(this.collection);
            new Visual(this.el, this.collection).foreverSymbols();
        }
    });

    return {
        SymbolModel: SymbolModel,
        SymbolView: SymbolView,
        GalleryCollection: GalleryCollection,
        GalleryView: GalleryView
    }
});
