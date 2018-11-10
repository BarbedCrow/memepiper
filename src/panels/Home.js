import React from 'react';
import PropTypes from 'prop-types';
import './postImg.css';
import { Panel, ListItem, Button, Group, Div, Avatar, PanelHeader, CellButton } from '@vkontakte/vkui';

const Home = props => (
	<Panel id={props.id}>
		<PanelHeader>Meme piper</PanelHeader>

		<Group>
            <p>Это мем!</p>
            <CellButton className="postImg" onClick={props.go} data-to="similar-memes">
                <img className="postImg" src="https://pp.userapi.com/c830509/v830509583/1d7a60/NRjOKwLLy-8.jpg"/>
            </CellButton>
        </Group>
        <Group>
            <p>Это второй мем!</p>
            <CellButton className="postImg" onClick={props.go} data-to="similar-memes">
                <img className="postImg" src="https://pp.userapi.com/c846021/v846021363/1243cf/MooEo9q0Vdo.jpg"/>
            </CellButton>
        </Group>
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
