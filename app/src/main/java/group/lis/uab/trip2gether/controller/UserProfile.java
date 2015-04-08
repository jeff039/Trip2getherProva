package group.lis.uab.trip2gether.controller;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
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
import android.widget.ListView;
import android.widget.TextView;


import group.lis.uab.trip2gether.R;
import group.lis.uab.trip2gether.model.User;

/**
 * Created by Jofré on 31/03/2015.
 */
public class UserProfile extends ActionBarActivity {

    private User myUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Intent intent = getIntent();
        myUser = (User) intent.getSerializableExtra("myUser");
        this.setSupportBar();
        this.initializeDrawerLayout();
        this.initializeButtons();
        this.initializeUserData();

    }

    ////////////INTERFÍCIE/////////////////

    public void initializeUserData() {
        TextView name = (TextView)findViewById(R.id.user_name);
        name.setText(myUser.getName());
        TextView surname = (TextView)findViewById(R.id.user_surname);
        surname.setText(myUser.getSurname());
        TextView city = (TextView)findViewById(R.id.user_city);
        city.setText(myUser.getCity());
        TextView country = (TextView)findViewById(R.id.user_country);
        country.setText(myUser.getCountry());
        TextView mail = (TextView)findViewById(R.id.user_mail);
        mail.setText(myUser.getMail());

    }

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
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);

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
            case (R.id.edit_user_profile):
                Intent editUserForm = new Intent(this, EditUserForm.class);
                editUserForm.putExtra("myUser", myUser);
                startActivity(editUserForm);
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
        getSupportActionBar().setCustomView(R.layout.action_bar_user_profile);
        getSupportActionBar().setTitle("      Edit User Profile");
    }

}