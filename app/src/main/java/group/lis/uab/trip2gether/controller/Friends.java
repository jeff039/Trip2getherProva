package group.lis.uab.trip2gether.controller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import group.lis.uab.trip2gether.R;
import group.lis.uab.trip2gether.Resources.Utils;
import group.lis.uab.trip2gether.model.FriendsListAdapter;
import group.lis.uab.trip2gether.model.User;

public class Friends extends ActionBarActivity {
    private ArrayList<User> friends = new ArrayList<User>();
    private ArrayList<String> friendsNames = new ArrayList<String>();
    protected ListView lista;
    private static Context context = null;
    private Toolbar mToolbar;
    private ListView leftDrawerList;
    private DrawerLayout mDrawerLayout;
    private SmoothActionBarDrawerToggle mDrawerToggle;
    private ArrayAdapter<String> navigationDrawerAdapter;
    User myUser;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        context = this;
        Intent intent = getIntent();
        myUser = (User) intent.getSerializableExtra("myUser");

        setRef();
        //Set the custom toolbar
        setSupportActionBar(mToolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new SmoothActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        try {
            this.ViewFriendsFromBBDD();
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }
    }

    private class SmoothActionBarDrawerToggle extends ActionBarDrawerToggle {

        private Runnable runnable;

        public SmoothActionBarDrawerToggle(Friends activity, DrawerLayout drawerLayout, Toolbar toolbar, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
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
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    private void setRef()
    {
        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.action_bar_friends);
        leftDrawerList = (ListView) findViewById(R.id.left_drawer);

        View list_header = getLayoutInflater().inflate(R.layout.drawerlist_header, null);
        leftDrawerList.addHeaderView(list_header);
        String [] options = getResources().getStringArray(R.array.options_array);
        navigationDrawerAdapter = new ArrayAdapter<String>(Friends.this, R.layout.drawer_list_item, options);
        leftDrawerList.setAdapter(navigationDrawerAdapter);
        leftDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    public void ViewFriendsFromBBDD() throws com.parse.ParseException {
        final List<ParseObject> idsFriends = Utils.getRegistersFromBBDD(myUser.getObjectId(), "Amistad", "Id_Usuario_1");

        for(int i=0;i<idsFriends.size();i++){
            ParseObject idFriends = idsFriends.get(i);
            List<ParseObject> friends = Utils.getRegistersFromBBDD(idFriends.getString("Id_Usuario_2"), "Usuario", "objectId");
            ParseObject idFriend = friends.get(0);
            User friend = new User(idFriend.getString("Mail"), idFriend.getString("Password"),
                    idFriend.getString("Nombre"), idFriend.getString("Apellidos"),
                    idFriend.getString("Pais"), idFriend.getString("Ciudad"), idFriend.getDate("Fecha_Nacimiento"),
                    idFriend.getObjectId());
                this.friends.add(friend);
                this.friendsNames.add(friend.getName());

        }

        FriendsListAdapter adaptador = new FriendsListAdapter(context, R.layout.friends_item_row, friends, myUser);
        lista = (ListView)findViewById(R.id.listaAmigos);
        lista.setAdapter(adaptador);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View v, final int position, long id) {
                //ELIMINAR AMIGO
                String alertString = getResources().getString(R.string.friendDel); //missatge de alerta
                new AlertDialog.Builder(Friends.this)
                        //.setTitle("DB")
                        .setMessage(alertString)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) { //si és sí, eliminem

                                String friendId = friends.get(position).getObjectId();
                                String friendId2 = myUser.getObjectId();
                                //(NO PODEN HVER DOS AMISTATS AMB LA MATEIXA PERSONA)
                                ///////QUERY DEL FRIEND/////////////////////
                                //eliminem la amistat en els dos sentits
                                HashMap<String, Object> paramsQuery = new HashMap<String, Object>();
                                paramsQuery.put("friend_Id", friendId); //ell cap a mi
                                paramsQuery.put("myId", friendId2);
                                HashMap<String, Object> paramsQuery2 = new HashMap<String, Object>();
                                paramsQuery2.put("friend_Id", friendId); //jo cap a ell
                                paramsQuery2.put("myId", friendId2);

                                //AMISTAT EN ELS DOS SENTITS
                                try {
                                    ParseCloud.callFunction("delFriendship", paramsQuery);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    ParseCloud.callFunction("delFriendshipReverse", paramsQuery2);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                //refresquem
                                Intent refresh = new Intent(Friends.this, Friends.class);
                                refresh.putExtra("myUser", myUser);
                                //alhora de fer back ja no tornem aquí
                                Friends.this.finish(); //finalitzem l'activitat actual
                                startActivity(refresh);
                            }
                        })

                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert) //ICONA!!!!
                        .show();
                return true;
            }
        });
    }

    /**
     * Method onCreateOptionsMenu. Action Bar
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friends, menu);
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
            case (R.id.addFriend):
                Intent newTrip = new Intent(this, AddFriend.class);
                newTrip.putExtra("myUser", myUser);
                startActivity(newTrip);
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

    /**
     * Method openMyProfile
     */
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