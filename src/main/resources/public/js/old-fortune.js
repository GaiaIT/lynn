console.log("Hello! <3, Lynn");

// Reload every 30 minutes, to keep things freshhhhh
var reload = function() {
    location.reload();
}
setTimeout(reload, 1800000);

var readings = [];
var newQueue = [];
var phrases = [];
phrases.push("Don’t touch it.");
phrases.push("Time 2 Frolic for real");
phrases.push("Cute. You’re growing!");
phrases.push("HOT.");
phrases.push("Pay attention in class.");
phrases.push("Always assume you’re wrong.");
phrases.push("Step up your game lady.");
phrases.push("Collaborate. Be a nice person.");
phrases.push("Don’t worry I have no idea what this means either");
phrases.push("Step in suite succession");
phrases.push("Invasion persuasion");
phrases.push("There’s a big ol thang in the way of your deal");
phrases.push("People agree about your shit. Move along move along");
phrases.push("The greatest leaders are always owning stuff");
phrases.push("Be honest be be be honest");
phrases.push("Circle back on it mama");
phrases.push("(/◕ヮ◕)");
phrases.push("(^。^)y-.。o○ (-。-)y-゜゜゜");
phrases.push("キタ━━━(゜∀゜)━━━!!!!! ");
phrases.push("(ノಠ益ಠ)ノ彡");
phrases.push("m9(^Д^)");
phrases.push("lXXXXXXXXl");
phrases.push("(*´Д`)ﾊｧﾊｧ");
phrases.push("ヾ(- -；)コラコラ");
phrases.push("you are such poetry");
phrases.push("make all the people love you");
phrases.push("she likes it");
phrases.push("he likes it");
phrases.push("open it up");
phrases.push("heart of the eye of the tiger");
phrases.push("erotic stirring coming your way");
phrases.push("slim pickings, wax n wane");
phrases.push("wax and wane");
phrases.push("can’t have rain without a rainbow");
phrases.push("gotta have a rain without rainbows sometimes");
phrases.push("boing boing");
phrases.push("its gonna happens");
phrases.push("whenever you want me, i’ll be there");
phrases.push("step inside");
phrases.push("get out of the bounding box");
phrases.push("5,000 steps more, soon");
phrases.push("love all the people");
phrases.push("fulfillment");
phrases.push("fleeting fulfillment");
phrases.push("did you forget to have breakfast this morning!?");
phrases.push("bacteria is very important to digestion");
phrases.push("woo-hoo!!! an individual blood cells circulates through your body in about 60 seconds");
phrases.push("did you know that drinking water in the morning is good");
phrases.push("you need to have more bananas");
phrases.push("answers will arrive in the form of a fruit");
phrases.push("your body probably contains 10x more bacteria cells than human cells");
phrases.push("yeild errorrr: you might be an algorithm, we need to check this first, try again……8Hbsh: running test node..AXC.kez");
phrases.push("a photon may need 40,000 years to travel from the core of the sun to the surface. From there it only takes 8 minutes to travel to earth. where your eyes detect it");
phrases.push("forget about yourself");
phrases.push("Forget about yourself. The great barrier reef is the largest living structure on EARTH");
phrases.push("The great barrier reef is the largest living structure on EARTH");
phrases.push("there are 8x as many atoms in a teaspoon of water as there are teaspoons of water in the Atlantic ocean");
phrases.push("theres plenty of room down there at the bottom");
phrases.push("channel your energy");
phrases.push("take your time");
phrases.push("The average person walks 5x around the world in one lifetime");
phrases.push("take it easy, you’ve been walking in circles a lot");
phrases.push("take it as it is");
phrases.push("come as you are");
phrases.push("standstill stagnation");
phrases.push("stand as still as you possibly can for a moment");
phrases.push("cooled helium (-460F) can flow against gravity and will actually flow upwards. chill out");
phrases.push("flow");
phrases.push("into the flow");
phrases.push("force fields");
phrases.push("remember what you will dream tonight");

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
            console.log("No readings yet!");
            return setTimeout(next, 1000);
        default:
            reading = readings[0];
            break;
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
    if (!reading) {
        console.log("eh");
        return setTimeout(next, 1000);
    }
    console.log("wow!");
    var positionsMap = reading.d3Json.positionMap;
    var nodes = reading.d3Json.d3data.nodes;
    var links = reading.d3Json.d3data.rels;

    dosomed3shit(nodes, links, positionsMap);
    setTimeout(next, 15000);
};

var id = parseInt($("#symbol").attr("symbol-id"));
$.get("/symbol/persisted/" + id, function(data) {
    console.log(data);
    var symbol = JSON.stringify(data);
    newQueue.push(readings.push(symbol) - 1);

    var subject = data.subject;
    if (!subject) {
        setTimeout(next, 1000);
    }
    var hex = data.hexagram;
    if (!hex) {
        setTimeout(next, 1000);
    }

    $('#readingtext').text(subject + " // " + hex.chinese + " // " + hex.english);
    $('#phrasetext').text(phrases[randomInt(0, phrases.length)]);

    async.forever(renderReading);
});
