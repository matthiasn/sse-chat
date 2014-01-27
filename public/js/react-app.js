/** @jsx React.DOM */

(function () {
    /** single chat message component */
    var ChatMsg = React.createClass({
        render: function() { return (
            <div className={"msg " + (this.props.user === "Juliet" ? "juliet" : this.props.user !== this.props.name ? "others" : "")}>
                {this.props.time}<br/>
                <strong>{this.props.user} says: </strong>
                {this.props.text}
            </div>
            );}
    });

    /** chat messages list component, renders all ChatMsg items (above) */
    var MsgList = React.createClass({
        render: function() {
            var msgNodes = this.props.data.map(function (msg) {
                return <ChatMsg user={msg.user} time={msg.time} text={msg.text} name={this.props.name} />;
            }.bind(this));
            return <div id="chat">{msgNodes}</div>;
        }
    });

    /** name and room selection component */
    var NameRoomBox = React.createClass({
        roomOpts: [1,2,3,4,5].map(function (room) { return <option value={room}>Room {room}</option> }),
        render: function() { return (
            <div id="header">
            Your Name: <input type="text" name="user" className="userField" value={this.props.name}
            onChange={this.props.handleNameChange}/>
                <select id="roomSelect" onChange={this.props.handleRoomChange} value={this.props.room}>
                    {this.roomOpts}
                </select>
            </div>
            );}
    });

    /** chat message input component*/
    var SaySomethingBox = React.createClass({
        handleSubmit: function () {
            var msg = { text: this.refs.text.getDOMNode().value, user: this.props.name,
                time: (new Date()).toUTCString(), room: "room" + this.props.room };
            console.log(msg)
            $.ajax({url: "/chat", type: "POST", data: JSON.stringify(msg),
                contentType:"application/json; charset=utf-8", dataType:"json"});
            this.refs.text.getDOMNode().value = ""; // empty text field
            return false;
        },
        render: function () { return (
            <div id="footer">
                <form onSubmit={this.handleSubmit}>
                    <input type="text" id="textField" ref="text" placeholder="Say something" className="input-block-level" />
                    <input type="button" className="btn btn-primary" value="Submit" onClick={this.handleSubmit} />
                </form>
            </div>
            );}
    });

    /** randomly generate initial user name */
    var initialName = function () { return "Jane Doe #" + Math.floor((Math.random()*100)+1) };

    /** ChatApp is the main component in this application, it holds all state, which is passed down to child components
     *  only as immutable props */
    var ChatApp = React.createClass({
        getInitialState: function () {
            return { data: [], room: 1, name: initialName() }; // creates initial application state
        },
        componentWillMount: function () {
            this.listen(this.state.room);                      // called on initial render of the application
        },
        handleNameChange: function (event) {
            this.setState({name: event.target.value});         // update name state with new value in text box
        },
        handleRoomChange: function (event) {
            this.setState({room: event.target.value});         // update room state with the newly selected value
            this.listen(event.target.value);                   // re-initialize SSE stream with new room
        },
        addMsg: function (msg) {
            this.state.data.push(JSON.parse(msg.data));        // push message into state.data array
            this.setState({data: _.last(this.state.data, 4)}); // replace state.data with up to last 5 entries
        },
        listen: function () {
            var chatFeed;            // holds SSE streaming connection for chat messages for current room
            return function(room) {   // returns function that takes room as argument
                if (chatFeed) { chatFeed.close(); }    // if initialized, close before starting new connection
                chatFeed = new EventSource("/chatFeed/room" + room);       // (re-)initializes connection
                chatFeed.addEventListener("message", this.addMsg, false);  // attach addMsg event handler
            }
        }(),
        render: function () { return (
            <div>
                <NameRoomBox name={this.state.name} handleNameChange={this.handleNameChange}
                handleRoomChange={this.handleRoomChange} />
                <MsgList data={this.state.data} name={this.state.name} />
                <SaySomethingBox name={this.state.name} room={this.state.room} />
            </div>
            );}
    });

    /** render top-level ChatApp component */
    React.renderComponent(<ChatApp />, document.getElementById('chat-app'));
})();
