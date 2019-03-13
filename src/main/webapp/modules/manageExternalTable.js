import BuySet from './buyTable'
import React from 'react'
import {hashHistory} from "react-router";

let ManageExternal=React.createClass({
    displayName: 'ManageExternal',

    getInitialState:function(){
        return {
            data:null,
        };
    },

    componentWillMount: function(){
        this.serverRequest21=$.get('queryCommonLibraries',{
                credentials: 'include'},function(data){
                let temp=JSON.parse(data);
                this.setState({
                    data:temp,
                });
        }.bind(this));
    },

    back:function(){
        hashHistory.push({
            pathname: '/wrongset',
        });
    },

    buy:function(id){
        console.log("get id:",id);

        let info={
            id:id,
            libraryId:parseInt(this.state.data[this.state.data.length - 1][0]) + 1,
            credentials: 'include',
        };
        this.serverRequest51 = $.post('buyLibrary',info, function (data) {
            alert('done');
        }.bind(this));
    },

    render:function () {
        return (
            <div>
                <button id='recordbackbtn' onClick={this.back}>back</button>
                <BuySet buyFunc={this.buy} login={true}/>
            </div>
        )
    }
});

export default ManageExternal;
