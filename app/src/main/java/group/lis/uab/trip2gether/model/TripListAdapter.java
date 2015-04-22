package group.lis.uab.trip2gether.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.parse.ParseFile;
import java.util.ArrayList;
import group.lis.uab.trip2gether.R;
import group.lis.uab.trip2gether.controller.EditTripForm;
import group.lis.uab.trip2gether.controller.Friends;

public class TripListAdapter extends ArrayAdapter<Trip> {

    private Context context;
    private int layoutResourceId;
    private User myUser;
    private ArrayList<Trip> trips = null;

    /**
     * Constructor class TripListAdapter
     * @param context
     * @param resource
     * @param trips
     */
    public TripListAdapter(Context context, int resource, ArrayList<Trip> trips, User myUser) {
        super(context, resource, trips);
        this.context = context;
        this.layoutResourceId = resource;
        this.trips = trips;
        this.myUser = myUser;
    }

    static class TripListHolder{
        TextView txtTitle;
        ImageView imageView;
        ImageView imgEditIcon;
        ImageView imgFriendsIcon;
        ImageView imgCalendar;
    }

    /**
     * Method getView
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        TripListHolder holder = null;

        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new TripListHolder();
            holder.imageView = (ImageView)row.findViewById(R.id.imgIcon);
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
            holder.imgEditIcon = (ImageView)row.findViewById(R.id.imgEditIcon);
            holder.imgFriendsIcon = (ImageView)row.findViewById(R.id.imgFriendsIcon);
            holder.imgCalendar = (ImageView)row.findViewById(R.id.imgCalendarIcon);
            row.setTag(holder);
        }else {
            holder = (TripListHolder)row.getTag();
        }

        ParseFile file = trips.get(position).getImagen();
        if (file != null) {
            byte[] bitmapdata = new byte[0];
            try {
                bitmapdata = file.getData();
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
            //holder.imgIcon.setBackgroundResource(R.drawable.background);
            holder.imageView.setImageBitmap(bitmap);
        }else{
            holder.imageView.setBackgroundResource(R.drawable.background2);

        }

        holder.txtTitle.setText(trips.get(position).getNombre());
        holder.imgFriendsIcon.setImageResource(R.drawable.ic_managment_friends);
        holder.imgEditIcon.setImageResource(R.drawable.ic_action_edit_white);
        holder.imgCalendar.setImageResource(R.drawable.ic_calendar);

        holder.imgEditIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditTripForm.class);
                intent.putExtra("myUser", myUser);
                intent.putExtra("myTrip", trips.get(position));
                ((Activity)context).startActivity(intent);
            }
        });

        holder.imgFriendsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Friends.class);
                ((Activity)context).startActivity(intent);
            }
        });
        return row;
    }
}


