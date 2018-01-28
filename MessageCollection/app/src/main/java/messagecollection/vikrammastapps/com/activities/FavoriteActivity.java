package messagecollection.vikrammastapps.com.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

import messagecollection.vikrammastapps.com.R;
import messagecollection.vikrammastapps.com.adapters.FavoriteAdapter;
import messagecollection.vikrammastapps.com.utils.AppPreferences;
import messagecollection.vikrammastapps.com.utils.Utils;

public class FavoriteActivity extends BaseActivity{

    private Context mContext;
    private Toolbar toolbar;
    private TextView toolBarHeader;
    private AdView mAdView;
    private RecyclerView rvFavorites;
    private ArrayList<String> mFavorites;
    private FavoriteAdapter mAdapter;
    private AdRequest adRequest;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        mContext =this;
        initializeViews();
    }

    /*
 *
 *   initializing views
 *
  *  */
    private void initializeViews() {

        rvFavorites = (RecyclerView)findViewById(R.id.rvFavorite);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvFavorites.setLayoutManager(mLayoutManager);
        rvFavorites.setItemAnimator(new DefaultItemAnimator());
        mFavorites = new ArrayList<>();
        mFavorites = AppPreferences.getFavoriteList(mContext);
        if(mFavorites.size()>0) {
            mAdapter = new FavoriteAdapter(mFavorites, mContext,mFirebaseAnalytics);
            rvFavorites.setAdapter(mAdapter);
        }

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
        toolBarHeader.setText(getResources().getString(R.string.my_favorites));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        checkNetworkAndSHowAds();
    }
/**
 *
 * check network and show ads
 *
 * ***/
    private void checkNetworkAndSHowAds()
    {
        if(Utils.isNetworkAvailable(mContext))
        {
            mAdView.loadAd(adRequest);
            mAdView.setVisibility(View.VISIBLE);
        }else{
            mAdView.setVisibility(View.GONE);
        }
    }
}
