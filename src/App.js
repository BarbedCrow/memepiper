import React from 'react';
import connect from '@vkontakte/vkui-connect';
import {View} from '@vkontakte/vkui';
import '@vkontakte/vkui/dist/vkui.css';
import axios from 'axios';

import Home from './panels/Home';
import SimilarMemes from './panels/SimilarMemes';

class App extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            activePanel: 'home',
            fetchedUser: null,
            postId: 0,
            memes: [],
            similarMemes: []
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
        this.getMemes();
    }

    getMemes = (e) => {
        axios.get("https://95.213.28.127:8443/get_memes/1").then(response => {
            this.setState({memes: this.state.memes.concat(response.data)});
            // console.log(this.state.memes);
            // var a = this.state.memes;
            // for (var i = 0; i < response.data.length; i++) {
            //     a.push(response.data[i]);
            // }
            // console.log(a);
            // this.setState({memes: a});
        })
    };

    getSimilarMemes = (e) => {
        axios.get("https://95.213.28.127:8443/get_memes_similar/1&" + e.postId).then(response => {
            this.setState({similarMemes: this.state.similarMemes.concat(response.data)});
            // console.log(this.state.similarMemes);
        })

    };

    go = (e) => {
        this.setState({activePanel: e.currentTarget.dataset.to});
        this.setState({similarMemes: []});
    };

    openSimilar = (e) => {
        this.setState({activePanel: e.currentTarget.dataset.to});
        this.setState({postId: e.currentTarget.dataset.post});
        this.getSimilarMemes({"postId": e.currentTarget.dataset.post});
    };


    render() {
        return (
            <View activePanel={this.state.activePanel}>
                <Home id="home" fetchedUser={this.state.fetchedUser} go={this.go} openSimilar={this.openSimilar}
                      memes={this.state.memes} getMemes={this.getMemes}/>
                <SimilarMemes id="similar-memes" go={this.go} postId={this.state.postId}
                              openSimilar={this.openSimilar} similarMemes={this.state.similarMemes}
                              getSimilarMemes={this.getSimilarMemes}/>
            </View>
        );
    }
}

export default App;
