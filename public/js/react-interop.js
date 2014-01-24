var SseChatApp = SseChatApp || {};

SseChatApp.listen = function () {
    var chatFeed; // holds SSE streaming connection for chat messages for current room
    return function (room, handler) { // returns function that takes room as argument
        if (chatFeed) {
            chatFeed.close();
        } // if initialized, close before starting new connection
        chatFeed = new EventSource("/chatFeed/" + room); // (re-)initializes connection
        chatFeed.addEventListener("message", function (msg) {
            handler(JSON.parse(msg.data));
        }, false); // attach addMsg event handler
    }
}();

/** POST chat message */
SseChatApp.submitMessage = function (msg) {
    $.ajax({ url: "/chat", type: "POST", data: JSON.stringify(msg),
        contentType: "application/json; charset=utf-8", dataType: "json" });
};

/** placeholder until replaced with real implementation upon compiling / initializing JSX */
SseChatApp.setProps = function (props) {};

/**
 * individual setProps because otherwise the closure compiler renamed function names on application state
 * case class object (would be more elegant with a single case class object)
 */
SseChatApp.setUserProps      = function (user)      { SseChatApp.setProps({ user: user }); };
SseChatApp.setRoomProps      = function (room)      { SseChatApp.setProps({ room: room }); };
SseChatApp.setMsgsProps      = function (msgs)      { SseChatApp.setProps({ msgs: msgs }); };
SseChatApp.setStackSizeProps = function (stackSize) { SseChatApp.setProps({ stackSize: stackSize }); };
