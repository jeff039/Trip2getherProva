package group.lis.uab.trip2gether.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import java.util.HashMap;
import java.util.List;
import group.lis.uab.trip2gether.R;
import group.lis.uab.trip2gether.Resources.Utils;
import group.lis.uab.trip2gether.model.User;

public class UserProfile extends ActionBarActivity {

    private User myUser;
    private static Intent intent;
    private Toolbar mToolbar;
    private ListView leftDrawerList;
    private DrawerLayout mDrawerLayout;
    private SmoothActionBarDrawerToggle mDrawerToggle;
    private ArrayAdapter<String> navigationDrawerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        intent = this.getIntent();

        setRef();
        setSupportActionBar(mToolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new SmoothActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        myUser = (User) intent.getSerializableExtra("myUser");
        this.initializeButtons();
        this.initializeUserData();
    }

    private class SmoothActionBarDrawerToggle extends ActionBarDrawerToggle {

        private Runnable runnable;

        public SmoothActionBarDrawerToggle(UserProfile activity, DrawerLayout drawerLayout, Toolbar toolbar, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
            super(activity, drawerLayout, toolbar, openDrawerContentDescRes, closeDrawerContentDescRes);
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);
            invalidateOptionsMenu();
        }
        @Override
        public void onDrawerClosed(View view) {
            super.onDrawerClosed(view);
            invalidateOptionsMenu();
        }
        @Override
        public void onDrawerStateChanged(int newState) {
            super.onDrawerStateChanged(newState);
            if (runnable != null && newState == DrawerLayout.STATE_IDLE) {
                runnable.run();
                runnable = null;
            }
        }
        public void runWhenIdle(Runnable runnable) {
            this.runnable = runnable;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (0) : {
                if (resultCode == Activity.RESULT_OK) {
                    myUser = (User) data.getSerializableExtra("myUser");
                    this.initializeUserData();
                }
                break;
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    private void setRef() {
        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.action_bar_user_profile);
        leftDrawerList = (ListView) findViewById(R.id.left_drawer);
        View list_header = getLayoutInflater().inflate(R.layout.drawerlist_header, null);
        leftDrawerList.addHeaderView(list_header);
        String [] options = getResources().getStringArray(R.array.options_array);
        navigationDrawerAdapter = new ArrayAdapter<String>(UserProfile.this, R.layout.drawer_list_item, options);
        leftDrawerList.setAdapter(navigationDrawerAdapter);
        leftDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

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

            ParseFile image;
            image = Utils.getRegistersFromBBDD(myUser.getObjectId(), "Usuario", "objectId").get(0).getParseFile("Imagen");
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            Utils.setImageViewWithParseFile(imageView, image, true);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method initializeButtons.
     */
    public void initializeButtons(){
        Button addFriend = (Button) findViewById(R.id.addFriend);
        addFriend.setOnClickListener(clickAddFriend);
        ImageButton openEditThisUser = (ImageButton) findViewById(R.id.EditThisUser);
        openEditThisUser.setOnClickListener(clickEditThisUser);
    }

    public Button.OnClickListener clickAddFriend = new Button.OnClickListener() {
        public void onClick(View v) {
        Intent addFriendIntent = new Intent(UserProfile.this, AddFriend.class);
        addFriendIntent.putExtra("myUser", myUser);
        startActivity(addFriendIntent);
        }
    };
    public ImageButton.OnClickListener clickEditThisUser = new ImageButton.OnClickListener() {
        public void onClick(View v) {
            Intent editUserIntent = new Intent(UserProfile.this, EditUserForm.class);
            editUserIntent.putExtra("myUser", myUser);
            startActivity(editUserIntent);
        }
    };

    /**
     * Method onCreateOptionsMenu. Barra superior
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_user_profile, menu);
        return super.onCreateOptionsMenu(menu);
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
            case R.id.edit_user_profile:
                Intent editUserForm = new Intent(this, EditUserForm.class);
                editUserForm.putExtra("myUser", myUser);
                startActivity(editUserForm);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /**
     * Method DrawerItemClickListener
     */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        /**
         * Method onItemClick
         * @param parent
         * @param view
         * @param position
         * @param id
         */
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 1: {
                    mDrawerToggle.runWhenIdle(new Runnable() {
                        @Override
                        public void run() {
                            openMyProfile();
                        }
                    });
                    break;
                }
                case 2: {
                    mDrawerToggle.runWhenIdle(new Runnable() {
                        @Override
                        public void run() {
                            openMyTrips();
                        }
                    });
                    break;
                }
                case 3: {
                    mDrawerToggle.runWhenIdle(new Runnable() {
                        @Override
                        public void run() {
                            openFriends();
                        }
                    });
                    break;
                }
                case 4: {
                    mDrawerToggle.runWhenIdle(new Runnable() {
                        @Override
                        public void run() {
                            openNotificationList();
                        }
                    });
                    break;
                }
                case 5: {
                    mDrawerToggle.runWhenIdle(new Runnable() {
                        @Override
                        public void run() {
                            openSettings();
                        }
                    });
                    break;
                }
                case 6: {
                    mDrawerToggle.runWhenIdle(new Runnable() {
                        @Override
                        public void run() {
                            logout();
                        }
                    });
                    break;
                }
            }
            mDrawerLayout.closeDrawers();
        }
    }

    public void logout() {
        Intent i = new Intent(this, MainLaunchLogin.class);
        startActivity(i);
    }

    public void openSettings() {
        Intent i = new Intent(this, Settings.class);
        startActivity(i);
    }

    public void openMyProfile() {
        Intent userProfile = new Intent(this, UserProfile.class);
        userProfile.putExtra("myUser", myUser);
        startActivity(userProfile);
    }

    public void openMyTrips() {
        Intent myTrips = new Intent(this, TripList.class);
        myTrips.putExtra("myUser", myUser);
        startActivity(myTrips);
    }

    public void openNotificationList() {
        Intent notificationList = new Intent(this, NotificationList.class);
        notificationList.putExtra("myUser", myUser);
        startActivity(notificationList);
    }

    public void openFriends() {
        Intent friendsList = new Intent(this, Friends.class);
        friendsList.putExtra("myUser", myUser);
        startActivity(friendsList);
    }
}