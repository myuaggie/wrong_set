import React, { Component }  from 'react'
import { hashHistory } from 'react-router'

let Manager = React.createClass({
    displayName: "Manager",
    handleCommon:function(){
        hashHistory.push("/manageCommon");
    },
    handleUser:function(){
        hashHistory.push("/manageUser");
    },
    handleSelect:function(){
      let s=document.getElementById("statisticsSelect");
      if (s.value==="countAll"){
          hashHistory.push("/countAll");
      }
      else if (s.value==="countByUser"){
          hashHistory.push("/countByUser");
      }
      else if (s.value==="countFre"){
          hashHistory.push("/countFre");
      }
      else{
          hashHistory.push("/countTags");
      }
    },
    logout:function() {
        this.serverRequest4=$.get('logout',{
            credentials: 'include'},function(data){
            hashHistory.push("/wrongset");
        }.bind(this));

    },
    render: function () {
        return (
            <div>
                <div>
                    <div className="Userbar">
                        <button id="logoutbtn" onClick={this.logout}>Logout</button>
                    </div>
                </div>
                <div>
                    <p id="manageCommon">manage common wrong set</p>
                    <button className="Toolbar" id="manageCommonBtn" onClick={this.handleCommon}>-></button>
                </div>
                <div >
                    <p id="manageUsers">manage users</p>
                    <button className="Toolbar" id="manageUsersBtn" onClick={this.handleUser}>-></button>
                </div>
                <div>
                    <p id="statistics">Statistics</p>
                    <select id="statisticsSelect" >
                        <option value="countAll">total number of questions</option>
                        <option value="countByUser">users' number of questions</option>
                        <option value="countFre">view frequency</option>
                        <option value="countTags">view tags</option>
                    </select>
                    <button className="Toolbar" id="statisticsBtn" onClick={this.handleSelect}>-></button>
                </div>
            </div>
        )
    }
});

export default Manager;