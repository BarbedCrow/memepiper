import React from 'react';
import PropTypes from 'prop-types';
import {Panel, PanelHeader, HeaderButton} from '@vkontakte/vkui';
import "./postImg.css"
import Icon28ChevronBack from '@vkontakte/icons/dist/28/chevron_back';

const SimilarMemes = props => (
    <Panel id={props.id}>
        <PanelHeader
            left={<HeaderButton onClick={props.go} data-to="home">
                <Icon28ChevronBack/>
            </HeaderButton>}
        >
            Similar memes
        </PanelHeader>
        <img className="postImg" src="https://i.uaportal.com/gallery/2018/11/9/13.jpg"/>
    </Panel>
);

SimilarMemes.propTypes = {
    id: PropTypes.string.isRequired,
    go: PropTypes.func.isRequired,
};

export default SimilarMemes;
