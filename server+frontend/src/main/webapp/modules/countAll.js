import React, { Component }  from 'react'
import { hashHistory } from 'react-router'

let CountAll = React.createClass({
    displayName: "CountAll",
    getInitialState: function() {
        return {data:"counting..."}
    },
    componentWillMount:function(){
        this.serverRequest25=$.get('queryCountAll',{
            credentials: 'include'},function(data){
            this.setState({
                data:data
            });
        }.bind(this));
    },

    back: function () {
        hashHistory.push({
            pathname: '/wrongset',
        })
    },

    render:function(){
        return(
            <div>
                <button id='recordbackbtn' onClick={this.back}>back</button>
                <p id="countAll">the total number of questions: {this.state.data}</p>
            </div>
        )

    }
});

export default CountAll;