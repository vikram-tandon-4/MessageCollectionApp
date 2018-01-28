package messagecollection.vikrammastapps.com.adapters;


import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

import messagecollection.vikrammastapps.com.R;
import messagecollection.vikrammastapps.com.activities.FavoriteActivity;
import messagecollection.vikrammastapps.com.activities.MessageDetailActivity;
import messagecollection.vikrammastapps.com.utils.AppPreferences;
import messagecollection.vikrammastapps.com.widget.Widget;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.MyViewHolder> {

    private ArrayList<String> favoriteMessages;
    private Context mContext;
    private static String LISTENING ="listening";
    private static String SUBTITLE_HEADING = "subTitleHeading";
    private String MESSAGE_LIST = "messageList";
    private String POSITION = "position";
    private FirebaseAnalytics mFirebaseAnalytics;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvCategory;
        CardView container;
        ImageView ivDelete;

        public MyViewHolder(View view) {
            super(view);
            tvCategory = (TextView) view.findViewById(R.id.tvCategory);
            container = (CardView) view.findViewById(R.id.categoryContainer);
            ivDelete = (ImageView) view.findViewById(R.id.ivDelete);
        }

        public void clearAnimation() {
            container.clearAnimation();
        }
    }


    public FavoriteAdapter(ArrayList<String> favoriteMessages, Context mContext, FirebaseAnalytics mFirebaseAnalytics) {
        this.favoriteMessages = favoriteMessages;
        this.mContext = mContext;
        this.mFirebaseAnalytics=mFirebaseAnalytics;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favorite_list_item_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.tvCategory.setText(favoriteMessages.get(position));
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MessageDetailActivity.class);
                intent.putStringArrayListExtra(MESSAGE_LIST, favoriteMessages);
                intent.putExtra(POSITION, position);
                intent.putExtra(LISTENING,false);
                intent.putExtra(SUBTITLE_HEADING,mContext.getString(R.string.my_favorites));
                mContext.startActivity(intent);
            }
        });

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showAlert(position);
            }
        });

        setAnimation(holder.container, position);
    }

    private void removePosition(int position) {
        favoriteMessages.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, favoriteMessages.size());
        AppPreferences.setFavoriteList(mContext, favoriteMessages);
    }

    @Override
    public int getItemCount() {
        return favoriteMessages.size();
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

    private void showAlert(final int position)
    {
        new AlertDialog.Builder(mContext)
                .setTitle(R.string.confirmation)
                .setMessage(R.string.delete_confirmation)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        removePosition(position);
                        // Obtain the FirebaseAnalytics instance.
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID,mContext.getString(R.string.analytics_delete_fav));
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, mContext.getString(R.string.analytics_text_deleted));
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, mContext.getString(R.string.text));
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                        updateWidget(mContext);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                     dialog.dismiss();
                    }
                })
                .setIcon(R.drawable.ic_warning_black_24dp)
                .show();
    }

    private static void updateWidget(Context context) {
        int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, Widget.class));
        Widget myWidget = new Widget();
        myWidget.onUpdate(context, AppWidgetManager.getInstance(context),ids);
    }
}
