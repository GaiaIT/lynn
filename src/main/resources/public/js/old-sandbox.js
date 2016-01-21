var readings = [];
var newQueue = [];

// This is for our lovely cell visualization!
var width = $(window).innerWidth();
var height = $(window).innerHeight();

var svg = d3.select("#symbol").append("svg")
    .attr("width", width)
    .attr("height", height);

// Update function for the visualization!
var dosomed3shit = function(nodes, links, positionsMap) {
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

var randomInt = function getRandomInt(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

var renderReading = function(next) {
    var reading;
    switch (readings.length) {
        case 0:
            return setTimeout(next, 1000);
        case 1:
            console.log("1 reading!");
            reading = readings[0];
            break;
        default:
            console.log(">1 reading!");
            reading = newQueue.length > 0 ?
                readings[newQueue.shift()] :
                readings[randomInt(0, readings.length - 1)] ;
    }
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
    reading = JSON.parse(reading);
    console.log("Our reading!");
    console.log(reading);
    if (!reading) {
        setTimeout(next, 1000);
    }

    var positionsMap = reading.d3Json.positionMap;
    var nodes = reading.d3Json.d3data.nodes;
    var links = reading.d3Json.d3data.rels;

    dosomed3shit(nodes, links, positionsMap);
    setTimeout(next, 15000);
};

var postSuccess = function(data) {
    console.log(data);
    var positionsMap = data.d3Json.positionMap;
    var nodes = data.d3Json.d3data.nodes;
    var links = data.d3Json.d3data.rels;

    dosomed3shit(nodes, links, positionsMap);
}

$("#submit-reading").on('click', function(event) {
    var reading = {
        subject: "sandbox",
        hexagramNo: 1,
        diviner: $("#username").text()
    }
    var config = {
        name: $('select[id="configs"]').val(),
        graphSize: parseInt($('#graphSize').val()),
        edgeThreshold: parseInt($('#edgeThreshold').val()),
        nodeThreshold: parseInt($('#nodeThreshold').val()),
        breakProbability: parseInt($('#breakProbability').val()),
        breakThreshold: parseInt($('#breakThreshold').val()),
        pieces: parseInt($('#pieces').val())
    }
    var symbol = {
        reading: reading,
        expiration: 1,
        symbolConfig: config
    }
    console.log(symbol);
    $('#subject').val("");
    $('#hexagram').val("");

    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $.ajax({
        type: "POST",
        url: "/symbol/transient",
        data: JSON.stringify(symbol),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        beforeSend: function (request) {
            request.setRequestHeader(header, token);
        },
        success: postSuccess
    });
});

async.forever(renderReading);

//////////////////////////
// Config saving stuff!//
////////////////////////
var configs = {
    "Default": {
        graphSize: 200,
        edgeThreshold: 7,
        nodeThreshold: 7,
        breakProbability: 10,
        breakThreshold: 1,
        pieces: 1
    }
};

$.getJSON("/allConfigs", function(data) {
    console.log(data);
    if (data === null || data.length === 0) {
        console.log("No configs to display!");
        return;
    }
    data.forEach(function(config) {
        configs[config.name] = config;
        $("#configs").append("<option>" + config.name + "</option>");
    });
});

$('select[id="configs"]').change(function(){
    var config = configs[$(this).val()];
    $('#graphSize').val(config.graphSize);
    $('#edgeThreshold').val(config.edgeThreshold);
    $('#nodeThreshold').val(config.nodeThreshold);
    $('#breakProbability').val(config.breakProbability);
    $('#breakThreshold').val(config.breakThreshold);
    $('#pieces').val(config.pieces);
});

var configSuccess = function(config) {
    console.log(config);
    if (config == null || config.name == null) {
        $('#status').text("There was an error saving your style :(");
        return;
    } else {
        $('#status').text("Your style has been saved!");
        $('#name').val("");
    }
    configs[config.name] = config;
    $("#configs").append("<option>" + config.name + "</option>").val(config.name);
}

$("#submit-config").on('click', function(event) {
    var config = {
        name: $('#name').val(),
        graphSize: parseInt($('#graphSize').val()),
        edgeThreshold: parseInt($('#edgeThreshold').val()),
        nodeThreshold: parseInt($('#nodeThreshold').val()),
        breakProbability: parseInt($('#breakProbability').val()),
        breakThreshold: parseInt($('#breakThreshold').val()),
        pieces: parseInt($('#pieces').val())
    }
    console.log(config);

    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $.ajax({
        type: "POST",
        url: "/saveConfig",
        data: JSON.stringify(config),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        beforeSend: function (request) {
            request.setRequestHeader(header, token);
        },
        success: configSuccess
    });
});
