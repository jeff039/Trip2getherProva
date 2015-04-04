package group.lis.uab.trip2gether.controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.database.Cursor;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import group.lis.uab.trip2gether.R;
import group.lis.uab.trip2gether.model.DrawerItemClickListener;

public class TripList extends ActionBarActivity {

    protected Cursor cursor;

    protected ListAdapter adapter;

    protected  ListView lista;

    private static Context context = null;

    private static Intent intent = null;

    String[] listaViajes = new String[] {};

    /**
     * Method onCreate
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_list);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        context = this;
        intent = this.getIntent();
        this.setSupportBar();
        this.initializeDrawerLayout();
        //this.initializeButtons();
    }

    /**
     * Method initializeButtons. Elements de la interfície
     */
    public void initializeButtons(){
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

    /**
     * Method initializeDrawerLayout. Drawer layout
     */
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

    /**
     * Method onCreateOptionsMenu. Barra superior
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trip_list, menu);

        return true;
    }

    /**
     * Method onOptionsItemSelected
     * @param item
     * @return true
     */
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

    /**
     * Method setSupportBar. Action Bar personalitzada
     */
    public void setSupportBar(){
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(75, 74, 104)));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_action);
        getSupportActionBar().setTitle("      Mis Viajes");
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public class PlaceholderFragment extends Fragment{

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_trip_list, container, false);
            setContentView(R.layout.fragment_trip_list);
            if(intent.getStringExtra("nombre")!=null){
                String nombre = intent.getStringExtra("nombre");
                nombreSitio = nombre;
                String pais = intent.getStringExtra("pais");
                String ciudad = intent.getStringExtra("ciudad");
                String fechaInicio = intent.getStringExtra("fechaInicio");
                String fechaFinal = intent.getStringExtra("fechaFinal");

                listaViajes = new String[] {nombre};
                lista = (ListView)findViewById(R.id.listaViajes);
                ArrayAdapter<String> adaptador = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1, listaViajes);
                lista.setAdapter(adaptador);

                lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position,
                                            long id) {
                        Intent intent = new Intent (TripList.this, SiteList.class);
                        intent.putExtra("nombreViaje", nombreSitio);
                        startActivity(intent);
                    }
                });

            }

            return rootView;
        }
     };

    private String nombreSitio = null;
}