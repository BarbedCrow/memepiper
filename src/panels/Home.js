import React from 'react';
import PropTypes from 'prop-types';
import './postImg.css';
import {Panel, ListItem, Button, Group, Div, Avatar, PanelHeader, CellButton} from '@vkontakte/vkui';

const Home = props => (
    <Panel id={props.id}>
        <PanelHeader>Meme piper</PanelHeader>

        {props.memes.map((meme) => (
            <Group>
                <p>{meme.text}</p>
                <CellButton className="postImg" onClick={props.openSimilar} data-to="similar-memes"
                            data-similar={meme.postId}>
                    <img className="postImg" src={meme.urlPic}/>
                </CellButton>
            </Group>
        ))}


        {/*<Group>*/}
        {/*<p>Это мем!</p>*/}
        {/*<CellButton className="postImg" onClick={props.openSimilar} data-to="similar-memes" data-similar="1">*/}
        {/*<img className="postImg" src="https://pp.userapi.com/c830509/v830509583/1d7a60/NRjOKwLLy-8.jpg"/>*/}
        {/*</CellButton>*/}
        {/*</Group>*/}
        {/*<Group>*/}
        {/*<p>Это второй мем!</p>*/}
        {/*<CellButton className="postImg" onClick={props.openSimilar} data-to="similar-memes" data-similar="2">*/}
        {/*<img className="postImg" src="https://pp.userapi.com/c846021/v846021363/1243cf/MooEo9q0Vdo.jpg"/>*/}
        {/*</CellButton>*/}
        {/*</Group>*/}
    </Panel>
);

Home.propTypes = {
    id: PropTypes.string.isRequired,
    go: PropTypes.func.isRequired,
    fetchedUser: PropTypes.shape({
        photo_200: PropTypes.string,
        first_name: PropTypes.string,
        last_name: PropTypes.string,
        city: PropTypes.shape({
            title: PropTypes.string,
        }),
    }),
};

export default Home;
