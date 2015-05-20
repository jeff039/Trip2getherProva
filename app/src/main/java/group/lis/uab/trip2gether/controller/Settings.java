package group.lis.uab.trip2gether.controller;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import group.lis.uab.trip2gether.R;
import group.lis.uab.trip2gether.model.User;

public class Settings extends ActionBarActivity {
    private Toolbar mToolbar;
    private User myUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.action_bar_settings);
        setSupportActionBar(mToolbar);
        this.initializeButtons();
    }

    public void initializeButtons() {
        ImageButton backActivity = (ImageButton)findViewById(R.id.backActivity);
        backActivity.setOnClickListener(doBackActivity);
    }

    /**
     * Method ImageButton.OnClickListener doBackActivity
     */
    public ImageButton.OnClickListener doBackActivity = new Button.OnClickListener() {
        public void onClick(View v) {
            onBackPressed();
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}