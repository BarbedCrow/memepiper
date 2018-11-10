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
            memes: [{
                "id": 1,
                "text": "Text 1",
                "url": "https://pp.userapi.com/c830509/v830509583/1d7a60/NRjOKwLLy-8.jpg"
            },
            {
                "id": 2,
                "text": "Text 2",
                "url": "https://pp.userapi.com/c830509/v830509583/1d7a60/NRjOKwLLy-8.jpg"
            }],
            similarMemes: [{
                "id": 4,
                "text": "Similar Pikachu",
                "url": "https://i.uaportal.com/gallery/2018/11/9/13.jpg"
            }]
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
        // TODO: get request for similars here!
        this.setState({activePanel: e.currentTarget.dataset.to});
        this.setState({similarId: e.currentTarget.dataset.similar});
    };


    render() {
        return (
            <View activePanel={this.state.activePanel}>
                <Home id="home" fetchedUser={this.state.fetchedUser} go={this.go} openSimilar={this.openSimilar}
                      memes={this.state.memes}/>
                <SimilarMemes id="similar-memes" go={this.go} similarId={this.state.similarId}
                              openSimilar={this.openSimilar} similarMemes={this.state.similarMemes}/>
            </View>
        );
    }
}

export default App;
