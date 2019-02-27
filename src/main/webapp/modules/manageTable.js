import React, { Component }  from 'react'
import {hashHistory} from "react-router";

var ManageSet = React.createClass({
    displayName: 'ManageSet',


    headers:["no","name","tags","pop or not","date"],

    getInitialState: function() {
        return {
            ready:false,
            data: null,
            sortby: null,
            descending: false,
            search: false,
            detail: false,
            detailData: null,
            showRef:false,
            myData:null,
            pop:null,
            delete:false
        };
    },


    componentWillMount:function(){
       this.getLibrary();

    },

    getLibrary:function(){
        this.serverRequest21=$.get('queryManagerLibraries',{
            credentials: 'include'},function(data){
            var temp=JSON.parse(data);
            this.serverRequest22=$.get('queryCommonLibraries',{
                credentials: 'include'},function(data){
                var temp2=JSON.parse(data);

                let len=temp.length;
                let lent=temp2.length;
                let p=new Array(len);
                for (let i=0;i<len;i++){
                    for (let j=0;j<lent;j++){
                        if (temp[i][1]===temp2[j][1]) {p[i]=1;break;}
                        else p[i]=0;
                    }
                }
                this.setState({
                    myData:temp2,
                    pop:p,
                    ready:true,
                    data:temp
                });
            }.bind(this));
        }.bind(this));
    },

    toPop: function(e){
        let id=parseInt(e.target.id.substring(1));
        let lid=parseInt(this.state.myData[this.state.myData.length-1][0])+1;
        let qid=this.state.data[id][9];
        let tag=this.state.data[id][2];
        let idx=tag.indexOf(" ");
        let tag1=tag.substring(0,idx);
        let tag2=tag.substring(idx+1);
        let info={libraryId:lid,questionId:qid,tagOne:tag1,tagTwo:tag2,
            credentials: 'include'};
        this.serverRequest23=$.post('addPop',info,function(data){
            let popt=this.state.pop;
            popt[id]=1;
            this.serverRequest22=$.get('queryUserLibraries',{
                credentials: 'include'},function(data){
                var temp=JSON.parse(data);
                this.setState({
                    myData:temp,
                    pop:popt
                })
            }.bind(this));
        }.bind(this));
    },

    toUnPop:function(e){
        let id=e.target.id.substring(1);
        let name=this.state.data[id][1];
        let temp=this.state.myData;
        let li=0;
        for (let i=0;i<temp.length;i++){
            if (temp[i][1]===name){
                li=temp[i][0];
                break;
            }
        }
        if (li!==0){
            let info={libraryId:li,
                credentials: 'include'};
        this.serverRequest24=$.post('deletePop',info,function(data){
            let popt=this.state.pop;
            popt[id]=0;
            this.serverRequest22=$.get('queryUserLibraries',{
                credentials: 'include'},function(data){
                var temp=JSON.parse(data);
                this.setState({
                    myData:temp,
                    pop:popt
                })
            }.bind(this));
        }.bind(this));
        }
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

    //delete

    showDelete: function(){
        if (!this.state.delete) {
            var pop = document.getElementsByClassName("deleteT");
            for (var i=0;i<pop.length;i++){
                pop[i].style.color = "#a1263b";
            }
        }
        else{
            var pop = document.getElementsByClassName("deleteT");
            for (var i=0;i<pop.length;i++){pop[i].style.color = "#2589bf";}
        }
        this.setState({delete:!this.state.delete});
    },

    deleteTable: function(e){

        let id=parseInt(e.target.id.substring(1));
        let lid=this.state.data[id][0];
        let uid=this.state.data[id][5];
        let info={libraryId:lid,userId:uid,
            credentials: 'include'};
        this.serverRequest7=$.post('deleteLibrariesByUser',info,function(data){
            this.getLibrary();
        }.bind(this));
    },

    render: function() {
        return (
            <div>
                <button id='recordbackbtn' onClick={this.back}>back</button>
                {this._renderLogin()}
                {this._renderTable()}
            </div>
        );
    },

    _renderLogin: function(){

        return(
            <div className="Userbar">
                <button id="logoutbtn" onClick={this.logout}>Logout</button>
            </div>
        )

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

    _renderPopLogout: function(){
        if (this.state.logoutPattern===1){
            return(
                <div id="poplogout">
                    <p>Are you sure to log out?</p>
                    <button onClick={this.saveLogout}>Yes</button>
                    <button onClick={this.handleCloseLogout}>No</button>
                </div>
            )
        }
        else{
            return (
                <div id="poplogout">
                    <p>success</p>
                    <button className="close" onClick={this.handleCloseLogout}>close</button>
                </div>
            )
        }
    },

    showContent: function(){
        var c=document.getElementById("detailContent");
        c.innerHTML=this.state.data[this.state.detailData][7];
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
                        <button onClick={this.showDelete}>Delete</button>
                    </div>
                    <div id="managerTitle">
                        <p> manage user libraries </p>
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
                                        if (idx===0){
                                            if (this.state.delete===true) {
                                                return <td className="deleteT"
                                                           id={"z" + rowidx.toString()}
                                                           onClick={this.deleteTable} key={idx}
                                                           data-row={rowidx}>{content}</td>
                                            }
                                            else{
                                                return <td className="deleteT"
                                                           id={"z" + rowidx.toString()}
                                                           key={idx}
                                                           data-row={rowidx}>{content}</td>
                                            }
                                        }
                                        if (idx===3){
                                            if (this.state.pop[rowidx]===1){
                                                return <td className="popular" key={idx} data-row={rowidx} id={"h"+rowidx.toString()} onClick={this.toUnPop}>&hearts;</td>;
                                            }
                                            else{
                                                return <td className="nonPopular" key={idx} data-row={rowidx} id={"h"+rowidx.toString()} onClick={this.toPop}>&hearts;</td>;
                                            }
                                        }
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

export default ManageSet;