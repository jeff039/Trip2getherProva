package group.lis.uab.trip2gether.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import group.lis.uab.trip2gether.R;
import group.lis.uab.trip2gether.Resources.Encrypt;
import group.lis.uab.trip2gether.Resources.Utils;
import group.lis.uab.trip2gether.controller.EditTripForm;
import group.lis.uab.trip2gether.controller.Friends;
import group.lis.uab.trip2gether.controller.NotificationList;

/**
 * Created by Jofré on 27/04/2015.
 */
public class FriendsListAdapter extends ArrayAdapter<User> {
    private Context context;
    private int layoutResourceId;
    private User myUser;
    private ArrayList<User> friends = null;

    /**
     * Constructor class NotificationListAdapter
     * @param context
     * @param resource
     * @param friends
     */
    public FriendsListAdapter(Context context, int resource, ArrayList<User> friends,
                                   User myUser) {
        super(context, resource, friends);
        this.context = context;
        this.layoutResourceId = resource;
        this.friends = friends;
        this.myUser = myUser;
    }

    static class FriendsListHolder {
        TextView txtTitle;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        FriendsListHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new FriendsListHolder();

            holder.txtTitle = (TextView) row.findViewById(R.id.txtTitle);

            row.setTag(holder);
        } else {
            holder = (FriendsListHolder) row.getTag();
        }
        holder.txtTitle.setText(friends.get(position).getName());
        return row;
    }
}
