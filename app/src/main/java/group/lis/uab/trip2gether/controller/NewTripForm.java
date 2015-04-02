package group.lis.uab.trip2gether.controller;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import group.lis.uab.trip2gether.R;
import group.lis.uab.trip2gether.model.Trip;

public class NewTripForm extends ActionBarActivity {

    /**
     * int LOAD_IMAGE. Static final -> no canviarà (s'ha d'inicializar)
     */
    private static final int LOAD_IMAGE = 1;

    private static String[] paises = { "Ninguno", "España", "Alemania", "Francia"};

    /**
     * Method onCreate
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trip_form);
        this.setSupportBar();
        this.initializeButtons();

        ImageButton imageButton = (ImageButton)findViewById(R.id.ImageButtonAddFirends);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewTripForm.this, Friends.class);
                startActivity(intent);
            }
        });

        Spinner spinnerPaises = (Spinner) findViewById (R.id.SpinnerPaises);
        ArrayAdapter<String> arrayAdapterPaises = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_item, paises);
        spinnerPaises.setAdapter(arrayAdapterPaises);
        spinnerPaises.setOnItemSelectedListener(new SpinnerListener());
    }

    public class SpinnerListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            cargaSpinnerCiudad(parent.getSelectedItemPosition());
        }
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    /**
     * Method cargaSpinnerCiudad. Carga de manera dinámica las ciudades en función del páis elegido
     * @param pais
     */
    private void cargaSpinnerCiudad(int pais){
        Spinner Ciudades = (Spinner) findViewById(R.id.SpinnerCiudades);

        ArrayAdapter arrayAdapterEspaña = ArrayAdapter.createFromResource
                (this, R.array.CiudadesEspaña, android.R.layout.simple_spinner_item);

        ArrayAdapter arrayAdapterAlemania= ArrayAdapter.createFromResource
                (this, R.array.CiudadesAlemania, android.R.layout.simple_spinner_item);

        ArrayAdapter arrayAdapterFrancia= ArrayAdapter.createFromResource
                (this, R.array.CiudadesFrancia, android.R.layout.simple_spinner_item);

        ArrayAdapter arrayAdapterDefault = ArrayAdapter.createFromResource
                (this, R.array.arrayDefault, android.R.layout.simple_spinner_item);

        switch (pais) {
            case 1: Ciudades.setAdapter(arrayAdapterEspaña);
                break;
            case 2: Ciudades.setAdapter(arrayAdapterAlemania);
                break;
            case 3: Ciudades.setAdapter(arrayAdapterFrancia);
                break;
            default: Ciudades.setAdapter(arrayAdapterDefault);
                break;
        }
    }

