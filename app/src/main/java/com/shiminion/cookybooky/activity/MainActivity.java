package com.shiminion.cookybooky.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shiminion.cookybooky.Config;
import com.shiminion.cookybooky.R;
import com.shiminion.cookybooky.adapter.LandingPageRowAdapter;
import com.shiminion.cookybooky.firebasemodelclass.RecipeModelClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private List<RecipeModelClass> recipeList=new ArrayList<>();
    private String TAG="MAINACTIVITY";
    private RecyclerView recyclerView;
    private InterstitialAd mInterstitialAd;
    private ProgressBar loading_spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, getApplication().getResources().getString(R.string.app_id));

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getApplication().getResources().getString(R.string.admob_inter_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        initViews();
        showLoader();
        getYoutubeUrlDataFromFirebase();
    }

    private int count=0;
    @Override
    protected void onResume() {
        super.onResume();
        if(count>0) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                Log.d("TAG", "The interstitial wasn't loaded yet.");
            }
        }

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });

        count=1;
    }

    private void initViews() {
        recyclerView= (RecyclerView) findViewById(R.id.recycler_view);
        loading_spinner=(ProgressBar) findViewById(R.id.loading_spinner);
    }

    private void showLoader(){
        loading_spinner.setVisibility(View.VISIBLE);
    }

    private void hideLoader(){
        loading_spinner.setVisibility(View.GONE);
    }

    private void getYoutubeUrlDataFromFirebase() {
        try {
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
                                RecipeModelClass recipeModelClass = new RecipeModelClass();
                                if ((recipeMainMap.get(recipeKey) != null)) {
                                    recipeModelClass.setName(recipeKey);
                                    final Map<String, Object> recipeSubMap = (Map<String, Object>) recipeMainMap.get(recipeKey);
                                    if (recipeSubMap.get("youtubeUrl") != null) {
                                        String youtubeKey = (String) recipeSubMap.get("youtubeUrl");
                                        if (youtubeKey.contains("https://www.youtube.com/watch?v=")) {
                                            youtubeKey = youtubeKey.replace("https://www.youtube.com/watch?v=", "");
                                        }
                                        if (youtubeKey.contains("https://youtu.be/")) {
                                            youtubeKey = youtubeKey.replace("https://youtu.be/", "");
                                        }
                                        if (youtubeKey.contains("&")) {
                                            int position = youtubeKey.indexOf("&");
                                            youtubeKey = youtubeKey.substring(0, position);
                                        }
                                        recipeModelClass.setUrl(youtubeKey);
                                    }
                                    if (recipeSubMap.get("title") != null) {
                                        recipeModelClass.setName((String) recipeSubMap.get("title"));
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
                    hideLoader();
                    MainActivity.this.finish();
                    Log.e(TAG, "Accessing Firebase Data Failed");
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

   private LandingPageRowAdapter mAdapter;
    private void gotFirebaseDataOfRecipe() {
        try {
            mAdapter = new LandingPageRowAdapter(recipeList, MainActivity.this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter);
            hideLoader();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
