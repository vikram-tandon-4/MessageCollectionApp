package messagecollection.vikrammastapps.com.fragments;


import android.appwidget.AppWidgetManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Locale;

import messagecollection.vikrammastapps.com.R;
import messagecollection.vikrammastapps.com.activities.MessageDetailActivity;
import messagecollection.vikrammastapps.com.utils.AppPreferences;
import messagecollection.vikrammastapps.com.widget.Widget;

public class MessageDetailFragment extends Fragment implements View.OnClickListener{

    private View view;
    private static  String MESSAGE="message";
    private TextView tvMessage;
    private ImageView ivCopy,ivAddToFav,ivListen;
    private FloatingActionButton fbShare;
    private TextToSpeech tts;
    private    ClipboardManager clipboard;
    private static String LISTENING ="listening";
    private static String HEADING ="heading";
    private static String FIREBASE_ANALYTICS ="firebaseAnalytics";

    public static MessageDetailFragment newInstance(String message, boolean isListeningAllowed, String heading) {
        MessageDetailFragment fragment = new MessageDetailFragment();
        Bundle bundleMessage = new Bundle();
        bundleMessage.putString(MESSAGE, message);
        bundleMessage.putString(HEADING, heading);
        bundleMessage.putBoolean(LISTENING, isListeningAllowed);
        fragment.setArguments(bundleMessage);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_message_detail, container, false);
        tvMessage = (TextView) view.findViewById(R.id.tvMessage);
        ivCopy = (ImageView) view.findViewById(R.id.ivCopy);
        ivListen = (ImageView) view.findViewById(R.id.ivListen);
        ivAddToFav = (ImageView) view.findViewById(R.id.ivAddToFav);
        fbShare = (FloatingActionButton) view.findViewById(R.id.fbShare);


        ivCopy.setOnClickListener(this);
        ivListen.setOnClickListener(this);
        ivAddToFav.setOnClickListener(this);
        fbShare.setOnClickListener(this);

        if((boolean)getArguments().get(LISTENING))
        {
            ivListen.setVisibility(View.VISIBLE);
        }else{
            ivListen.setVisibility(View.GONE);
        }

        String heading = ((String)getArguments().get(HEADING));
        if(heading.equals(getString(R.string.my_favorites)))
        {
            ivAddToFav.setVisibility(View.GONE);
        }else{
            ivAddToFav.setVisibility(View.VISIBLE);
        }

        // Gets a handle to the clipboard service.
          clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

        setupMessageData();
        return view;
    }
/****
 *
 * set text message
 * ***/
    private void setupMessageData() {

      String  message= (String) getArguments().get(MESSAGE);
        if (!TextUtils.isEmpty(message)) {
            tvMessage.setText(message);
        }
    }
    /****
     *
     * click events
     * **/
    @Override
    public void onClick(View view) {
        Animation animFadein;
        switch (view.getId()) {
            case R.id.ivCopy:
                animFadein = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
                ivCopy.startAnimation(animFadein);
                // Creates a new text clip to put on the clipboard
                ClipData clip = ClipData.newPlainText("simple text",tvMessage.getText().toString());
                // Set the clipboard's primary clip.
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getActivity(), R.string.message_copied,Toast.LENGTH_LONG).show();
                Bundle bundleCopy = new Bundle();
                bundleCopy.putString(FirebaseAnalytics.Param.ITEM_ID, getString(R.string.copy));
                bundleCopy.putString(FirebaseAnalytics.Param.ITEM_NAME, getString(R.string.analytics_text_copied));
                bundleCopy.putString(FirebaseAnalytics.Param.CONTENT_TYPE,getString(R.string.text));
                ((MessageDetailActivity)getActivity()).getFirebaseAnalyticsInstance().logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundleCopy);


                break;

            case R.id.ivListen:
                animFadein = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
                ivListen.startAnimation(animFadein);
                final Bundle bundleListen = new Bundle();
                bundleListen.putString(FirebaseAnalytics.Param.ITEM_ID, getString(R.string.analytics_tts));
                bundleListen.putString(FirebaseAnalytics.Param.CONTENT_TYPE, getString(R.string.text));
                tts = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status == TextToSpeech.SUCCESS) {
                            tts.setLanguage(Locale.US);
                            tts.speak(tvMessage.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                            bundleListen.putString(FirebaseAnalytics.Param.ITEM_NAME, getString(R.string.tts_worked));
                        }else{
                            bundleListen.putString(FirebaseAnalytics.Param.ITEM_NAME, getString(R.string.analytics_tts_failed));
                            Snackbar.make(tvMessage, R.string.tts_failed,Snackbar.LENGTH_LONG).show();
                        }
                        ((MessageDetailActivity)getActivity()).getFirebaseAnalyticsInstance().logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundleListen);
                    }
                });
                break;

            case R.id.ivAddToFav:
                animFadein = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
                ivAddToFav.startAnimation(animFadein);
                addToFavorite();
                Bundle bundleAddToFAv = new Bundle();
                bundleAddToFAv.putString(FirebaseAnalytics.Param.ITEM_ID, getString(R.string.add_to_fav));
                bundleAddToFAv.putString(FirebaseAnalytics.Param.ITEM_NAME,getString(R.string.analytics_add_to_fav_cliacked));
                bundleAddToFAv.putString(FirebaseAnalytics.Param.CONTENT_TYPE, getString(R.string.text));
                ((MessageDetailActivity)getActivity()).getFirebaseAnalyticsInstance().logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundleAddToFAv);
                break;
            case R.id.fbShare:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, tvMessage.getText().toString());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.share_text)));
                Bundle bundleShare = new Bundle();
                bundleShare.putString(FirebaseAnalytics.Param.ITEM_ID, getString(R.string.share));
                bundleShare.putString(FirebaseAnalytics.Param.ITEM_NAME,getString(R.string.analytics_user_sharing_content));
                bundleShare.putString(FirebaseAnalytics.Param.CONTENT_TYPE, getString(R.string.text));
                ((MessageDetailActivity)getActivity()).getFirebaseAnalyticsInstance().logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundleShare);
                break;
        }
    }

    /****
     *
     * Add to favorite logic
     * **/
    private void addToFavorite() {

        ArrayList<String> favorite;
        String text = tvMessage.getText().toString();
        boolean textAlreadyExists=false;
        if(AppPreferences.getFavoriteList(getActivity())== null) {
            favorite = new ArrayList<>();
            favorite.add(text);
            AppPreferences.setFavoriteList(getActivity(),favorite);
            Snackbar.make(tvMessage, R.string.added_to_fav,Snackbar.LENGTH_LONG).show();
        }else {
            favorite = AppPreferences.getFavoriteList(getActivity());

            for(int i=0;i<favorite.size();i++)
            {
               if(text.equals(favorite.get(i)))
               {
                   textAlreadyExists=true;
                   break;
               }
            }
            if(!textAlreadyExists)
            {
                favorite.add(text);
                AppPreferences.setFavoriteList(getActivity(),favorite);
                Snackbar.make(tvMessage, R.string.added_to_fav,Snackbar.LENGTH_LONG).show();
                updateWidget(getActivity());
            }else{
                Snackbar.make(tvMessage, R.string.already_exist_in_fav_list,Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onPause() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onPause();
    }
/****
 *
 * favorite list changed, update Widget
 *
 * ***/
    private static void updateWidget(Context context) {
        int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, Widget.class));
        Widget myWidget = new Widget();
        myWidget.onUpdate(context, AppWidgetManager.getInstance(context),ids);
    }
}
