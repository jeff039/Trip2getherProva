package group.lis.uab.trip2gether.model;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by davidcara on 29/3/15.
 */
public class DrawerItemClickListener implements ListView.OnItemClickListener {
    @SuppressWarnings("rawtypes")
    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id) {
        //quan clickem:
        switch (position){
            case 0:	openMyScans();
                break;
            case 1: openSettings(); //obrim la activity de settings (IDIOMA etc)
                break;
        }
    }

    public void openMyScans(){
        //start activity "myScans"
    }

    public void openSettings(){
        //start activity "settings"
    }
}
