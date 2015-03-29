package group.lis.uab.trip2gether.controller;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ListAdapter;
import android.database.Cursor;
import android.widget.ListView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import com.parse.ParseCloud;

import java.util.ArrayList;
import java.util.HashMap;

import group.lis.uab.trip2gether.R;
import group.lis.uab.trip2gether.model.DrawerItemClickListener;

public class TripList extends ActionBarActivity {
    protected Cursor cursor;

    protected ListAdapter adapter;


    private static Intent intent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_list);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        this.setSupportBar();
        this.initializeDrawerLayout();
        this.initializeButtons();
    }

/////////////ELEMENTS DE LA INTERFÍCIE///////////////
    public void initializeButtons()
    {
        ImageButton openDrawer = (ImageButton) findViewById(R.id.openDrawer);
        openDrawer.setOnClickListener(clickDrawer);
    }

    public Button.OnClickListener clickDrawer = new Button.OnClickListener() {
        public void onClick(View v) {
            DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            if(!mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
            else
            {
                mDrawerLayout.closeDrawer(Gravity.LEFT);
            }
        }
    };



    ////////////////////////////////////////////////////////
    //DRAWER LAYOUT////////////////////////////
    ////////////////////////////////////////////////////
    public void initializeDrawerLayout(){
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //no cal fer un adaptador a la mDrawer,
        //ja est� configurat en la situaci� dels elements al xml
        ListView mDrawerList = (ListView) findViewById(R.id.left_drawer);
        //per� s� en la ListView:
        //agafem les opcions de "strings"
        String [] options = getResources().getStringArray(R.array.options_array);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, options));
        //simple_list_itm_1, �s un layout "prefabricat" que ve amb la API,
        //consistent en un  layout amb un text simple que requereix el ArrayAdapter
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

//////////////BARRA SUPERIOR///////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trip_list, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case (R.id.addTrip):

                Intent newTrip = new Intent(this, NewTripForm.class);
                startActivity(newTrip);
                return true;
            case (R.id.goToSites):
                Intent goToSites = new Intent(this, SiteList.class);
                startActivity(goToSites);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setSupportBar()
    {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(75, 74, 104)));
        //barra personalitzada
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment{

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_trip_list, container, false);

            if(intent!=null){
                String nombre = intent.getStringExtra("nombre");
                String pais = intent.getStringExtra("pais");
                String ciudad = intent.getStringExtra("ciudad");
                String fechaInicio = intent.getStringExtra("fechaInicio");
                String fechaFinal = intent.getStringExtra("fechaFinal");

                TextView nombreViaje = (TextView) rootView.findViewById(R.id.nombreViaje);
                nombreViaje.setText(nombre);

            }

            return rootView;
        }

     };

}