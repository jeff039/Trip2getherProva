package group.lis.uab.trip2gether.controller;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.support.v7.widget.Toolbar;

import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import group.lis.uab.trip2gether.R;
import group.lis.uab.trip2gether.model.Trip;
import group.lis.uab.trip2gether.model.User;

public class NewTripForm extends ActionBarActivity {

    /**
     * int LOAD_IMAGE. Static final -> no canviarà (s'ha d'inicializar)
     */
    private static final int LOAD_IMAGE = 1;
    private EditText pickDateIni;
    private DatePickerDialog pickDateDialogIni;
    private EditText pickDateFin;
    private DatePickerDialog pickDateDialogFin;
    private SimpleDateFormat dateFormatter;
    private static Intent intentR = null;
    private User myUser;
    private ParseFile file;
    private Toolbar mToolbar;

    public ParseFile getFile() {
        return file;
    }

    public void setFile(ParseFile file) {
        this.file = file;
    }


    private static String[] paises = { "Escoge un País", "España", "Alemania", "Francia"};

    /**
     * Method onCreate
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trip_form);

        mToolbar = (Toolbar) findViewById(R.id.action_bar_new_trip);
        setSupportActionBar(mToolbar);

        this.initializeButtons();
        this.setDateTimeFieldIni();
        this.setDateTimeFieldFin();
        intentR = this.getIntent();
        myUser = (User) intentR.getSerializableExtra("myUser");
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

            ImageView imageView = (ImageView) findViewById(R.id.imageTrip);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

            //file it's a ParseFile that contains the image selected
            Bitmap image = BitmapFactory.decodeFile(picturePath);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] dataImage = stream.toByteArray();
            setFile(new ParseFile("imagenViaje.png", dataImage));
            try {
                file.save();
            } catch (ParseException e) {
                e.printStackTrace();
            }
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

        ImageButton backActivity = (ImageButton)findViewById(R.id.backActvity);
        backActivity.setOnClickListener(doBackActivity);

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


        pickDateIni = (EditText) findViewById(R.id.EditTextFechaInicio);
        pickDateIni.setOnClickListener(clickPickDateIni);
        pickDateIni.setInputType(InputType.TYPE_NULL);
        pickDateIni.setOnFocusChangeListener(focusPickDateIni);

        pickDateFin = (EditText) findViewById(R.id.EditTextFechaFinal);
        pickDateFin.setOnClickListener(clickPickDateFin);
        pickDateFin.setInputType(InputType.TYPE_NULL);
        pickDateFin.setOnFocusChangeListener(focusPickDateFin);

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
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
     * Method ImageButton.OnClickListener doBackActivity
     */
    public ImageButton.OnClickListener doBackActivity = new Button.OnClickListener() {
        public void onClick(View v) {
            Intent i = new Intent(NewTripForm.this, TripList.class);
            i.putExtra("myUser", myUser);
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

            EditText TextNombre =(EditText)findViewById(R.id.EditTextNombre);
            String search = TextNombre.getText().toString();
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
                intent.putExtra("imagen", String.valueOf(nuevoViaje.getImagen()));
                intent.putExtra("myUser", myUser);

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

        ParseFile imagen = getFile();

        return new Trip(nombre, pais, ciudad, dataInicial, dataFinal, imagen);
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
        params.put("imagen", nuevoViaje.getImagen());

        String addTripResponse = ParseCloud.callFunction("addTrip", params);
        if(!addTripResponse.isEmpty())
            nuevoViaje.setId(addTripResponse);
            CrearGrupoBDD(nuevoViaje);
            success = true;
        Log.i("Add newTrip:", addTripResponse);

        return success;
    }


    /**
     * Method CrearGrupoBDD
     * @param nuevoViaje
     * @return success
     * @throws ParseException
     */
    public boolean CrearGrupoBDD(Trip nuevoViaje) throws ParseException{
        boolean success = false;
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("Id_Viaje", nuevoViaje.getId());
        params.put("Id_Usuario", myUser.getObjectId());

        String addGroupResponse = ParseCloud.callFunction("addGroup", params);
        if(!addGroupResponse.isEmpty())
            success = true;
        Log.i("Add newGroup:", addGroupResponse);

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

    private void setDateTimeFieldIni() {
        Calendar newCalendar = Calendar.getInstance();
        pickDateDialogIni = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                pickDateIni.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }

    public EditText.OnClickListener clickPickDateIni = new EditText.OnClickListener() {
        @Override
        public void onClick(View view) {
            pickDateDialogIni.show();
        }
    };

    public EditText.OnFocusChangeListener focusPickDateIni = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus)
                pickDateDialogIni.show();
            v.clearFocus();
        }
    };


    private void setDateTimeFieldFin() {
        Calendar newCalendar = Calendar.getInstance();
        pickDateDialogFin = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                pickDateFin.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }

    public EditText.OnClickListener clickPickDateFin = new EditText.OnClickListener() {
        @Override
        public void onClick(View view) {
            pickDateDialogFin.show();
        }
    };

    public EditText.OnFocusChangeListener focusPickDateFin = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus)
                pickDateDialogFin.show();
            v.clearFocus();
        }
    };
}
