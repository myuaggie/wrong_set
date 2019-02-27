import React from 'react'
import { render } from 'react-dom'
import Wrong from './modules/selfTable'

import { Router, Route, hashHistory } from 'react-router'
import App from './modules/app'
import Detail from './modules/detail'
import WrongLog from './modules/selfTableLogin'
import ManageSet from "./modules/manageTable";
import Manager from "./modules/manage"
import ManageUser from "./modules/managerUser"
import CountAll from "./modules/countAll"
import CountByUser from "./modules/countByUser"
import CountFre from "./modules/countFre"
import CountTags from "./modules/countTags"
render((
    <Router history={hashHistory}>
        <Route path="/" component={App}/>
        <Route path="/wrongset" component={Wrong}/>
        <Route path="/detail/:key" component={Detail}/>
        <Route path="/yourset/:key" component={WrongLog}/>
        <Route path="/manager" component={Manager}/>
        <Route path="/manageCommon" component={ManageSet}/>
        <Route path="/manageUser" component={ManageUser}/>
        <Route path="/countAll" component={CountAll}/>
        <Route path="/countByUser" component={CountByUser}/>
        <Route path="/countFre" component={CountFre}/>
            <Route path="/countTags" component={CountTags}/>
    </Router>
), document.getElementById('app'))
