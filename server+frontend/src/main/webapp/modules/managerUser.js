import React from 'react'
import {hashHistory} from "react-router";

let ManageUser=React.createClass({
    displayName: 'ManageUser',


    getInitialState: function() {
        return {
            data: [ ],
            sortby: null,
            descending: false,
            edit: null, // [row index, cell index],
            search: false,
            headers : ["id", "Username", "Email", "Phone","valid"],
            delete:false
        };
    },

    componentWillMount:function(){
        this._getUsers();
    },
    _getUsers: function() {
        this.serverRequest = $.get('queryAllUsers',{
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

    _showEditor: function(e) {
        this.setState({edit: {
                row: parseInt(e.target.dataset.row, 10),
                cell: e.target.cellIndex,
            }});
    },

    _save: function(e) {
        e.preventDefault();
        var input = e.target.firstChild;
        var data = this.state.data.slice();
        data[this.state.edit.row][this.state.edit.cell] = input.value;
        this.setState({
            edit: null,
            data: data,
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

    //delete

    showDelete: function(){
        if (!this.state.delete) {
            var pop = document.getElementsByClassName("deleteU");
            for (var i=0;i<pop.length;i++){
                pop[i].style.color = "#a1263b";
            }
        }
        else{
            var pop = document.getElementsByClassName("deleteU");
            for (var i=0;i<pop.length;i++){pop[i].style.color = "#2589bf";}
        }
        this.setState({delete:!this.state.delete});
    },

    deleteUser: function(e){

        var id=e.target.id;
        var key=document.getElementById(id).innerHTML;
        let info={id:key,
            credentials: 'include'};
        this.serverRequest7=$.post('deleteUser',info,function(data){
            this._getUsers();
        }.bind(this));
    },

    validUser:function(e){
        var id=e.target.id.substring(3);
        let info={id:this.state.data[parseInt(id)][0],
            credentials: 'include'};
        this.serverRequest8=$.post('validUser',info,function(data){
            this._getUsers();
        }.bind(this));
    },

    invalidUser:function(e){
        var id=e.target.id.substring(3);
        let info={id:this.state.data[parseInt(id)][0],
            credentials: 'include'};
        this.serverRequest9=$.post('invalidUser',info,function(data){
            this._getUsers();
        }.bind(this));
    },

    _renderToolbar: function() {
        return (
            <div>
                <div className="Mnpltbar" >
                    <button onClick={this._toggleSearch}>Search</button>
                    <button onClick={this.showDelete}>Delete</button>
                </div>
                <div className="Userbar">
                    <button id="logoutbtn" onClick={this.logout}>Logout</button>
                </div>
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
                    <p> manage users </p>
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
                    <tbody onDoubleClick={this._showEditor}>
                    {this._renderSearch()}
                    {this.state.data.map(function(row, rowidx) {
                        return (
                            <tr key={rowidx}>{
                                row.map(function(cell, idx) {
                                    var content = cell;
                                    var edit = this.state.edit;
                                    if (idx===0){
                                        if (this.state.delete===true) {
                                            return <td className="deleteU"
                                                       id={"tdu" + rowidx.toString() + idx.toString()}
                                                       onClick={this.deleteUser} key={idx}
                                                       data-row={rowidx}>{content}</td>
                                        }
                                        else{
                                            return <td className="deleteU"
                                                       id={"tdu" + rowidx.toString() + idx.toString()}
                                                       key={idx}
                                                       data-row={rowidx}>{content}</td>
                                        }
                                    }
                                    if (idx===4){
                                        if (content==="1"){
                                            return <td><button id={"tdv"+rowidx.toString()} onClick={this.invalidUser}>invalid</button></td>
                                        }
                                        else if (content==="0"){
                                            return <td><button id={"tdv"+rowidx.toString()} onClick={this.validUser}>valid</button></td>
                                        }
                                    }
                                    if (edit && edit.row === rowidx && edit.cell === idx) {
                                        content = (
                                            <form onSubmit={this._save}>
                                                <input type="text" defaultValue={cell} />
                                            </form>
                                        );
                                    }
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

    logout:function() {
        this.serverRequest4=$.get('logout',{
            credentials: 'include'},function(data){
            hashHistory.push("/wrongset");
        }.bind(this));

    },

    back: function () {
        hashHistory.push({
            pathname: '/wrongset',
        })
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
});

export default ManageUser;
