package messagecollection.vikrammastapps.com.activities;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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

import messagecollection.vikrammastapps.com.R;
import messagecollection.vikrammastapps.com.fragments.MessageListFragment;
import messagecollection.vikrammastapps.com.models.SubSubCategoryModel;
import messagecollection.vikrammastapps.com.models.SubcategoryModel;
import messagecollection.vikrammastapps.com.utils.Utils;

public class MessageListActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ArrayList<String> messages;
    private int categoryPostion;
    private String ID = "id";
    private Dialog mProgressDialog;
    private AdRequest adRequest;
    private TextView toolBarHeader;
    private AdView mAdView;
    private RelativeLayout noNetworkLayout;
    private AppBarLayout appBar;
    private Button btnTryAgain;
    private Context mContext;
    private SubcategoryModel subCategoryModel;
    private String TAG="MESSAGE LIST ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        mContext = this;
        initializeViews();

    }

    /*****
     *
     * initialize views
     * *****/


    private void initializeViews() {

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        categoryPostion = getIntent().getIntExtra(ID, 0);
        noNetworkLayout = (RelativeLayout) findViewById(R.id.noNetworkLayout);
        appBar = (AppBarLayout) findViewById(R.id.appBar);
        btnTryAgain = (Button) findViewById(R.id.btnTryAgain);

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
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        checkNetworkAndSetData();
    }

    private void checkNetworkAndSetData() {

        if (Utils.isNetworkAvailable(mContext)) {
            mAdView.loadAd(adRequest);
            noNetworkLayout.setVisibility(View.GONE);
            appBar.setVisibility(View.VISIBLE);
            mAdView.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.VISIBLE);
            mProgressDialog = Utils.showProgressDialog(mContext);
            populateData();

        } else {
            noNetworkLayout.setVisibility(View.VISIBLE);
            appBar.setVisibility(View.GONE);
            mAdView.setVisibility(View.GONE);
            viewPager.setVisibility(View.GONE);
        }
    }
/******
 *
 * Retrieve nested data from following 3 methods:
 *
 *
 * populate data --> populate subtitle --> populate messages
 *
 *
 * ********/
    private void populateData() {
        // Write a message to the database
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference(getString(R.string.firebase_database_subcategories)).child(String.valueOf(categoryPostion));

        // Read from the database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                HashMap<String, Object> appMessageListData = (HashMap<String, Object>) dataSnapshot.getValue();
                subCategoryModel = new SubcategoryModel();

                subCategoryModel.setTitle((String) appMessageListData.get(getString(R.string.firebase_database_title)));
                subCategoryModel.setListeningAllowed((boolean) appMessageListData.get(getString(R.string.firebase_database_isListening_allowed)));
                toolBarHeader.setText((String) appMessageListData.get(getString(R.string.firebase_database_title)));
                populateSubTitles(databaseReference);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Utils.cancelProgressDialog(mProgressDialog);
                Log.e(TAG, getString(R.string.failed_to_read_values), error.toException());
            }
        });
    }

    private void populateSubTitles(DatabaseReference databaseReference) {
        final DatabaseReference databaseReferenceSub = databaseReference.child(getString(R.string.firebase_database_textarray));

        // Read from the database
        databaseReferenceSub.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                List<HashMap<String, Object>> appSubSubcategoryListData = (List<HashMap<String, Object>>) dataSnapshot.getValue();

                for (int i = 0; i < appSubSubcategoryListData.size(); i++) {
                    String subtitle = (String) appSubSubcategoryListData.get(i).get(getString(R.string.firebase_database_subtitle));

                    if (i == (appSubSubcategoryListData.size() - 1)) {
                        populateMessages(i, subtitle, databaseReferenceSub, true);
                    } else {
                        populateMessages(i, subtitle, databaseReferenceSub, false);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Utils.cancelProgressDialog(mProgressDialog);
                Log.e(TAG,getString(R.string.failed_to_read_values), error.toException());
            }
        });

    }

    private void populateMessages(int position, final String subtitle, DatabaseReference databaseReferenceSub, final boolean islastElement) {
        DatabaseReference databaseReferenceSubSub = databaseReferenceSub.child(String.valueOf(position)).child(getString(R.string.firebase_database_subarray));
        // Read from the database
        databaseReferenceSubSub.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
//                String value = dataSnapshot.getValue(String.class);
                List<HashMap<String, Object>> messageList = (List<HashMap<String, Object>>) dataSnapshot.getValue();
                ArrayList<String> messages = new ArrayList<String>();
                for (int i = 0; i < messageList.size(); i++) {
                    messages.add(String.valueOf(messageList.get(i)));
                }

                SubSubCategoryModel subSubCategoryModel = new SubSubCategoryModel();
                subSubCategoryModel.setSubtitle(subtitle);
                subSubCategoryModel.setSubArray(messages);

                ArrayList<SubSubCategoryModel> subcategoryModelNew;
                if (subCategoryModel.getTextArray() != null) {
                    subcategoryModelNew = subCategoryModel.getTextArray();
                } else {
                    subcategoryModelNew = new ArrayList<SubSubCategoryModel>();
                }
                subcategoryModelNew.add(subSubCategoryModel);
                subCategoryModel.setTextArray(subcategoryModelNew);


                if (islastElement) {
                    Utils.cancelProgressDialog(mProgressDialog);
                    setupViewPager(viewPager,subCategoryModel);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Utils.cancelProgressDialog(mProgressDialog);
                Log.e(TAG, getString(R.string.failed_to_read_values), error.toException());
            }
        });

    }

    private void setupViewPager(ViewPager viewPager, SubcategoryModel subCategoryModel) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        ArrayList<SubSubCategoryModel> subSubCategoryModellist = subCategoryModel.getTextArray();
        for(int i=0;i<subSubCategoryModellist.size();i++)
        {
            adapter.addFragment(MessageListFragment.newInstance(subSubCategoryModellist.get(i).getSubArray(),subCategoryModel.isListeningAllowed(),subSubCategoryModellist.get(i).getSubtitle()), subSubCategoryModellist.get(i).getSubtitle());
        }

        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final ArrayList<Fragment> mFragmentList = new ArrayList<>();
        private final ArrayList<String> mFragmentTitleList = new ArrayList<>();

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

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