    /**
     * Method setSupportBar. Action Bar personalitzada
     */
    public void setSupportBar(){
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(75, 74, 104)));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_action_cancel);
        getSupportActionBar().setTitle("      Nuevo Viaje");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            // String picturePath contains the path of selected Image
            ImageView imageView = (ImageView) findViewById(R.id.image);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

            ///////////////////PROVA UPDATE///////////////////
            //commit2
            /*
            Bitmap image = BitmapFactory.decodeFile(picturePath);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] dataImage = stream.toByteArray();
            ParseFile file = new ParseFile("viaje.png", dataImage);


            ParseQuery query = new ParseQuery("Viaje");
            try {
                ParseObject o = query.get("ATDImGclly");
                o.put("Imagen",file);
                o.save();
            } catch (ParseException e) {
                e.printStackTrace();
            }*/
        }
    }

    /**
     * Method initializeButtons. Interficie
     */
    private void initializeButtons() {
        Button gallery = (Button)findViewById(R.id.gallery);
        gallery.setOnClickListener(clickGallery);
        Button google = (Button)findViewById(R.id.google);
        google.setOnClickListener(clickGoogle);
        Button maps = (Button)findViewById(R.id.maps);
        maps.setOnClickListener(clickMaps);
    }

    /**
     * Method Button.OnClickListener clickMaps
     */
    public Button.OnClickListener clickMaps = new Button.OnClickListener() {
        public void onClick(View v) {
            Intent i = new Intent(NewTripForm.this, SiteMapsActivity.class);
            startActivity(i);
        }
    };

    /**
     * Method Button.OnClickListener clickGallery
     */
    public Button.OnClickListener clickGallery = new Button.OnClickListener() {
        public void onClick(View v) {
            Intent i = new Intent(
                    Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(i, LOAD_IMAGE);
        }
    };

    /**
     * Method Button.OnClickListener clickGoogle
     */
    public Button.OnClickListener clickGoogle = new Button.OnClickListener() {
        public void onClick(View v) {
            String search = "fish";
            Uri uri = Uri.parse("https://www.google.com/search?hl=en&site=imghp&tbm=isch&source=hp&q="+search);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    };

    /**
     * Method onCreateOptionsMenu. Action Bar
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_trip_form, menu);
        return true;
    }

    /**
     * Method onOptionsItemSelected
     * @param item
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.saveTrip:
                Trip nuevoViaje = CargarViaje();
                Intent intent = new Intent (NewTripForm.this, TripList.class);
                intent.putExtra("nombre", nuevoViaje.getNombre());
                intent.putExtra("pais", nuevoViaje.getPais());
                intent.putExtra("ciudad", nuevoViaje.getCiudad());
                intent.putExtra("fechaInicio", nuevoViaje.getFechaInicio());
                intent.putExtra("fechaFinal", nuevoViaje.getFechaFinal());

                try {
                    String idCiudad = getIdCiudad(nuevoViaje);
                    nuevoViaje.setCiudad(idCiudad);
                    GuardarViajeBDD(nuevoViaje);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                startActivity(intent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * CargarViaje. Recuperamos la información de los datos del viaje especificado por el usuario
     */
    public Trip CargarViaje(){
        EditText TextNombre =(EditText)findViewById(R.id.EditTextNombre);
        String nombre = TextNombre.getText().toString();

        Spinner TextPais =(Spinner)findViewById(R.id.SpinnerPaises);
        String pais = (String)TextPais.getSelectedItem();

        Spinner TextCiudad =(Spinner)findViewById(R.id.SpinnerCiudades);
        String ciudad = (String)TextCiudad.getSelectedItem();

        EditText TextFechaInicio =(EditText)findViewById(R.id.EditTextFechaInicio);
        String fechaInicio = TextFechaInicio.getText().toString();

        EditText TextFechaFinal =(EditText)findViewById(R.id.EditTextFechaFinal);
        String fechaFinal = TextFechaFinal.getText().toString();

        Date dataInicial = ConvertStringToDate(fechaInicio);
        Date dataFinal = ConvertStringToDate(fechaFinal);

        return new Trip(nombre, pais, ciudad, dataInicial, dataFinal);
    }

    /**
     * Method GuardarViajeBDD
     * @param nuevoViaje
     * @return success
     * @throws ParseException
     */
    public boolean GuardarViajeBDD(Trip nuevoViaje) throws ParseException{
        boolean success = false;
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("name", nuevoViaje.getNombre());
        params.put("idCiudad", nuevoViaje.getCiudad());
        params.put("fechaInicial", nuevoViaje.getFechaInicio());
        params.put("fechaFinal", nuevoViaje.getFechaFinal());

        String addTripResponse = ParseCloud.callFunction("addTrip", params);
        if(!addTripResponse.isEmpty())
            success = true;
        Log.i("Add newTrip:", addTripResponse);

        return success;
    }

    /**
     * Method getIdCiudad
     * @param nuevoViaje
     * @return idCiudad
     * @throws ParseException
     */
    public String getIdCiudad(Trip nuevoViaje) throws ParseException{
        boolean success = false;
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("ciudad", nuevoViaje.getCiudad());
        String idCiudad = ParseCloud.callFunction("getIdCity", params);
        return idCiudad;
    }

    /**
     * ConvertStringToDate
     * @param fecha
     * @return data
     */
    public Date ConvertStringToDate(String fecha){
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date data = null;
        try {
            data = formatter.parse(fecha);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return data;
    }
}
