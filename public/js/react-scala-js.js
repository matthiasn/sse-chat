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
    $.ajax(
        {
            url: "/chat",
            type: "POST",
            data: JSON.stringify(msg),
            contentType: "application/json; charset=utf-8",
            dataType: "json"
        }
    );
};

SseChatApp.setAppState = function (appState) { };
SseChatApp.setUserProps = function (user) { };
SseChatApp.setRoomProps = function (room) { };
SseChatApp.setMsgsProps = function (msgs) { };
SseChatApp.setStackSizeProps = function (stackSize) { };

SseChatApp.wireHandlers = function (submitMsg, setUser, setRoom, undo, undoAll) {
  SseChatApp.scalaApp = {
      submitMsg: submitMsg,
      setUser: setUser,
      setRoom: setRoom,
      undo: undo,
      undoAll: undoAll
  }
};
