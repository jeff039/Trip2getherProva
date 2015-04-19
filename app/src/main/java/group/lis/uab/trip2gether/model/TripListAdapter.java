package group.lis.uab.trip2gether.model;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import group.lis.uab.trip2gether.R;

public class TripListAdapter extends ArrayAdapter<Trip> {

    private Context context;

    private int layoutResourceId;

    private ArrayList<Trip> trips = null;

    /**
     * Constructor class TripListAdapter
     * @param context
     * @param resource
     * @param trips
     */
    public TripListAdapter(Context context, int resource, ArrayList<Trip> trips) {
        super(context, resource, trips);
        this.context = context;
        this.layoutResourceId = resource;
        this.trips = trips;
    }

    static class TripListHolder{
        ImageView imgIcon;
        TextView txtTitle;
    }

    /**
     * Method getView
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        TripListHolder holder = null;

        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new TripListHolder();
            holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
            row.setTag(holder);
        }else {
            holder = (TripListHolder)row.getTag();
        }

        holder.txtTitle.setText(trips.get(position).getNombre());
        holder.imgIcon.setImageResource(R.drawable.background);
        return row;
    }
}


