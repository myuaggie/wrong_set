import React, { Component }  from 'react'

function getJsonLength(jsonData){
    var jsonLength = 0;
    for(var item in jsonData){
        jsonLength++;
    }
    return jsonLength;
}

var BuySet = React.createClass({
    displayName: 'BuySet',


    headers:["no","name","price","category","number of questions","sales","buy"],

    getInitialState: function() {
        return {
            k:10,
            data:null,
            login:this.props.login,
            page:0,
        };
    },

    componentDidMount:function(){
        this.serverRequest1=$.post('getTopKSaleLibraries',{k:this.state.k,
            credentials: 'include'},function(data){
            let temp=JSON.parse(data);
            this.setState({
                data:temp,
            });

        }.bind(this));
    },

    goLastPage: function(){
        let pagenow=this.state.page-1;
        let len=getJsonLength(this.state.data);
        let temp=[];
        for (let i=pagenow*10;i<len&&i<pagenow*10+10;i++){
            temp.push(this.state.data[i]);
        }
        this.setState({pageData:temp,
            page:pagenow});
    },

    goNextPage: function(){
        let pagenow=this.state.page+1;
        let len=getJsonLength(this.state.data);
        let temp=[];
        for (let i=pagenow*10;i<len&&i<pagenow*10+10;i++){
            temp.push(this.state.data[i]);
        }
        this.setState({pageData:temp,
            page:pagenow});
    },


    search: function(e) {
        let el=document.getElementById('input-k');
        this.serverRequest2=$.post('getTopKSaleLibraries',{k:el.value,
            credentials: 'include'},function(data){
                let temp=JSON.parse(data);
                this.setState({
                    data:temp,
                    k:el.value,
                });
        }.bind(this));
    },

    buy: function(e){
        console.log("buy");
        let idx = e.target.dataset.row;
        console.log(this.state.data[idx][0]);
        this.props.buyFunc(this.state.data[idx][0]);
        let datanow=this.state.data;
        datanow.splice(idx,1);
        this.setState({
            data:datanow
        });
    },

    _renderSearchBox: function () {
        return (
            <p id="recordbackbtn">Search Libraries with top <input type="text" id="input-k"/> sales<button onClick={this.search}>search</button></p>
        )
    },

    _renderTable: function () {
        if (this.state.data!=null) {
                return (
                    <table>
                        <tr>
                            {
                                this.headers.map(function (title, idx) {
                                    return <th key={idx}>{title}</th>;
                                }, this)
                            }
                        </tr>
                        <tbody>
                        {
                            this.state.data.map(function (row, rowidx) {
                                if (rowidx>=this.state.page*10 && rowidx<this.state.page*10+10)
                                return (
                                    <tr key={rowidx}>{
                                        row.map(function (cell, idx) {
                                            return <td key={idx} data-row={rowidx}>{cell}</td>;
                                        }, this)}
                                        {this.state.login?<td key={6} data-row={rowidx} onClick={this.buy}>+</td>:<td>login needed</td>}
                                    </tr>

                                );
                            }, this)

                        }
                        </tbody>
                    </table>
                )
        }
        else {
            return(<p id="loginHint">no external libraries available</p>);
        }
    },

    _renderPageBtn: function() {
        if (this.state.data==null){
            return (
                <p>
                    <button id="recordbackbtn" onClick={this.goLastPage} disabled>last page</button>
                    <button onClick={this.goNextPage} disabled>next page</button>
                </p>
            )
        }
        if (this.state.page>0 && this.state.page<getJsonLength(this.state.data)/10-1) {
            return (
                <p>
                    <button id="recordbackbtn" onClick={this.goLastPage}>last page</button>
                    <button onClick={this.goNextPage}>next page</button>
                </p>
            )

        }
        else if (this.state.page>0){
            return (
                <p>
                    <button id="recordbackbtn" onClick={this.goLastPage}>last page</button>
                    <button onClick={this.goNextPage} disabled>next page</button>
                </p>
            )
        }
        else if (this.state.page<getJsonLength(this.state.data)/10-1){
            return (
                <p>
                    <button id="recordbackbtn" onClick={this.goLastPage} disabled>last page</button>
                    <button onClick={this.goNextPage}>next page</button>
                </p>
            )
        }
        else {
            return (
                <p>
                    <button id="recordbackbtn" onClick={this.goLastPage} disabled>last page</button>
                    <button onClick={this.goNextPage} disabled>next page</button>
                </p>
            )
        }
    },


    render:function () {
        return (
            <div>
                {this._renderSearchBox()}
                {this._renderTable()}
                {this._renderPageBtn()}
            </div>
        )

    }
});

export default BuySet;