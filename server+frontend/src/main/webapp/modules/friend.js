import React, { Component }  from 'react';
import {hashHistory} from "react-router";
import Chat from './chat';

var Friend = React.createClass({
    displayName: 'Friend',

    getInitialState:function(){
        return {
            user:null,
            friends:[],
        };
    },

    componentWillMount: function () {
        this.serverRequest23=$.get('getUserState',{
            credentials: 'include'},function(data){
            var u=JSON.parse(data);
            if (u[0]!=="-1") {
                this.serverRequest24=$.get('getFriends',{credentials: 'include'},function(data){
                    let fs=JSON.parse(data);
                    this.setState({
                        friends:fs,
                        user:u
                    });
                    console.log(fs);
                }.bind(this));
            }
            else {
                this.setState({user: null});
            }
        }.bind(this));
    },

    updateFriend:function(fs){
        this.setState({friends:fs});
    },

    renderFriendsTable:function(){
        return (
            <table>
                <tbody>
                <tr>
                    <th>user id</th>
                    <th>name</th>
                </tr>
                {this.state.friends.map(function(row, rowidx) {
                    return (
                        <tr id={"f"+rowidx.toString()} key={"f"+rowidx.toString()}>{
                            row.map(function(cell, idx) {
                                return <td id={rowidx.toString()+"f"+idx.toString()}
                                           key={"f"+idx.toString()}>{cell}</td>;
                            }, this)}
                        </tr>
                    );
                }, this)}
                </tbody>
            </table>
        )
    },

    render(){
        if (this.state.user===null){
            return (<p> </p>);
        }
        return (
            <div>
                <p id="loginHint">Yours Friends</p>
                {this.renderFriendsTable()}
                <Chat user={this.state.user} friends={this.state.friends} update={this.updateFriend}/>
            </div>
        );
    }

});

export default Friend;