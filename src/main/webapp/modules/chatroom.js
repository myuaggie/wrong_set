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

var ChatRoom = React.createClass({
    displayName: 'ChatRoom',
    wsocket:null,
    textarea:null,
    wsconsole:null,
    userlist:null,
    userName:"",

    componentWillMount:function() {

    },

    componentDidMount:function(){
        this.wsocket = new WebSocket("ws://localhost:8081/websocketbot");
        this.wsocket.onmessage=this.onMessage;
        this.wsocket.onopen=this.onOpen;
        this.textarea = document.getElementById("textarea");
      //  this.wsconsole = document.getElementById("wsconsole");
        this.userlist = document.getElementById("userlist");
        document.getElementById("name").focus();
     //   document.getElementById("consolediv").style.visibility = 'hidden';
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
            line = "Users:\n";
            for (var i=0; i < msg.userlist.length; i++)
                line += "-" + msg.userlist[i] + "\n";
            /* Update the user list area */
            this.userlist.value = line;
        }
        this.textarea.scrollTop = 999999;
        /* Update the Websocket console area */
   //     this.wsconsole.value += "-> " +  evt.data + "\n";
   //     this.wsconsole.scrollTop = 999999;
    },

    checkJoin:function(evt){
        if (evt.keyCode === 13){
            this.sendJoin();
        }
    },

    sendJoin:function(){
        var input = document.getElementById("input");
        var name = document.getElementById("name");
        var join = document.getElementById("join");
        var jsonstr;
        if (name.value.length > 0) {
            /* Create a message as a JavaScript object */
            var joinMsg = {};
            joinMsg.type = "join";
            joinMsg.name = name.value;
            /* Convert the message to JSON */
            jsonstr = JSON.stringify(joinMsg);
            /* Send the JSON text */
            this.wsocket.send(jsonstr);
            /* Disable join controls */
            name.disabled = true;
            join.disabled = true;
            input.disabled = false;
            this.userName = name.value;
            /* Update the Websocket console area */
      //      this.wsconsole.value += "<- " + jsonstr + "\n";
      //      this.wsconsole.scrollTop = 999999;
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
            chatMsg.name = this.userName;
            msgstr = input.value;
            chatMsg.target = getTarget(msgstr.replace(/,/g, ""));
            chatMsg.message = cleanTarget(msgstr);
            chatMsg.message = chatMsg.message.replace(/(\r\n|\n|\r)/gm,"");
            /* Convert the object to JSON */
            jsonstr = JSON.stringify(chatMsg);
            /* Send the message as JSON text */
            this.wsocket.send(jsonstr);
            input.value = "";
            /* Update the Websocket console area */
         //   this.wsconsole.value += "<- " + jsonstr + "\n";
         //   this.wsconsole.scrollTop = 999999;
        }
    },

    showHideConsole: function(){
        var chkbox = document.getElementById("showhideconsole");
        var consolediv = document.getElementById("consolediv");
        if (chkbox.checked)
            consolediv.style.visibility = 'visible';
        else
            consolediv.style.visibility = 'hidden';
    },

    renderAux:function() {
        return (
            <div>
                <input id="showhideconsole" type="checkbox" onClick={this.showHideConsole}/>
                Show WebSocket console<br/>
                <div id="consolediv">
                    <textarea id="wsconsole" cols="80" rows="8" readOnly
                              style={{fontSize:8}}/>
                </div>
            </div>
        )
    },

    render: function () {
        return (
            <div id="chatroom">
                <h2 id="chatroom-title">Chat Room</h2>
                Your name: <input id="name" type="text" size="20" onKeyDown={this.checkJoin}/>
                <input type="submit" id="join" value="Join!" onClick={this.sendJoin}/><br/>
                <textarea id="input" cols="70" rows="1" disabled="true"
                          onKeyUp={this.sendMessage}/><br/>
                <textarea id="textarea" cols="70" rows="20" readOnly/>
                <textarea id="userlist" cols="20" rows="20" readOnly/>
                <br/><br/><br/>
            </div>
        )
    }
});

export default ChatRoom;

