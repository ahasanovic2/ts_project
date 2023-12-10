import { BrowserRouter as Router, Route, Switch, Redirect } from 'react-router-dom';
import LoginPage from './components/notLogged/js/LoginPage';
import './App.css';
import './components/notLogged/css/SignUpPage.css';
import VerificationPage from './components/notLogged/js/VerificationPage'
import NotVerifiedPage from './components/notLogged/js/NotVerifiedPage'
import SignUpPage from "./components/notLogged/js/SignUpPage";
import React from "react";
import LandingPage from "./components/user/js/LandingPage";
import './components/user/css/LandingPage.css';
import HomePage from "./components/notLogged/js/HomePage";
import VotersPage from "./components/user/js/VotersPage";
import './components/user/css/VotersPage.css';
import Legislativa from "./components/user/js/Legislativa";
import './components/user/css/Legislativa.css';
import './components/admin/css/AdminLandingPage.css';
import VotingPageFinal from './components/user/js/VotingPage';
import ElectionPage from './components/user/js/ElectionPage';
import ChoosePSPage from './components/user/js/ChoosePollingStation';
import ResultsHome from './components/user/js/ResultsHome';
import AdminLandingPage from './components/admin/js/AdminLandingPage';
import CreatingLists from './components/admin/js/CreatingLists';
import CreatingElections from './components/admin/js/CreatingElections';
import CreatingCandidates from './components/admin/js/CreatingCandidates';
import CreatingPollingStations from './components/admin/js/CreatingPollingStations';
import AssociatePollingStations from './components/admin/js/AssociatePollingStations';
import SuperAdminHome from './components/superadmin/js/Home'
import SACreatingElections from './components/superadmin/js/CreateElections';
import SACreateLists from './components/superadmin/js/CreateLists';
import SACreateCandidates from './components/superadmin/js/CreateCandidates';
import SACreatePollingStations from './components/superadmin/js/CreatePollingStations';
import SAAssociatePollingStations from './components/superadmin/js/AssociatePollingStations';
import SAAdmins from './components/superadmin/js/Admins';
import SACreateAdmin from './components/superadmin/js/CreateAdmin';
import SAUsers from './components/superadmin/js/Users';
import SAResultsHome from './components/superadmin/js/ResultsHome';
import Users from './components/admin/js/Users';
import SAChangePassword from './components/superadmin/js/ChangePassword';
import AChangePassword from './components/admin/js/ChangePassword';
import ChangePassword from './components/user/js/ChangePassword';
import {useHistory} from "react-router-dom";

function App() {
    const history = useHistory();
    const handleLogout = () => {
        // Don't forget to clear the access tokens when logging out
        localStorage.removeItem('access_token');
        localStorage.removeItem('refresh_token');
        history.push("/home");
    };

    // Higher-order component for protected routes
    const PrivateRoute = ({ component: Component, ...rest }) => (
        <Route
            {...rest}
            render={props =>
                localStorage.getItem('access_token') ? (
                    <Component {...props} />
                ) : (
                    <Redirect to={{ pathname: "/login", state: { from: props.location, message: 'You need to log in to access this page' } }} />
                )
            }
        />
    );    
    return (
        <Router>
            <Switch>
                <Route path="/home" component={HomePage}/>
                <Route path="/sign-up" component={SignUpPage}/>
                <Route path="/verification" component={VerificationPage}/>
                <Route path="/not-verified" component={NotVerifiedPage}/>
                <Route path="/login" render={props => <LoginPage {...props} />} />
                <PrivateRoute path="/admin-create-pollingstations" component={CreatingPollingStations} onLogout={handleLogout}/>
                <PrivateRoute path="/admin-add-pollingstations" component={AssociatePollingStations} onLogout={handleLogout}/>
                <PrivateRoute path="/admin-create-candidates" component={CreatingCandidates} onLogout={handleLogout}/>
                <PrivateRoute path="/admin-create-lists" component={CreatingLists} onLogout={handleLogout}/>
                <PrivateRoute path="/admin-landing" component={AdminLandingPage} onLogout={handleLogout}/>
                <PrivateRoute path="/admin-create-elections" component={CreatingElections} onLogout={handleLogout} />
                <PrivateRoute path="/results" component={ResultsHome} onLogout={handleLogout} />
                <PrivateRoute path="/choose-pollingstation" component={ChoosePSPage} onLogout={handleLogout}/>
                <PrivateRoute path="/election" component={ElectionPage} onLogout={handleLogout} />
                <PrivateRoute path="/voting-page" component={VotingPageFinal} onLogout={handleLogout} />
                <PrivateRoute path="/landing" component={LandingPage} onLogout={handleLogout} />
                <PrivateRoute path="/admin-users" component={Users} onLogout={handleLogout} />
                <PrivateRoute path="/voters" component={VotersPage} />
                <PrivateRoute path="/legislativa" component={Legislativa} onLogout={handleLogout} />
                <PrivateRoute path="/admin-change-password" component={AChangePassword} onLogout={handleLogout} />
                <PrivateRoute path="/change-password" component={ChangePassword} onLogout={handleLogout} />

                <PrivateRoute path="/superadmin-home" component={SuperAdminHome} onLogout={handleLogout} />
                <PrivateRoute path="/superadmin-create-elections" component={SACreatingElections} onLogout={handleLogout} />
                <PrivateRoute path="/superadmin-create-lists" component={SACreateLists} onLogout={handleLogout} />
                <PrivateRoute path="/superadmin-create-candidates" component={SACreateCandidates} onLogout={handleLogout} />
                <PrivateRoute path="/superadmin-create-pollingstations" component={SACreatePollingStations} onLogout={handleLogout} />
                <PrivateRoute path="/superadmin-add-pollingstations" component={SAAssociatePollingStations} onLogout={handleLogout} />
                <PrivateRoute path="/superadmin-users" component={SAUsers} onLogout={handleLogout} />
                <PrivateRoute path="/superadmin-results" component={SAResultsHome} onLogout={handleLogout} />
                <PrivateRoute path="/superadmin-change-password" component={SAChangePassword} onLogout={handleLogout} />
                <PrivateRoute path="/admins" component={SAAdmins} onLogout={handleLogout} />
                <PrivateRoute path="/create-admin" component={SACreateAdmin} onLogout={handleLogout} />

                <Route path="/">
                    {/* Default route */}
                    <HomePage />
                </Route>
            </Switch>
        </Router>
    );
}

export default App;
