package messagecollection.vikrammastapps.com.adapters;


import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import messagecollection.vikrammastapps.com.R;
import messagecollection.vikrammastapps.com.activities.MessageDetailActivity;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MyViewHolder> {

    private ArrayList<String> messageList;
    private Context mContext;
    private String MESSAGE_LIST = "messageList";
    private String POSITION = "position";
    private boolean listeningAllowed;
    private static String LISTENING ="listening";
    private String heading;
    private static String SUBTITLE_HEADING = "subTitleHeading";

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvMessage;
        CardView container;

        public MyViewHolder(View view) {
            super(view);
            tvMessage = (TextView) view.findViewById(R.id.tvMessage);
            container = (CardView) view.findViewById(R.id.categoryContainer);
        }
    }


    public MessageListAdapter(ArrayList<String> messageList, Context mContext, boolean listening, String heading) {
        this.messageList = messageList;
        this.mContext = mContext;
        listeningAllowed = listening;
        this.heading=heading;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_list_item_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.tvMessage.setText(messageList.get(position));
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, MessageDetailActivity.class);
                intent.putStringArrayListExtra(MESSAGE_LIST, messageList);
                intent.putExtra(POSITION, position);
                intent.putExtra(LISTENING,listeningAllowed);
                intent.putExtra(SUBTITLE_HEADING,heading);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

}
