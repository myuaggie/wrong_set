import React from 'react'
import {hashHistory} from "react-router";

let CountFre= React.createClass({
    displayName: 'CountFre',



    getInitialState: function() {
        return {
            data: [ ],
            sortby: null,
            descending: false,
            search: false,
            headers : ["Question ID", "Total Frequency"],
        };
    },

    componentWillMount:function(){
        this._getCounts();
    },

    back: function () {
        hashHistory.push({
            pathname: '/wrongset',
        })
    },

    _getCounts: function() {
        this.serverRequest = $.get('queryCountFre',{
            credentials: 'include'},function(data){
            this.setState({
                data: JSON.parse(data),
            });
        }.bind(this));
    },

    _sort: function(e) {
        var column = e.target.cellIndex;
        var data = this.state.data.slice();
        var descending = this.state.sortby === column && !this.state.descending;
        data.sort(function(a, b) {
            return descending
                ? (a[column] < b[column] ? 1 : -1)
                : (a[column] > b[column] ? 1 : -1);
        });
        this.setState({
            data: data,
            sortby: column,
            descending: descending,
        });
    },



    _preSearchData: null,

    _toggleSearch: function() {
        if (this.state.search) {
            this.setState({
                data: this._preSearchData,
                search: false,
            });
            this._preSearchData = null;
        } else {
            this._preSearchData = this.state.data;
            this.setState({
                search: true,
            });
        }
    },

    _search: function(e) {
        var needle = e.target.value.toLowerCase();
        if (!needle) {
            this.setState({data: this._preSearchData});
            return;
        }
        var idx = e.target.dataset.idx;
        var searchdata = this._preSearchData.filter(function(row) {
            return row[idx].toString().toLowerCase().indexOf(needle) > -1;
        });
        this.setState({data: searchdata});
    },


    _renderToolbar: function() {
        return (
            <div className="Mnpltbar">
                <button onClick={this._toggleSearch}>Search</button>
            </div>
        );
    },

    _renderSearch: function() {
        if (!this.state.search) {
            return null;
        }
        return (
            <tr onChange={this._search}>
                {this.state.headers.map(function(_ignore, idx) {
                    return <td key={idx}><input type="text" data-idx={idx}/></td>;
                })}
            </tr>
        );
    },

    _renderTable: function() {
        return (
            <div>
                <div id="managerTitle">
                    <p> counting the number of frequency by each question </p>
                </div>
                <table>
                    <thead onClick={this._sort}>
                    <tr>{
                        this.state.headers.map(function(title, idx) {
                            if (this.state.sortby === idx) {
                                title += this.state.descending ? ' \u2191' : ' \u2193';
                            }
                            return <th key={idx}>{title}</th>;
                        }, this)
                    }</tr>
                    </thead>
                    <tbody>
                    {this._renderSearch()}
                    {this.state.data.map(function(row, rowidx) {
                        return (
                            <tr key={rowidx}>{
                                row.map(function(cell, idx) {
                                    var content = cell;
                                    return <td key={idx} data-row={rowidx}>{content}</td>;
                                }, this)}
                            </tr>
                        );
                    }, this)}
                    </tbody>
                </table>
            </div>
        );
    },

    render: function() {

        return (
            <div>
                <button id='recordbackbtn' onClick={this.back}>back</button>
                {this._renderToolbar()}
                {this._renderTable()}
            </div>
        );
    },
})

export default CountFre;