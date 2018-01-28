package messagecollection.vikrammastapps.com.activities;


import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import messagecollection.vikrammastapps.com.R;
import messagecollection.vikrammastapps.com.adapters.WhatsHotAdapter;
import messagecollection.vikrammastapps.com.utils.Utils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WhatsHotActivity extends BaseActivity{

    private ArrayList<String> jokeList;
    private RecyclerView rvWhatsHot;
    private WhatsHotAdapter mAdapter;
    private ImageView ivBannerImage;
    private Toolbar toolbar;
    private Context mContext;
    private int count=0, numberOfMessages=2;
    private Dialog mProgressDialog;
    private boolean showList= false;
    private AdRequest adRequest;
    private RelativeLayout noNetworkLayout;
    private AppBarLayout appBar;
    private Button btnTryAgain;
    private AdView mAdView;
    private String TAG="WHAT'S HOT ACTIVITY";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whats_hot);
        mContext=this;
        initializeViews();
    }

    private void initializeViews() {

        noNetworkLayout = (RelativeLayout)findViewById(R.id.noNetworkLayout);
        appBar = (AppBarLayout) findViewById(R.id.appBar);
        btnTryAgain = (Button) findViewById(R.id.btnTryAgain);

        /*
*
*   initializing ads
*
 *  */
        mAdView = (AdView) findViewById(R.id.adView);
        adRequest = new AdRequest.Builder().build();

        jokeList = new ArrayList<>();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onBackPressed();
            }
        });

        ivBannerImage = (ImageView)findViewById(R.id.ivBannerImage);

        CollapsingToolbarLayout collapsing_container = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);
        collapsing_container.setTitle(getString(R.string.whats_hot));

        rvWhatsHot = (RecyclerView) findViewById(R.id.rvWhatsHot);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvWhatsHot.setLayoutManager(mLayoutManager);
        rvWhatsHot.setItemAnimator(new DefaultItemAnimator());

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


    private void checkNetworkAndSetData()
    {
        if(Utils.isNetworkAvailable(mContext))
        {
            mAdView.loadAd(adRequest);
            noNetworkLayout.setVisibility(View.GONE);
            rvWhatsHot.setVisibility(View.VISIBLE);

            mProgressDialog = Utils.showProgressDialog(mContext);
            for(int i=0; i< numberOfMessages; i++) {
                new EasterEggTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

            getToolbarImage();

        }else{
            noNetworkLayout.setVisibility(View.VISIBLE);
            appBar.setVisibility(View.GONE);
            mAdView.setVisibility(View.GONE);
            rvWhatsHot.setVisibility(View.GONE);
        }
    }

    private void getToolbarImage() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference(getString(R.string.firebase_database_whatshot));

        // Read from the database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                HashMap<String, Object> appMessageListData = (HashMap<String, Object>) dataSnapshot.getValue();

                Picasso.with(mContext)
                        .load((String) appMessageListData.get(getString(R.string.firebase_database_image_url))).fit().placeholder(R.mipmap.ic_launcher)
                        .into(ivBannerImage);
                appBar.setVisibility(View.VISIBLE);

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, getString(R.string.whats_hot));
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, getString(R.string.analytics_whats_hot));
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, getString(R.string.image));
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Utils.cancelProgressDialog(mProgressDialog);
                Log.e(TAG, getString(R.string.failed_to_read_values), error.toException());

                Picasso.with(mContext)
                        .load("xyz").fit().placeholder(R.mipmap.ic_launcher)
                        .into(ivBannerImage);
                appBar.setVisibility(View.VISIBLE);
            }
        });
    }

    public class EasterEggTask extends AsyncTask<Void, Void, String> {

        private  final OkHttpClient CLIENT = new OkHttpClient();

        private final WeakReference<AppCompatActivity> weakActivity;

        public EasterEggTask(AppCompatActivity activity) {
            weakActivity = new WeakReference<>(activity);
        }

        @Override protected String doInBackground(Void... params) {
            // Request a Chuck Norris joke. See http://www.icndb.com/api/
            Request request = new Request.Builder().url("http://api.icndb.com/jokes/random").build();
            try {
                Response response = CLIENT.newCall(request).execute();
                if (response.isSuccessful()) {
                    count++;

                    return new JSONObject(response.body().string()).getJSONObject("value").getString("joke");

                }
            } catch (Exception e) {
                Utils.cancelProgressDialog(mProgressDialog);
                count++;
                Log.e(TAG, getString(R.string.async_whats_hot_error), e);
                if(jokeList.size()>0)
                {
                    showList=true;
                }
            }
            return null;
        }

        @Override protected void onPostExecute(String joke) {

            AppCompatActivity activity = weakActivity.get();
            if (activity != null && !activity.isFinishing() && !TextUtils.isEmpty(joke)) {
                jokeList.add(joke);
                if(numberOfMessages == count || showList) {
                    Utils.cancelProgressDialog(mProgressDialog);
                    mAdapter = new WhatsHotAdapter(jokeList, mContext);
                    rvWhatsHot.setAdapter(mAdapter);
                    mAdView.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
