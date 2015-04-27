package group.lis.uab.trip2gether.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

import group.lis.uab.trip2gether.R;
import group.lis.uab.trip2gether.Resources.Utils;
import group.lis.uab.trip2gether.model.DrawerItemClickListener;
import group.lis.uab.trip2gether.model.Notification;
import group.lis.uab.trip2gether.model.Site;
import group.lis.uab.trip2gether.model.SiteListAdapter;
import group.lis.uab.trip2gether.model.User;

/**
 * Created by Jofré on 26/04/2015.
 */
public class NotificationList extends ActionBarActivity {
    private ArrayList<Notification> notifications = new ArrayList<Notification>();
    private ArrayList<String> notificationsNames = new ArrayList<String>();
    protected ListView lista;
    private static Context context = null;
    private Toolbar mToolbar;
    private ListView leftDrawerList;
    User myUser;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_list);
        context = this;
        Intent intent = getIntent();
        myUser = (User) intent.getSerializableExtra("myUser");

        mToolbar = (Toolbar) findViewById(R.id.action_bar_notification_list);
        setSupportActionBar(mToolbar);

        this.initializeDrawerLayout();
        this.initializeButtons();

        try {
            this.ViewNotificationsFromBBDD();
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }
    }

    public void ViewNotificationsFromBBDD() throws com.parse.ParseException {
        List<ParseObject> idsNotificacion = Utils.getRegistersFromBBDD(myUser.getObjectId(), "Notificacion", "Id_Receptor");

        for(int i=0;i<idsNotificacion.size();i++){
            ParseObject idNotificacion = idsNotificacion.get(i);
            Notification notificacion = new Notification(idNotificacion.getObjectId(), idNotificacion.getBoolean("Estado"),
                    idNotificacion.getString("Id_Emisor"), idNotificacion.getString("Id_Receptor"), idNotificacion.getString("Id_Tipo"),
                    idNotificacion.getString("Tipo"));
            this.notifications.add(notificacion);
            this.notificationsNames.add(notificacion.getIdEmisor());
        }

        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1, notificationsNames);
        lista = (ListView)findViewById(R.id.listaNotificaciones);
        lista.setAdapter(adaptador);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*Intent intent = new Intent (SiteList.this, SiteView.class);
                intent.putExtra("currentSite", sites.get(position));
                startActivity(intent);*/
            }
        });
    }

    /**
     * Method initializeButtons. Elements de la interfície
     */
    public void initializeButtons() {
        ImageButton openDrawer = (ImageButton) findViewById(R.id.openDrawer);
        openDrawer.setOnClickListener(clickDrawer);
    }

    /**
     * Method Button.OnClickListener clickDrawer
     */
    public Button.OnClickListener clickDrawer = new Button.OnClickListener() {
        public void onClick(View v) {
            DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            if(!mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
            else {
                mDrawerLayout.closeDrawer(Gravity.LEFT);
            }
        }
    };

    /**
     * Method initializeDrawerLayout. Drawer layout
     */
    public void initializeDrawerLayout(){
        leftDrawerList = (ListView) findViewById(R.id.left_drawer);
        View list_header = getLayoutInflater().inflate(R.layout.drawerlist_header, null);
        leftDrawerList.addHeaderView(list_header);

        ListView mDrawerList = (ListView) findViewById(R.id.left_drawer);
        String [] options = getResources().getStringArray(R.array.options_array);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    /**
     * Method onCreateOptionsMenu. Action Bar
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_site_list, menu);
        return true;
    }
}