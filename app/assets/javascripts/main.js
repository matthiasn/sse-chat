(function () {
    
    var room, chatFeed, now, chatDiv, chatDiv2, textField, userField;
    
    /* somehow scrolling did not work with jQuery selector, I wonder why*/
    chatDiv = $("#chat");
    chatDiv2 = document.getElementById("chat");

    textField = $("#textField");
    userField = $("#userField");

    userField.val("Jane Doe #" + Math.floor((Math.random()*100)+1));
    
    /** post message, clear text field */
    function submit() {
        now = new Date();

        /** posting chat text to server */
        $.ajax({
            type: "POST",
            url: "/chat",
            data: JSON.stringify( {
                text: textField.val(),
                user: userField.val(),
                time: now.toUTCString(),
                room: room
            }),
            contentType: "application/json; charset=utf-8",
            dataType: "json"
        });

        /** empty text field and set focus (using chaining) */
        textField.val("").focus();
    }

    /** submit on enter within text field */
    textField.keypress(function(e) {
        if(e.which == 13) { 
            submit();
        }
    });

    /** submit on button press */
    $("#saySomething").click(function() { 
        submit(); 
    });

    /** handle incoming messages: append to chat div */
    function chatHandler (msg) {
        var data = JSON.parse(msg.data);
        
        var who = "others";
        
        if (data.user === userField.val()) { who = "self"}
        
        chatDiv.append("<div class='" + who + " msg'>" + data.time + "<br/>" 
            +"<strong>" + data.user + " says: " 
            + "</strong>" + data.text + "</div>");

        chatDiv2.scrollTop = chatDiv2.scrollHeight;
    };
    
    /** start listening on messages from selected room */ 
    function listen() {
        room = $("#roomSelector").val();
        chatFeed = new EventSource("/chatFeed/" + room);
        chatFeed.addEventListener("message", chatHandler, false);
    };
    
    /** close previous connection, start new one */
    $("#roomSelector").change(function() {
        chatFeed.close();
        listen();
    });

    listen();
    textField.focus();
    
}).call(this);
