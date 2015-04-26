package group.lis.uab.trip2gether.controller;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
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
import android.support.v7.widget.Toolbar;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import java.util.HashMap;
import java.util.List;
import group.lis.uab.trip2gether.R;
import group.lis.uab.trip2gether.Resources.Utils;
import group.lis.uab.trip2gether.model.User;

/**
 * Created by Jofré on 31/03/2015.
 */
public class UserProfile extends ActionBarActivity {

    private User myUser;
    private Toolbar mToolbar;
    private ListView leftDrawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Intent intent = getIntent();
        myUser = (User) intent.getSerializableExtra("myUser");
        mToolbar = (Toolbar) findViewById(R.id.action_bar_user_profile);
        setSupportActionBar(mToolbar);
        this.initializeDrawerLayout();
        this.initializeButtons();
        this.initializeUserData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (0) : {
                if (resultCode == Activity.RESULT_OK) {
                    myUser = (User) data.getSerializableExtra("myUser");
                    this.initializeUserData();
                }
                break;
            }
        }
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
        TextView user_friends = (TextView)findViewById(R.id.number_friends_user);
        TextView user_trips = (TextView)findViewById(R.id.number_trips_user);
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("userId", myUser.getObjectId());
        try {
            List<ParseObject> numberFriendsResponse = ParseCloud.callFunction("getUserFriends", params);
            int number_user_friends = numberFriendsResponse.size();
            String text_user_friends = String.valueOf(number_user_friends);
            user_friends.setText(text_user_friends);
            List<ParseObject> numberTripsResponse = Utils.getRegistersFromBBDD(myUser.getObjectId(), "Grupo", "Id_Usuario");
            int number_user_trips = numberTripsResponse.size();
            String text_user_trips = String.valueOf(number_user_trips);
            user_trips.setText(text_user_trips);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method initializeButtons. Button Open Drawer
     */
    public void initializeButtons(){
        ImageButton openDrawer = (ImageButton) findViewById(R.id.openDrawer);
        openDrawer.setOnClickListener(clickDrawer);
        Button addFriend = (Button) findViewById(R.id.addFriend);
        addFriend.setOnClickListener(clickAddFriend);
    }

    /**
     * Method Button.OnClickListener. clickDrawer
     */
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

    public Button.OnClickListener clickAddFriend = new Button.OnClickListener() {
        public void onClick(View v) {
            Intent addFriendIntent = new Intent(UserProfile.this, AddFriend.class);
            addFriendIntent.putExtra("myUser", myUser);
            startActivity(addFriendIntent);
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
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, options));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    /**
     * Method DrawerItemClickListener
     */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            switch (position){
                case 1:
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                    break;
            }
        }
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
                startActivityForResult(editUserForm, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}