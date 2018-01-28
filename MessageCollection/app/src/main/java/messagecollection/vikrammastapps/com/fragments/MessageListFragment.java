package messagecollection.vikrammastapps.com.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import messagecollection.vikrammastapps.com.R;
import messagecollection.vikrammastapps.com.adapters.MessageListAdapter;

public class MessageListFragment extends Fragment{

    private TextView tvNoData;
    private View view;
    private RecyclerView rvMessageList;
    private static  String MESSAGES="messages";
    private static String LISTENING ="listening";
    private static String SUBTITLE_HEADING = "subTitleHeading";

public static MessageListFragment newInstance(ArrayList<String> messages, boolean isListeningAllowed, String subtitle) {
    MessageListFragment fragment = new MessageListFragment();
    Bundle bundleMessageList = new Bundle();
    bundleMessageList.putStringArrayList(MESSAGES, messages);
    bundleMessageList.putBoolean(LISTENING, isListeningAllowed);
    bundleMessageList.putString(SUBTITLE_HEADING, subtitle);
    fragment.setArguments(bundleMessageList);
    return fragment;
}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_message_list, container, false);
        tvNoData = (TextView) view.findViewById(R.id.tvNoData);
        rvMessageList = (RecyclerView) view.findViewById(R.id.rvMessageList);
        tvNoData.setText(R.string.no_data_available);
          setupMessageList();
        return view;
    }
/****
 *
 * populate list
 *
 * ****/
    private void setupMessageList() {
        ArrayList<String> messagesList = new ArrayList<>();
        messagesList= (ArrayList<String>) getArguments().get(MESSAGES);
        if (messagesList != null && messagesList.size()>0) {
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            rvMessageList.setLayoutManager(mLayoutManager);
            rvMessageList.setItemAnimator(new DefaultItemAnimator());
            MessageListAdapter mAdapter = new MessageListAdapter(messagesList,getActivity(),(boolean)getArguments().get(LISTENING),(String)getArguments().get(SUBTITLE_HEADING));
            rvMessageList.setAdapter(mAdapter);
        } else {
            tvNoData.setVisibility(View.VISIBLE);
        }
    }
}
