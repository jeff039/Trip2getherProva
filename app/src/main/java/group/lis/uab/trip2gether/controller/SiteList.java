
package group.lis.uab.trip2gether.controller;

        import android.content.Intent;
        import android.graphics.Color;
        import android.graphics.drawable.ColorDrawable;
        import android.support.v7.app.ActionBarActivity;
        import android.os.Bundle;
        import android.view.LayoutInflater;
        import android.view.Menu;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.TextView;
        import android.support.v4.app.Fragment;
        import android.view.Menu;
        import android.view.MenuItem;

        import group.lis.uab.trip2gether.R;

/**
 * Created by Mireia on 25/03/2015.
 */
public class SiteList  extends ActionBarActivity {

    private static Intent intent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_list);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        intent = getIntent();

        //Mostrem la Action Bar en la activity
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(75, 74, 104)));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_action);
        getSupportActionBar().setTitle("      Mis Sitios");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_site_list, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.addSite)
        {
            Intent newSite = new Intent(this, NewSiteForm.class);
            startActivity(newSite);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     *
     *
     *
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_site_list, container, false);

            if(intent!=null){
                String nombre = intent.getStringExtra("nombre");
                String descripcion = intent.getStringExtra("descripcion");
                String duracion = intent.getStringExtra("duracion");
                String puntuacion_Total= intent.getStringExtra("puntuacion_Total");


                TextView nombreSitio = (TextView) rootView.findViewById(R.id.nombreSitio);
                nombreSitio.setText(nombre);
            }

            return rootView;
        }
    }

}
