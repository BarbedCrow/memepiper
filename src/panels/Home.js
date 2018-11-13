import React from 'react';
import PropTypes from 'prop-types';
import './postImg.css';
import {Panel, Group, PanelHeader, CellButton, Spinner} from '@vkontakte/vkui';
import InfiniteScroll from "react-infinite-scroll-component";
// import InfiniteScroll from 'react-infinite-scroller';

const Home = props => (
    <Panel id={props.id}>
        <PanelHeader>Meme piper</PanelHeader>

        <InfiniteScroll
            dataLength={props.memes.length}
            next={props.getMemes}
            hasMore={true}
            loader={<div style={{height: 100}}>
                <Spinner/>
            </div>
            }
        >

            {/*<div style="height:700px;overflow:auto;">*/}
            {/*<InfiniteScroll*/}
            {/*pageStart={0}*/}
            {/*loadMore={props.getMemes}*/}
            {/*hasMore={true}*/}
            {/*loader={<h4>Loading...</h4>}*/}
            {/*>*/}
            {props.memes.map((meme) => (
                <Group>
                    <p>{meme.text}</p>
                    <CellButton className="postImg" onClick={props.openSimilar} data-to="similar-memes"
                                data-post={meme.postId}>
                        <img className="postImg" src={meme.urlPic}/>
                    </CellButton>
                </Group>
            ))}
        </InfiniteScroll>
        {/*</div>*/}

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
