package com.example.jon.eventmeets.event_detail_components;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.jon.eventmeets.R;
import com.example.jon.eventmeets.model.VideoGamingEvent;

import java.util.List;

public class VideoGameEventsActivity extends AppCompatActivity implements VideoGameSearchContract.View {
    private Presenter mPresenter;
    private List<VideoGamingEvent> mGameList;
    private VideoGamesRecyclerAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private Button mGameSearch;
    private EditText mQueryText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_game_events);

        mRecyclerView = (RecyclerView)findViewById(R.id.game_search_results);
        mGameSearch = (Button)findViewById(R.id.search_games);
        mQueryText = (EditText)findViewById(R.id.video_game_search);

        mPresenter = new Presenter(this);

        mGameSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = mQueryText.getText().toString();
                if(query.length() < 1) {
                    mQueryText.setError("Search cannot be blank");
                } else {
                    mPresenter.onSearchRequested(query);
                }
            }
        });
    }

    @Override
    public void displaySearchResults(List<VideoGamingEvent> games) {
        mGameList = games;
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new VideoGamesRecyclerAdapter(mGameList);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
    }
}