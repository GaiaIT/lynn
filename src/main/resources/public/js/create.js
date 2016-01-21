var postSuccess = function(data) {
    console.log(data);
    if (data.errors != null || data.url == null) {
        $("#status").text("Error!!");
        $("#url").text("");
        return;
    }
    $("#status").text("Success!!");
    $("#url").text("");
    $('<a>', {
        text: data.url,
        href: data.url,
        target: "_blank"
    }).appendTo("#url");
    //$("#url").text("<a href='" + data.url +"' target='_blank'>" + data.url + "</a>");
    $("#subject").val("");
    $("#hexagram").val("");
}

$("#submit-reading").on('click', function(event) {
    var reading = {
        subject: $("#subject").val(),
        hexagramNo: parseInt($("#hexagram").val()),
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
        expiration: parseInt($("#expiration").val()),
        symbolConfig: config
    }
    console.log(symbol);
    $('#subject').text("");
    $('#hexagram').text("");

    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $.ajax({
        type: "POST",
        url: "/symbol/persisted",
        data: JSON.stringify(symbol),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        beforeSend: function (request) {
            request.setRequestHeader(header, token);
        },
        success: postSuccess
    });
});

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
