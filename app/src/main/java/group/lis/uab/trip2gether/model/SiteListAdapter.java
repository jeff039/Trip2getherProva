package group.lis.uab.trip2gether.model;

import android.app.Activity;
import android.content.Context;
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

public class SiteListAdapter extends ArrayAdapter<Site> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<Site> sitios = null;

    /**
     * Constructor class TripListAdapter
     * @param context
     * @param resource
     * @param sitios
     */
    public SiteListAdapter(Context context, int resource, ArrayList<Site> sitios) {
        super(context, resource, sitios);
        this.context = context;
        this.layoutResourceId = resource;
        this.sitios = sitios;
    }

    static class SiteListHolder{
        TextView txtName;
        ImageView imageView;
        ImageView imgPosSite;
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
        SiteListHolder holder = null;

        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new SiteListHolder();

            holder.imageView = (ImageView)row.findViewById(R.id.imgIcon);
            holder.txtName = (TextView)row.findViewById(R.id.txtName);
            holder.imgPosSite = (ImageView)row.findViewById(R.id.imgPosSite);

            row.setTag(holder);
        }else {
            holder = (SiteListHolder)row.getTag();
        }

        ParseFile file = sitios.get(position).getImagen();
        if (file != null) {
            byte[] bitmapdata = new byte[0];
            try {
                bitmapdata = file.getData();
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
            holder.imageView.setImageBitmap(bitmap);
        }else{
            holder.imageView.setBackgroundResource(R.drawable.background2);
        }

        holder.txtName.setText(sitios.get(position).getNombre());
        holder.imgPosSite.setImageResource(R.drawable.ic_position_site);

        return row;
    }
}