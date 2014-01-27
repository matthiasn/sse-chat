/** @jsx React.DOM */

var SseChatApp = SseChatApp || {};

(function () {
    /** single chat message component */
    var ChatMsg = React.createClass({
        render: function() { return (
            <div className={"msg " + (this.props.user === "Juliet" ? "juliet" : this.props.user !== this.props.name ? "others" : "")}>
                {moment(this.props.time).fromNow()}<br/>
                <strong>{this.props.user} says: </strong>
                {this.props.text}
            </div>
        );}
    });

    /** chat messages list component, renders all ChatMsg items (above) */
    var MsgList = React.createClass({
        render: function() {
            var data = [].concat(this.props.data);
            var msgNodes = data.map(function (msg) {
                if (!msg) return "";
                return <ChatMsg user={msg.user} time={msg.time} text={msg.text} name={this.props.name} />;
            }.bind(this));
            return <div id="chat">{msgNodes}</div>;
        }
    });

    /** name and room selection component */
    var NameRoomBox = React.createClass({
        roomOpts: [1,2,3,4,5].map(function (room) { return <option value={"room" + room}>Room {room}</option> }),
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
            SseChatApp.submitMessage({ text: this.refs.text.getDOMNode().value, time: moment().format(),
                                       user: this.props.name, room: this.props.room });
            this.refs.text.getDOMNode().value = "";
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

    /** undo component*/
    var UndoBox = React.createClass({
        handleUndo: function () { this.props.scalaApp.undo(); },
        handleUndoAll: function () { this.props.scalaApp.undoAll(10); },
        render: function () { return (
            <div className="undo">
                <input type="button" className="btn" value="Undo" onClick={this.handleUndo} />
                <input type="button" className="btn" value="Undo All" onClick={this.handleUndoAll} />
                <span> Stack size:  {this.props.undoSize}</span>
            </div>
         );}
    });

    /** ChatApp is the main component in this application, it holds all state, which is passed down to child components
     *  only as immutable props */
    var ChatApp = React.createClass({
        handleNameChange: function (event) { this.props.scalaApp.setUser(event.target.value) },
        handleRoomChange: function (event) { this.props.scalaApp.setRoom(event.target.value); },
        render: function () { return (
            <div>
                <UndoBox scalaApp={this.props.scalaApp} undoSize={this.props.stackSize}/>
                <NameRoomBox name={this.props.user} handleNameChange={this.handleNameChange}
                room={this.props.room} handleRoomChange={this.handleRoomChange} />
                <MsgList data={this.props.msgs} name={this.props.user}/>
                <SaySomethingBox name={this.props.user} room={this.props.room}/>
            </div>
        );}
    });

    /** render top-level ChatApp component */
    var tlComp = React.renderComponent(<ChatApp scalaApp={ScalaApp}/>, document.getElementById('chat-app'));

    /** pass props to top level component */
    SseChatApp.setProps = function (props) { tlComp.setProps(props); };

    /** application ready, call initial trigger so that name and room get loaded without receiving message */
    ScalaApp.triggerReact();
})();
