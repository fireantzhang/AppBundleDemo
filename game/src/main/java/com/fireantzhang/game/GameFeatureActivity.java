package com.fireantzhang.game;

import android.os.Bundle;

import com.fireantzhang.aabdemo.BaseSplitActivity;

public class GameFeatureActivity extends BaseSplitActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_fature);
        setTitle(R.string.game_room_title);
    }
}
