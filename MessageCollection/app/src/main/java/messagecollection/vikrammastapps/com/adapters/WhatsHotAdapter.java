package messagecollection.vikrammastapps.com.adapters;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.ArrayList;

import messagecollection.vikrammastapps.com.R;
import messagecollection.vikrammastapps.com.activities.MessageDetailActivity;

public class WhatsHotAdapter extends RecyclerView.Adapter<WhatsHotAdapter.MyViewHolder> {

    private ArrayList<String> messages;
    private Context mContext;
    private static String LISTENING ="listening";
    private static String SUBTITLE_HEADING = "subTitleHeading";
    private String MESSAGE_LIST = "messageList";
    private String POSITION = "position";


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvMessage;
        CardView container;

        public MyViewHolder(View view) {
            super(view);
            tvMessage = (TextView) view.findViewById(R.id.tvMessage);
            container = (CardView) view.findViewById(R.id.categoryContainer);
        }

        public void clearAnimation() {
            container.clearAnimation();
        }
    }


    public WhatsHotAdapter(ArrayList<String> messages, Context mContext) {
        this.messages = messages;
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_list_item_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.tvMessage.setText(messages.get(position));
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MessageDetailActivity.class);
                intent.putStringArrayListExtra(MESSAGE_LIST, messages);
                intent.putExtra(POSITION, position);
                intent.putExtra(LISTENING,true);
                intent.putExtra(SUBTITLE_HEADING,mContext.getString(R.string.whats_hot));
                mContext.startActivity(intent);
            }
        });

        setAnimation(holder.container, position);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
    }
    @Override
    public void onViewDetachedFromWindow(MyViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        (holder).clearAnimation();
    }
}
