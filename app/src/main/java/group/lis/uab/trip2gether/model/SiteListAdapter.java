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
        TextView txtTimeHours;
        ImageView imageView;
        ImageView imgPosSite;
        ImageView imgPhotoUser;
        ImageView imgWatch;
        ImageView imgStar1;
        ImageView imgStar2;
        ImageView imgStar3;
        ImageView imgStar4;
        ImageView imgStar5;
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
            holder.imgPhotoUser = (ImageView)row.findViewById(R.id.imgPhotoUser);
            holder.imgWatch = (ImageView)row.findViewById(R.id.imgTime);
            holder.txtTimeHours = (TextView)row.findViewById(R.id.txtTimeHours);

            holder.imgStar1 = (ImageView)row.findViewById(R.id.imgStar1);
            holder.imgStar2 = (ImageView)row.findViewById(R.id.imgStar2);
            holder.imgStar3 = (ImageView)row.findViewById(R.id.imgStar3);
            holder.imgStar4 = (ImageView)row.findViewById(R.id.imgStar4);
            holder.imgStar5 = (ImageView)row.findViewById(R.id.imgStar5);

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
            //Try to reduce the necessary memory
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inSampleSize = 2;

            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length, options);
            holder.imageView.setImageBitmap(bitmap);
        }else{
            holder.imageView.setBackgroundResource(R.drawable.background2);
        }

        holder.txtName.setText(sitios.get(position).getNombre());
        holder.imgPosSite.setImageResource(R.drawable.ic_position_site);
        holder.imgWatch.setImageResource(R.drawable.ic_watch);

        String time = String.valueOf((int) sitios.get(position).getDuracion());
        holder.txtTimeHours.setText(time+"h");

        holder.imgStar1.setImageResource(R.drawable.ic_stars_white_48dp);
        holder.imgStar2.setImageResource(R.drawable.ic_stars_white_48dp);
        holder.imgStar3.setImageResource(R.drawable.ic_stars_white_48dp);
        holder.imgStar4.setImageResource(R.drawable.ic_stars_grey600_48dp);
        holder.imgStar5.setImageResource(R.drawable.ic_stars_grey600_48dp);

        return row;
    }
}