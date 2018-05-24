package com.shiminion.cookybooky.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.shiminion.cookybooky.Config;
import com.shiminion.cookybooky.R;

public class YoutubeActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private YouTubePlayerView youTubeView;
    private YouTubePlayer youTubePlayer;
    private final int RECOVERY_DIALOG_REQUEST = 1;
    private String key="";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_youtube);
        initViews();
        Intent intent=getIntent();
        key=intent.getStringExtra("key");
    }

    private void initViews(){
        youTubeView= (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.initialize(Config.DEVELOPER_KEY, YoutubeActivity.this);
    }

    private void playVideo(){
        if(youTubePlayer!=null && key!=null && !key.isEmpty()){
            youTubePlayer.loadVideo(key);
        } else {
            Toast.makeText(this, "Error in playing video", Toast.LENGTH_LONG).show();
            YoutubeActivity.this.finish();
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        if (!wasRestored) {

            // loadVideo() will auto play video
            // Use cueVideo() method, if you don't want to play it automatically
            this.youTubePlayer=youTubePlayer;
            // Hiding player controls
            youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
            playVideo();
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format(
                    "error playing youtube", errorReason.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

}
