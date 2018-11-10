import React from 'react';
import connect from '@vkontakte/vkui-connect';
import {View} from '@vkontakte/vkui';
import '@vkontakte/vkui/dist/vkui.css';

import Home from './panels/Home';
import SimilarMemes from './panels/SimilarMemes';

class App extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            activePanel: 'home',
            fetchedUser: null,
            similarId: 0,
        };
    }

    componentDidMount() {
        connect.subscribe((e) => {
            switch (e.detail.type) {
                case 'VKWebAppGetUserInfoResult':
                    this.setState({fetchedUser: e.detail.data});
                    // fetchUser.id
                    break;
                default:
                    console.log(e.detail.type);
            }
        });
        connect.send('VKWebAppGetUserInfo', {});
    }

    go = (e) => {
        this.setState({activePanel: e.currentTarget.dataset.to});
    };

    openSimilar = (e) => {
        this.setState({activePanel: e.currentTarget.dataset.to});
        this.setState({similarId: e.currentTarget.dataset.similar});
    };


    render() {
        return (
            <View activePanel={this.state.activePanel}>
                <Home id="home" fetchedUser={this.state.fetchedUser} go={this.go} openSimilar={this.openSimilar}/>
                <SimilarMemes id="similar-memes" go={this.go} similarId={this.state.similarId}
                              openSimilar={this.openSimilar}/>
            </View>
        );
    }
}

export default App;
