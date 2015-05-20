package group.lis.uab.trip2gether.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import group.lis.uab.trip2gether.R;
import group.lis.uab.trip2gether.Resources.Utils;

public class NoInternetConnection  extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_connection);
        this.initializeButtons();
    }

    private void initializeButtons() {
        Button errorConnection = (Button)findViewById(R.id.tryAgain);
        errorConnection.setOnClickListener(clickRetry);

    }

    public Button.OnClickListener clickRetry = new Button.OnClickListener() {
        public void onClick(View v) {
        if(Utils.isNetworkStatusAvialable(getApplicationContext())) {
            Intent login = new Intent(NoInternetConnection.this, MainLaunchLogin.class);
            startActivity(login);
        }
        else Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_error_connection, menu);
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