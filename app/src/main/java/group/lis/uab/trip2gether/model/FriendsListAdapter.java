package group.lis.uab.trip2gether.model;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.ArrayList;
import group.lis.uab.trip2gether.R;
import group.lis.uab.trip2gether.Resources.Utils;

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
        TextView txtEmailUser;
        ImageView imageView;
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
            holder.txtEmailUser = (TextView) row.findViewById(R.id.txtEmailUser);
            row.setTag(holder);
        } else {
            holder = (FriendsListHolder) row.getTag();
        }
        holder.imageView = (ImageView)row.findViewById(R.id.imgIcon);
        holder.txtTitle.setText(friends.get(position).getName());
        holder.txtEmailUser.setText(friends.get(position).getMail());

        try {
            ParseFile file  = Utils.getRegistersFromBBDD(friends.get(position).getObjectId(),"Usuario", "objectId").get(0).getParseFile("Imagen");
            if (file != null) {
                ImageView imageView = holder.imageView;
                Utils.setImageViewWithParseFile(imageView, file, true);
            }else{
                holder.imageView.setImageResource(R.drawable.avatar);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return row;
    }
}