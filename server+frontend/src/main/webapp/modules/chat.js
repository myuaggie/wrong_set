import React, { Component }  from 'react'

/* Get the @User (target) for a message */
function getTarget(str) {
    var arr = str.split(" ");
    var target = "";
    for (var i=0; i<arr.length; i++) {
        if (arr[i].charAt(0) === '@') {
            target = arr[i].substring(1,arr[i].length);
            target = target.replace(/(\r\n|\n|\r)/gm,"");
        }
    }
    return target;
}
/* Remove the @User (target) from a message */
function cleanTarget(str) {
    var arr = str.split(" ");
    var cleanstr = "";
    for (var i=0; i<arr.length; i++) {
        if (arr[i].charAt(0) !== '@')
            cleanstr += arr[i] + " ";
    }
    return cleanstr.substring(0,cleanstr.length-1);
}

function existInJSON(jsonData, str){
    let i=0;
    let idx=str.indexOf(' ');
    str=str.substring(0,idx);
    for(let item in jsonData){
        if (jsonData[i][1]===str) return true;
        i++;
    }
    return false;
}

var Chat = React.createClass({
    displayName: 'Chat',
    wsocket: null,
    textarea: null,

    getInitialState:function(){
        return {
            user:null,
            userlist:[],
            friends:[],
        };
    },

    componentWillMount: function () {
        this.setState({
            user:this.props.user,
            friends:this.props.friends,
        })
    },

    componentDidMount:function(){
        this.wsocket = new WebSocket("ws://localhost:8081/websocketbot");
        this.wsocket.onmessage=this.onMessage;
        this.wsocket.onopen=this.onOpen;
        this.textarea = document.getElementById("textarea");
    },

    componentWillUnmount:function(){
        this.wsocket.close();
    },

    onOpen:function(){
        console.log("open");
    },

    onMessage:function(evt) {
        var line = "";
        /* Parse the message into a JavaScript object */
        var msg = JSON.parse(evt.data);
        if (msg.type === "chat") {
            line = msg.name + ": ";
            if (msg.target.length > 0)
                line += "@" + msg.target + " ";
            line += msg.message + "\n";
            /* Update the chat area */
            this.textarea.value += "" + line;
        } else if (msg.type === "info") {
            line = "[--" + msg.info + "--]\n";
            /* Update the chat area */
            this.textarea.value += "" + line;
        } else if (msg.type === "users") {
            this.setState({userlist:msg.userlist});
        }
        this.textarea.scrollTop = 999999;
    },

    sendJoin:function(){
        var input = document.getElementById("input");
        var name = this.state.user[1]+" ["+this.state.user[0]+"]";
        var join = document.getElementById("join");
        var jsonstr;
        if (name.length > 0) {
            /* Create a message as a JavaScript object */
            var joinMsg = {};
            joinMsg.type = "join";
            joinMsg.name = name;
            /* Convert the message to JSON */
            jsonstr = JSON.stringify(joinMsg);
            /* Send the JSON text */
            this.wsocket.send(jsonstr);
            /* Disable join controls */
            join.disabled = true;
            input.disabled=false;
        }
    },

    sendMessage:function(evt){
        var input = document.getElementById("input");
        var jsonstr;
        var msgstr;
        if (evt.keyCode === 13 && input.value.length > 0) {
            /* Create a chat message as a JavaScript object */
            var chatMsg = {};
            chatMsg.type = "chat";
            chatMsg.name = this.state.user[1]+" ["+this.state.user[0]+"]";
            msgstr = input.value;
            chatMsg.target = getTarget(msgstr.replace(/,/g, ""));
            chatMsg.message = cleanTarget(msgstr);
            chatMsg.message = chatMsg.message.replace(/(\r\n|\n|\r)/gm,"");
            /* Convert the object to JSON */
            jsonstr = JSON.stringify(chatMsg);
            /* Send the message as JSON text */
            this.wsocket.send(jsonstr);
            input.value = "";
        }
    },

    addFriend:function(e){
        var i=e.target.id;
        var row=i.substring(i.indexOf("_")+1);
        let idx=row.indexOf(' ');
        let name=row.substring(0,idx);
        let id=row.substring(idx+2,row.length-1);
        let info={
            id:id,
            username:name,
            credentials: 'include'
        };
        this.serverRequest2=$.post('addFriend',info,function(data){
            this.serverRequest24=$.get('getFriends',{credentials: 'include'},function(data){
                let fs=JSON.parse(data);
                this.setState({
                    friends:fs,
                });
                this.props.update(fs);
                console.log(fs);
            }.bind(this));
        }.bind(this));
    },

    render: function () {
        return (
            <div id="chatroom">
                <h2 id="chatroom-title">Chat Room</h2>
                <input type="submit" id="join" value="Join!" onClick={this.sendJoin}/><br/>
                <textarea id="input" cols="70" rows="1" disabled="true"
                          onKeyUp={this.sendMessage}/><br/>
                <textarea id="textarea" cols="70" rows="20" readOnly/>
                <div id="userlist">
                    {this.state.userlist.map(function(row, rowidx) {
                        return (
                            <div className="userpanel" id={"u"+rowidx.toString()} key={"u"+rowidx.toString()}>
                                <span>{row}</span>
                                {existInJSON(this.state.friends,row)||
                                row.substring(0,row.indexOf(' '))===this.state.user[1]?
                                    <span> </span>:<button id={"af_"+row} onClick={this.addFriend}>Add Friend</button>}
                            </div>
                        );
                    }, this)}
                </div>
            </div>
        )
    }
});

export default Chat;
