package messagecollection.vikrammastapps.com.activities;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

import messagecollection.vikrammastapps.com.R;
import messagecollection.vikrammastapps.com.fragments.MessageDetailFragment;
import messagecollection.vikrammastapps.com.utils.Utils;

public class MessageDetailActivity extends BaseActivity {

    private ViewPager viewpager;
    private ArrayList<String> messageList;
    private String MESSAGE_LIST = "messageList";
    private String POSITION = "position";
    private TextView tvCurrentPage;
    private int listSize = 0;
    private static String LISTENING = "listening";
    private boolean isListeningAllowed;
    private ImageView ivPrevious, ivNext;
    private Context mContext;
    private int currentPosition;
    private AdRequest adRequest;
    private AdView mAdView;
    private Toolbar toolbar;
    private TextView toolBarHeader;
    private static String SUBTITLE_HEADING = "subTitleHeading";
    private String heading="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);
        mContext = this;
        initializeViews();
    }

    private void initializeViews() {


                      /*
        *
        * initializing toolbar
        *
        * */
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolBarHeader = (TextView) findViewById(R.id.toolBarHeader);
        heading = (String)getIntent().getStringExtra(SUBTITLE_HEADING);
        if(!TextUtils.isEmpty(heading)) {
            toolBarHeader.setText(heading);
        }else{
            toolBarHeader.setText(R.string.messages);
        }
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID,getString(R.string.subsubcategory_analytics));
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, heading);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, getString(R.string.text));
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        /*
*
*   initializing ads
*
 *  */
        mAdView = (AdView) findViewById(R.id.adView);
        adRequest = new AdRequest.Builder().build();

        viewpager = (ViewPager) findViewById(R.id.viewpager);
        tvCurrentPage = (TextView) findViewById(R.id.tvCurrentPage);
        ivPrevious = (ImageView) findViewById(R.id.ivPrevious);
        ivNext = (ImageView) findViewById(R.id.ivNext);

        ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animFadein = AnimationUtils.loadAnimation(mContext, R.anim.fade_in);
                ivNext.startAnimation(animFadein);
                if ((currentPosition + 1) == listSize) {
                    viewpager.setCurrentItem(0);
                } else {
                    viewpager.setCurrentItem(currentPosition + 1);
                }

            }
        });

        ivPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animFadein = AnimationUtils.loadAnimation(mContext, R.anim.fade_in);
                ivPrevious.startAnimation(animFadein);

                if (currentPosition == 0) {
                    viewpager.setCurrentItem(listSize - 1);
                } else {
                    viewpager.setCurrentItem(currentPosition - 1);
                }

            }
        });

        messageList = getIntent().getStringArrayListExtra(MESSAGE_LIST);
        listSize = messageList.size();
        currentPosition = getIntent().getIntExtra(POSITION, 0);

        isListeningAllowed = getIntent().getBooleanExtra(LISTENING, false);


        if (messageList != null && listSize > 0) {
            setupViewPager(viewpager, messageList, currentPosition);
            tvCurrentPage.setText((currentPosition + 1) + "/" + listSize);
        }

        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tvCurrentPage.setText((position + 1) + "/" + listSize);
                currentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        checkNetworkAndSetAd();
    }
/***
 *
 * show ad if internet is available
 * **/
    private void checkNetworkAndSetAd() {
        if(Utils.isNetworkAvailable(mContext)) {
            mAdView.loadAd(adRequest);
        }else{
            mAdView.setVisibility(View.GONE);
        }
    }
/******
 *
 *
 * setting up viewpager with data
 *
 * *****/
    private void setupViewPager(ViewPager pViewPager, ArrayList<String> messages, int position) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        for (int i = 0; i < messages.size(); i++) {
            adapter.addFragment(MessageDetailFragment.newInstance(messages.get(i), isListeningAllowed, heading));
        }

        pViewPager.setAdapter(adapter);
        pViewPager.setCurrentItem(position);

        viewpager = pViewPager;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final ArrayList<Fragment> mFragmentList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

    }
    /*****
     *
     * Sending firebase instance
     *
     * *****/

    public  FirebaseAnalytics getFirebaseAnalyticsInstance()
    {
        return mFirebaseAnalytics;
    }
}
