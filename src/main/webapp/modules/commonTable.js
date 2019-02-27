import React, { Component }  from 'react'

var CommonSet = React.createClass({
    displayName: 'CommonSet',


    headers:["no","name","tags","frequency","date"],

    getInitialState: function() {
        return {
            ready:false,
            data: null,
            sortby: null,
            descending: false,
            search: false,
            detail: false,
            detailData: null,
            showRef:false
        };
    },


    componentWillMount:function(){
        this.serverRequest21=$.get('queryCommonLibraries',{
            credentials: 'include'},function(data){
            var temp=JSON.parse(data);

            this.setState({
                data:temp,
                ready:true,
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


    _showDetail: function(e){
        var id=parseInt(e.target.id.substring(1));
        //alert(id);
        this.setState({detail:true,detailData:id});
    },

    _back: function(e){
        this.setState({detail:false,detailData:null,showRef:false});
    },

    _showReference: function(){
        var ref=document.getElementById("referenceCom");
        ref.style.display="block";
        this.setState({showRef:true});
    },

    _hideReference: function(){
        var ref=document.getElementById("referenceCom");
        ref.style.display="none";
        this.setState({showRef:false});
    },

    showContent: function(){
        var c=document.getElementById("detailContent");
        c.innerHTML=this.state.data[this.state.detailData][7];
    },


    render: function() {
        return (
            <div>
                {this._renderTable()}
            </div>
        );
    },

    _renderRefBtn: function(){
        if (this.state.showRef===true){
            return <button onClick={this._hideReference}>hide reference</button>
        }
        else{
            return <button onClick={this._showReference}>show reference</button>
        }
    },

    _renderSearch: function() {
        if (!this.state.search) {
            return null;
        }
        return (
            <tr onChange={this._search}>
                {this.headers.map(function(_ignore, idx) {
                    return <td key={idx}><input type="text" data-idx={idx}/></td>;
                })}
            </tr>
        );
    },

    _renderRef: function(){
        if (this.state.data[this.state.detailData][8]){
            return(<div id="referenceCom">
                <div id="refContent">{this.state.data[this.state.detailData][8]}</div>
            </div>);
        }
        else{
            return(<div id="referenceCom">
                <div id="refContent">No reference yet.</div>
            </div>);
        }
    },

    _renderTable: function() {
        if (this.state.detail===true){
            if (this.state.detailData>=0){
                return(
                    <div className="detailPage">
                        <h id="detailTitle">{this.state.data[this.state.detailData][1]}</h>
                        <p id="detailOwner">contributor: {this.state.data[this.state.detailData][6]}</p>
                        <p id="detailDate">updated: {this.state.data[this.state.detailData][4]}</p>
                        <button onClick={this._back}>back</button>
                        <p id="detailContent">show content<button onClick={this.showContent}>+</button></p>

                        {this._renderRefBtn()}
                        {this._renderRef()}
                    </div>
                )
            }
        }
        if (this.state.ready===true){
        return (
            <div>
                <div className="Mnpltbar" >
                    <button onClick={this._toggleSearch}>Search</button>
                </div>
                <table>
                    <thead onClick={this._sort}>
                    <tr>{
                        this.headers.map(function(title, idx) {
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
                                    if (idx>=5) return null;
                                    if (idx===1) return  <td className="detail" onClick={this._showDetail} id={"c"+rowidx.toString()} key={idx} data-row={rowidx}>{content}</td>;
                                    return <td key={idx} data-row={rowidx}>{content}</td>;
                                }, this)}
                            </tr>
                        );
                    }, this)}
                    </tbody>
                </table>
            </div>
        );
        }
        else{
            return (<p> </p>);
        }
    }
});

export default CommonSet;
//<p id="detailContent">{this.state.data[this.state.detailData][7]}</p>