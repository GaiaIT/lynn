var svg;
var width = $(window).innerWidth();
var height = $(window).innerHeight();

// Update function for the visualization!
var dosomed3shit = function(nodes, links) {
    // wipe out old visuals...
    d3.selectAll("svg > *").remove();

    var force = d3.layout.force()
        .charge(-120)
        .linkDistance(30)
        .size([width, height]);

    force
        .nodes(nodes)
        .links(links)
        .start();

    var link = svg.selectAll(".link")
        .data(links)
        .enter().append("line")
        .attr("class", "link")
        .style("stroke-width", function(d) { return Math.sqrt(d.value); });

    var node = svg.selectAll(".node")
        .data(nodes)
        .enter().append("circle")
        .attr("class", "node")
        .attr("r", 5)
        .style("fill", "#D819FF")
        .call(force.drag);

    force.on("tick", function() {
        link.attr("x1", function(d) { return d.source.x; })
            .attr("y1", function(d) { return d.source.y; })
            .attr("x2", function(d) { return d.target.x; })
            .attr("y2", function(d) { return d.target.y; });

        node.attr("cx", function(d) { return d.x; })
            .attr("cy", function(d) { return d.y; });
    });
}


var SymbolModel = Backbone.Model.extend({
  url: '/symbol/persisted/' + location.pathname.match(/\/testFortune\/(.*)/)[1],
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
    _.bindAll(this, 'render');
    this.model.on("change", this.render);
    this.model.fetch();
  },

  render: function() {
    console.log(this.model);
    // Render the html of the fortune...
    var symbolContainer = '<div id="symbol" style="position: absolute; z-index: 1"></div>';
    var readingText = '<h3 id="readingtext">'
      + this.model.get("subject") + ' // '
      + this.model.get("hexagram").chinese + ' // '
      + this.model.get("hexagram").english + '</h3>';
    var phraseText = '<h3 id="phrasetext">test 1-2</h3>';

    var html = symbolContainer + readingText + phraseText;
    console.log(html);
    this.$el.append(html);

    // Prepare to render the symbol itself via D3...
    svg = d3.select("#symbol").append("svg")
        .attr("width", width)
        .attr("height", height);

    var nodes = this.model.get("d3Data").nodes;
    var rels = this.model.get("d3Data").rels;
    var positionMap = this.model.get("d3Data").positionMap;
    dosomed3shit(nodes, rels, positionMap);

    return this;
  }

});

var symbol = new SymbolModel();
var symbolView = new SymbolView({model: symbol});
