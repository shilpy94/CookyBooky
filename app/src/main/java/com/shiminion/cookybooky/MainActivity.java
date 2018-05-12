package com.shiminion.cookybooky;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shiminion.cookybooky.firebasemodelclass.RecipeModelClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener {

    private YouTubePlayerView youTubeView;
    private static final int RECOVERY_DIALOG_REQUEST = 1;
    private List<RecipeModelClass> recipeList=new ArrayList<>();
    private String TAG="MAINACTIVITY";
    private YouTubePlayer youTubePlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        getYoutubeUrlDataFromFirebase();
    }

    private void initViews() {
        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
    }

    private void getYoutubeUrlDataFromFirebase() {
// Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("recipes");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Map<String, Object> recipeMainMap = new HashMap<>();
                    recipeMainMap = (Map<String, Object>) dataSnapshot.getValue();
                    if (recipeMainMap != null) {
                        for (String recipeKey : recipeMainMap.keySet()) {
                            RecipeModelClass recipeModelClass=new RecipeModelClass();
                            if ((recipeMainMap.get(recipeKey) != null)) {
                                recipeModelClass.setName(recipeKey);
                                final Map<String, Object> recipeSubMap = (Map<String, Object>) recipeMainMap.get(recipeKey);
                                if (recipeSubMap.get("youtubeUrl") != null) {
                                    recipeModelClass.setUrl((String) recipeSubMap.get("youtubeUrl"));
                                }
                            }
                            recipeList.add(recipeModelClass);
                        }

                        gotFirebaseDataOfRecipe();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
               Log.e(TAG,"Accessing Firebase Data Failed");
            }
        });

    }

    private void gotFirebaseDataOfRecipe(){
        youTubeView.initialize(Config.DEVELOPER_KEY, MainActivity.this);
        if(youTubePlayer!=null){
            youTubePlayer.loadVideo(recipeList.get(0).getUrl());
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                        YouTubePlayer youTubePlayer, boolean wasRestored) {
        if (!wasRestored) {

            // loadVideo() will auto play video
            // Use cueVideo() method, if you don't want to play it automatically
//            youTubePlayer.loadVideo(Config.YOUTUBE_VIDEO_CODE);
            this.youTubePlayer=youTubePlayer;
            // Hiding player controls
            youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format(
                    "error playing youtube", errorReason.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }
}
