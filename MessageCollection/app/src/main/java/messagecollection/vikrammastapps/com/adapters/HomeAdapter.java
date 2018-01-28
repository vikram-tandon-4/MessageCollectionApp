package messagecollection.vikrammastapps.com.adapters;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

import messagecollection.vikrammastapps.com.R;
import messagecollection.vikrammastapps.com.activities.MessageListActivity;


public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {

    private ArrayList<String> categoryList;
    private Context mContext;
    private String ID="id";
    private FirebaseAnalytics mFirebaseAnalytics;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvCategory;
        CardView container;

        public MyViewHolder(View view) {
            super(view);
            tvCategory = (TextView) view.findViewById(R.id.tvCategory);
            container = (CardView) view.findViewById(R.id.categoryContainer);
        }

        public void clearAnimation() {
            container.clearAnimation();
        }
    }


    public HomeAdapter(ArrayList<String> categoryList, Context mContext, FirebaseAnalytics mFirebaseAnalytics) {
        this.categoryList = categoryList;
        this.mContext = mContext;
        // Obtain the FirebaseAnalytics instance.
        this.mFirebaseAnalytics=mFirebaseAnalytics;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_item_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.tvCategory.setText(categoryList.get(position));

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MessageListActivity.class);
                intent.putExtra(ID, position);
                    mContext.startActivity(intent);
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID,mContext.getString(R.string.analytics_category));
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, categoryList.get(position));
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, mContext.getString(R.string.text));
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            }
        });

        setAnimation(holder.container, position);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
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
