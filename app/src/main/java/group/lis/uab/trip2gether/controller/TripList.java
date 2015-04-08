package group.lis.uab.trip2gether.controller;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.database.Cursor;
import android.widget.ListView;

import group.lis.uab.trip2gether.R;
//Implementar bé els métodes de la classe DrawerItemClickListener;
//import group.lis.uab.trip2gether.model.DrawerItemClickListener;
import group.lis.uab.trip2gether.model.DrawerItemClickListener;
import group.lis.uab.trip2gether.model.User;

public class TripList extends ActionBarActivity {

    private String nombreSitio = null;

    protected Cursor cursor;

    protected ListAdapter adapter;

    protected  ListView lista;

    private static Context context = null;

    private static Intent intent = null;

    String[] listaViajes = new String[] {};

    private User myUser;

    /**
     * Method onCreate
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_list);
        context = this;
        intent = this.getIntent();

        this.setSupportBar();
        this.initializeDrawerLayout();
        this.initializeButtons();
        this.MostrarViajes();
        myUser = (User) intent.getSerializableExtra("myUser");
    }

    public void MostrarViajes(){
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
    }

    /**
     * Method initializeButtons. Elements de la interfície
     */
    public void initializeButtons() {
        ImageButton openDrawer = (ImageButton) findViewById(R.id.openDrawer);
        openDrawer.setOnClickListener(clickDrawer);
    }

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
        ListView mDrawerList = (ListView) findViewById(R.id.left_drawer);
        String [] options = getResources().getStringArray(R.array.options_array);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    private class DrawerItemClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            switch (position){
                case 0:	openMyProfile();
                    break;
            }
        }
    }

    public void openMyProfile() {
        Intent userProfile = new Intent(this, UserProfile.class);
        userProfile.putExtra("myUser", myUser);
        startActivity(userProfile);

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
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_trip_list);
        getSupportActionBar().setTitle("      Mis Viajes");
    }
}