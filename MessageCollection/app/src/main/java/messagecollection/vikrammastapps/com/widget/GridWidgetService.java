package messagecollection.vikrammastapps.com.widget;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import java.util.ArrayList;

import messagecollection.vikrammastapps.com.R;
import messagecollection.vikrammastapps.com.activities.FavoriteActivity;
import messagecollection.vikrammastapps.com.utils.AppPreferences;

public class GridWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        // TODO Auto-generated method stub
        return new GridRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}


class GridRemoteViewsFactory implements RemoteViewsFactory {

    public static final String EXTRA_ITEM = "messagecollection.vikrammastapps.com.EXTRA_ITEM";
    private Context mContext;
    private ArrayList<String> favMessages;

    public GridRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;


    }

    @Override
    public void onCreate() {

        if(AppPreferences.getFavoriteList(mContext)!=null && AppPreferences.getFavoriteList(mContext).size()>0) {
            favMessages = AppPreferences.getFavoriteList(mContext);
        }else{
            favMessages = new ArrayList<>();
        }

    }

    @Override
    public void onDataSetChanged() {
        // TODO Auto-generated method stub

        Thread thread = new Thread() {
            public void run() {
                if(AppPreferences.getFavoriteList(mContext)!=null && AppPreferences.getFavoriteList(mContext).size()>0) {
                    favMessages = AppPreferences.getFavoriteList(mContext);
                }else{
                    favMessages = new ArrayList<>();
                }
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
          return AppPreferences.getFavoriteList(mContext).size();
    }

    @Override
    public RemoteViews getViewAt(int position) {


        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);

            rv.setTextViewText(R.id.tvMessage,favMessages.get(position));
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public int getViewTypeCount() {
        // TODO Auto-generated method stub
        return AppPreferences.getFavoriteList(mContext).size();
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return true;
    }
}
