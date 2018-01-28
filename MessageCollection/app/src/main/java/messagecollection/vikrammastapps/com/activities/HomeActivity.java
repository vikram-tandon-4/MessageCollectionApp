package messagecollection.vikrammastapps.com.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import messagecollection.vikrammastapps.com.adapters.HomeAdapter;
import messagecollection.vikrammastapps.com.R;
import messagecollection.vikrammastapps.com.utils.AppPreferences;
import messagecollection.vikrammastapps.com.utils.Utils;

public class HomeActivity extends BaseActivity {

    private Context mContext;
    private Toolbar toolbar;
    private TextView toolBarHeader;
    private AdView mAdView;
    private RelativeLayout noNetworkLayout;
    private AppBarLayout appBar;
    private RecyclerView rvCategories;
    private Button btnTryAgain;
    private ArrayList<String> mCategories;
    private HomeAdapter mAdapter;
    private Dialog mProgressDialog;
    private  AdRequest adRequest;
    private String TAG="HOME ACTIVITY";;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mContext =this;
        initializeViews();

    }
    /*
    *
    *   initializing views
    *
     *  */
    private void initializeViews() {


        noNetworkLayout = (RelativeLayout)findViewById(R.id.noNetworkLayout);
        appBar = (AppBarLayout) findViewById(R.id.appBar);
        rvCategories = (RecyclerView)findViewById(R.id.rvCategories);
        btnTryAgain = (Button) findViewById(R.id.btnTryAgain);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvCategories.setLayoutManager(mLayoutManager);
        rvCategories.setItemAnimator(new DefaultItemAnimator());
        GridLayoutManager layout = new GridLayoutManager(mContext, numberOfColumns());
        rvCategories.setLayoutManager(layout);

/*
*
*   initializing ads
*
 *  */
        mAdView = (AdView) findViewById(R.id.adView);
         adRequest = new AdRequest.Builder().build();

        /*
        *
        * initializing toolbar
        *
        * */
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolBarHeader = (TextView) findViewById(R.id.toolBarHeader);
        toolBarHeader.setText(getResources().getString(R.string.home_toolbar_text));

        toolbar.inflateMenu(R.menu.home);
        toolbar.setOnMenuItemClickListener(new android.support.v7.widget.Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.favorite:
                            startActivity(new Intent(HomeActivity.this, FavoriteActivity.class));
                        return true;
                    case R.id.whatsHot:
                            startActivity(new Intent(HomeActivity.this, WhatsHotActivity.class));
                        return true;
                }
                return false;
            }
        });

        checkNetworkAndSetData();

        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animFadein = AnimationUtils.loadAnimation(mContext, R.anim.fade_in);
                btnTryAgain.startAnimation(animFadein);
                checkNetworkAndSetData();
            }
        });
    }
/***
 *
 * check network and set data
 *
 * ***/
    private void checkNetworkAndSetData()
    {
        if(Utils.isNetworkAvailable(mContext))
        {
            mAdView.loadAd(adRequest);
            noNetworkLayout.setVisibility(View.GONE);
            mProgressDialog = Utils.showProgressDialog(mContext);
            populateData();

            if(AppPreferences.isFirstTime(mContext)) {
                addDataToFavorites();
            }

        }else{
            noNetworkLayout.setVisibility(View.VISIBLE);
            appBar.setVisibility(View.GONE);
            mAdView.setVisibility(View.GONE);
            rvCategories.setVisibility(View.GONE);
        }
    }
/****
 *
 * Add messages to favorite once
 *
 * ****/
    private void addDataToFavorites() {
        // Write a message to the database
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("myFavourites").child("messages");

        // Read from the database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                ArrayList<String> favorite = new ArrayList<>();
                List<HashMap<String, Object>> appCategoryData = (List<HashMap<String, Object>>) dataSnapshot.getValue();
                for (int i = 0; i < appCategoryData.size(); i++) {
                    favorite.add(String.valueOf(appCategoryData.get(i)));
                }
                if(favorite.size()>0) {
                    AppPreferences.setFavoriteList(mContext,favorite);
                    AppPreferences.setFirstTime(mContext,false);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, getString(R.string.failed_to_read_values), error.toException());
            }
        });
    }

/***
 *
 * populate categories
 * **/
    private void populateData() {
        // Write a message to the database
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(getString(R.string.firebase_database_categories));

        // Read from the database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                mCategories = new ArrayList<>();
                List<HashMap<String, Object>> appCategoryData = (List<HashMap<String, Object>>) dataSnapshot.getValue();
                for (int i = 0; i < appCategoryData.size(); i++) {
                    mCategories.add(String.valueOf(appCategoryData.get(i)));
                }
                if(mCategories.size()>0) {
                    mAdapter = new HomeAdapter(mCategories, mContext,mFirebaseAnalytics);
                    rvCategories.setAdapter(mAdapter);
                    appBar.setVisibility(View.VISIBLE);
                    mAdView.setVisibility(View.VISIBLE);
                    rvCategories.setVisibility(View.VISIBLE);
                }
                Utils.cancelProgressDialog(mProgressDialog);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Utils.cancelProgressDialog(mProgressDialog);
                Log.e(TAG, getString(R.string.failed_to_read_values), error.toException());
                Snackbar.make(appBar, R.string.error_something_went_wrong,Snackbar.LENGTH_LONG).show();
            }
        });
    }
    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // You can change this divider to adjust the size of the poster
        int widthDivider = 400;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2;
        return nColumns;
    }
}