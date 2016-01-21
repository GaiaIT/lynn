define(["lib/d3", "jquery", "lib/async", "lib/window"], function(d3, $, async, window) {

    var symbols;
    var newQueue;

    var width;
    var height;
    var svg;

    var container;
    var readingElement;
    var phraseElement;

    var init = function(element, newSymbols) {
        if (symbols !== null) {
            symbols = newSymbols;
            newQueue = [];
        }

        width = window.innerWidth;
        height = window.innerHeight;

        $(element).append('<div id="symbol" style="position: absolute; z-index: 1"></div>');
        $(element).append('<h3 id="reading"></h3>');
        $(element).append('<h3 id="phrase"></h3>');

        svg = d3.select("#symbol").append("svg")
            .attr("width", width)
            .attr("height", height);
    };

    var foreverSymbols = function() {
        async.forever(symbolCycle);

        // Reload every 30 minutes, to keep things freshhhhh
        var reload = function() {
            location.reload();
        }
        setTimeout(reload, 1800000);
    };

    var symbolCycle = function(next) {
        var symbol;
        switch (symbols.length) {
            case 0:
                console.log("1");
                return setTimeout(next, 1000);
            case 1:
                console.log("2");
                symbol = symbols.at(0);
                break;
            default:
                console.log("2");
                symbol = newQueue.length > 0 ?
                    newQueue.shift() :
                    symbols.at(randomInt(0, symbols.length - 1));
        }
        renderFortune(symbol);
        setTimeout(next, 5000);
    };

    var randomInt = function getRandomInt(min, max) {
        return Math.floor(Math.random() * (max - min + 1)) + min;
    }

    var renderFortune = function(symbol) {
        var readingText = symbol.get("subject") + ' // '
          + symbol.get("hexagram").chinese + ' // '
          + symbol.get("hexagram").english;
        var phraseText = 'test 1-2';

        $("#reading").text(readingText);
        $("#phrase").text(phraseText);

        // At one point, the networks would stop "exploding" upon being visualized
        // a second time. I spent hour after hour trying to understand why, given
        // identical reading data, the first visualization would blast and subsequent
        // visualizations wouldn't. I tried fucking with D3, clearing cache, messing
        // with the randomized reading data, to no avail....
        //
        // After many hours, I decided to revert to an old version of the app where this
        // was not an issue. How could this be! Why!
        //
        // Then, a dawning revelation crept into my head. The "all readings" call to load
        // the existing readings would fail, so I had to parse all readings and store in
        // the readings array. THEN I REALIZED. Storing JSON objects means that every
        // time that D3 tried to visualize a network, it would store references to the
        // nodes + links because they were constant. If I stored reading strings instead, and
        // parsed them just prior to visualization, then D3 would receive a unique
        // JSON object every time, meaning no internally stored references, meaning that it
        // would do a fresh "exploding" visualization every time!
        //
        // The act of consuming the network is as fundamental to Lynn's divinations
        // as the network data themselves!!
        var d3string = JSON.stringify(symbol.get("d3Data"));
        var d3Data = JSON.parse(d3string);

        var nodes = d3Data.nodes;
        var rels = d3Data.rels;

        renderSymbol(nodes, rels);
    };

    var renderSymbol = function(nodes, links) {
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
    };

    return function(element, symbols) {
        init(element, symbols);

        return {
            renderFortune: renderFortune,
            foreverSymbols: foreverSymbols
        }
    };
});
