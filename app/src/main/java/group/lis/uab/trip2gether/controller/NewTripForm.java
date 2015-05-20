package group.lis.uab.trip2gether.controller;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import group.lis.uab.trip2gether.R;
import group.lis.uab.trip2gether.Resources.Utils;
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

    /**
     * LLISTES PER GESTIONAR ELS AMICS
     */
    private ArrayList<String> friendsIdList = new ArrayList<String>(); //llista de ids de amics TOTS (en ordre amb checks)
    private ArrayList<Integer> checkIdList = new ArrayList<Integer>(); //llista de ids dels checks (TOTS)
    private ArrayList<String> friendsMailList = new ArrayList<String>(); //llista dels mails dels amics TOTS
    private ArrayList<Integer> textViewIdList = new ArrayList<Integer>(); //lista de ids dels text view
    // TOT ESTÀ EN ORDRE
    private ArrayList<String> includedFriends = new ArrayList<String>(); //llista de ids dels amics SI INCLOSOS
    // en tot moment
    private ArrayList<Integer> checkedBoxes = new ArrayList<Integer>(); //llista amb els id dels boxes checked
    // abans de tancar el popup

    public ParseFile getFile() {
        return file;
    }
    public void setFile(ParseFile file) {
        this.file = file;
    }
    private ArrayList<String> paises = new ArrayList<String>();
    private ArrayList<String> ciudades = new ArrayList<String>();
    private NewTripForm context = null;

    /**
     * Method onCreate
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trip_form);
        context = this;
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
            cargaSpinnerCiudad(parent.getSelectedItem());
        }
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    /**
     * Method cargaSpinnerCiudad. Carga de manera dinámica las ciudades en función del páis elegido
     * @param pais
     */
    private void cargaSpinnerCiudad(Object pais){
        Spinner spinnerCiudad = (Spinner) findViewById(R.id.SpinnerCiudades);
        try {
            spinnerCiudad.setAdapter(new ArrayAdapter<String> (this, R.layout.spinner_row, Utils.getCitiesOfCountry((String) pais)));
        } catch (ParseException e) {
            e.printStackTrace();
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

            //Try to reduce the necessary memory
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inSampleSize = 2;

            //String picturePath contains the path of selected Image
            ImageView imageView = (ImageView) findViewById(R.id.imageTrip);
            Bitmap image = BitmapFactory.decodeFile(picturePath, options);
            imageView.setImageBitmap(image);

            //file it's a ParseFile that contains the image selected
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 75, stream);
            byte[] dataImage = stream.toByteArray();
            setFile(new ParseFile("imagenViaje.jpeg", dataImage));
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

        final Button addFriendButton = (Button)findViewById(R.id.ImageButtonAddFirends);
        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //quan clickem canviem el color
                //addFriendButton.setImageResource(R.drawable.ic_action_add_group);

                //POPUP add friend
                LayoutInflater layoutInflater
                        = (LayoutInflater)getBaseContext()
                        .getSystemService(LAYOUT_INFLATER_SERVICE);

                final View popupView = layoutInflater.inflate(R.layout.add_friends_popup, null);

                final PopupWindow popupWindow = new PopupWindow(
                        popupView,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                popupWindow.setFocusable(true); //per evitar back

                //PRIMER COP?
                if(!checkIdList.isEmpty()) { //al eliminar el popup, es reseteja la vista

                    //RECORDEM ELS AMICS ANTERIORS JA CLICATS SI HAVIA
                    setPopUpContent(popupView, false);
                    for (int i = 0; i < checkedBoxes.size(); i++) {
                        CheckBox cb = (CheckBox) popupView.findViewById(checkedBoxes.get(i));
                        cb.setChecked(true);
                    }
                }else {
                   setPopUpContent(popupView, true); //posem el content del primer cop
                }

                //BOTÓ OK DEL POPUP
                //afegim els amics marcats al grup
                Button btnOK = (Button)popupView.findViewById(R.id.okFriends);
                btnOK.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        //addFriendButton.setImageResource(R.drawable.ic_action_add_group);

                       //PER TOT ELS CHECKS MIREM QUIN ESTÀ CHECKED
                        for(int i = 0; i < checkIdList.size(); i++) {
                            //AGAFEM ELS ELEMENTS
                            CheckBox cb = (CheckBox) popupView.findViewById(checkIdList.get(i));
                            String idFriend = friendsIdList.get(i);
                            String mailFriend = friendsMailList.get(i);

                            if(cb.isChecked()) {
                                if(!includedFriends.contains(idFriend)) { //NO AMIC REPETIT
                                    //GUARDEM ELS AMICS I BOXES SELECCIONATS LÒGICAMENT
                                    includedFriends.add(idFriend);
                                    checkedBoxes.add(cb.getId());

                                    //MOSTREM EN PANTALLA
                                    LinearLayout addedFriends = (LinearLayout) //linear dels texts views
                                            NewTripForm.this.findViewById(R.id.addedFriendsList);
                                    TextView newTv = new TextView(getApplicationContext());
                                    newTv.setTextColor(Color.BLACK);
                                    int textViewId = Utils.generateViewId();
                                    newTv.setId(textViewId);
                                    textViewIdList.add(textViewId);
                                    newTv.setText(mailFriend);
                                    addedFriends.addView(newTv);
                                }
                            }else { //SI NO ESTÀN CHECKED
                                if(includedFriends.contains(idFriend)) { //SI ESTAVA INCLÒS
                                    // I EL "DESCHECKEGEM" --> ELIMINEM
                                    //VISTA
                                    LinearLayout addedFriends = (LinearLayout) //linear dels texts views
                                            NewTripForm.this.findViewById(R.id.addedFriendsList);
                                    //si l'amic estava inclòs ja tenim el text view

                                    //busquem i eliminem PER EMAIL (la llista de text view no correspon amb la llista de amics
                                    //NOMÉS TEXT VIEW DELS SELECCIONATS!!
                                    boolean found = false;
                                    int iter = 0;
                                    int idRemoveText = 0;
                                    while(!found) {
                                        TextView tv = (TextView) NewTripForm.this.findViewById(textViewIdList.get(iter));
                                        if(tv.getText().equals(friendsMailList.get(i))) {
                                            addedFriends.removeView(tv);
                                            int idTv = tv.getId();
                                            textViewIdList.remove(new Integer(idTv)); //per elminar per value i no per key
                                            found = true;
                                        }
                                        iter++;
                                    }
                                    //LÒGICA
                                    includedFriends.remove(idFriend);
                                    checkedBoxes.remove(checkIdList.get(i));
                                }
                            }
                        }
                        popupWindow.dismiss(); //tanquem el popup
                    }});
                popupWindow.showAsDropDown(popupView); //mostrem el popup
            }
        });

        Spinner spinnerPaises = (Spinner) findViewById (R.id.SpinnerPaises);
        try {
            paises = Utils.getCountriesOfBBDD();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ArrayAdapter<String> arrayAdapterPaises = new ArrayAdapter<String> (this, R.layout.spinner_row, paises);
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
     * Method setPopUpContent
     * @param popupView
     * @param first
     */
    public void setPopUpContent(View popupView, boolean first) {
        //Contingut de la vista DINÀMIC segons els amics
        //agafem de la popup view perquè encara no ha estat carregada
        final LinearLayout ll = (LinearLayout) popupView.findViewById(R.id.linearFriends);
        String userId = myUser.getObjectId();

        //QUERY AMICS
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("userId", userId);
        List<ParseObject> friendsResponse = null; //crida al BE
        try {
            //busquem la amistat
            friendsResponse = ParseCloud.callFunction("getUserFriends", params);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(friendsResponse.isEmpty() == false) { //tenim un amic
            // AFEGIM ELS ELEMENTS DE LA VISTA I ACTUALITZEM LES LLISTES AMB ELS AMICS (TOTS)

            for(int i = 0; i < friendsResponse.size(); i++) { //per tots els amics
                ParseObject userFriendsParse = friendsResponse.get(i);
                String friendId = userFriendsParse.getString("Id_Usuario_2"); //id de l'amic
                //QUERY DETALLS AMIC
                //busquem els detalls de la amistat
                HashMap<String, Object> params2 = new HashMap<String, Object>();
                params2.put("userId", friendId);
                ////////////////////////////////QUERY MAIL///////////////
                List<ParseObject> userResponse = null; //crida al BE
                try {
                    //busquem la amistat
                    userResponse = ParseCloud.callFunction("getUserFromId", params2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                ParseObject userParse = userResponse.get(0);
                String friendEmail = userParse.getString("Nombre"); //email de l'amic

                //CHECKBOXES
                CheckBox cb = new CheckBox(this);
                cb.setText(friendEmail);
                cb.setTextColor(Color.BLACK);
                //SI ÉS EL PRIMER COP S'HA DE CREAR ID NOVA, PERÒ SINÓ HEM DE TORNAR A AFEGIR LA ID ANTIGA (ES MANTÉ)
                int checkId = 0;
                if(first) {
                    checkId = Utils.generateViewId();
                    //INICIALITZEM LLISTES AMB TOTS ELS AMICS I CHECKBOXES
                    checkIdList.add(checkId);
                    friendsMailList.add(friendEmail);
                    friendsIdList.add(friendId);
                }else {
                    checkId = checkIdList.get(i);
                }
                cb.setId(checkId); //li donem una id
                ll.addView(cb); //AFEGIM A LA VISTA
            }

        }else {//no tenim amics
            Utils.showInfoAlert(getResources().getString(R.string.noFriendsAlert), NewTripForm.this);
        }
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
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
                if (nuevoViaje.getNombre().equalsIgnoreCase("")
                        || nuevoViaje.getPais().equalsIgnoreCase("")
                        || nuevoViaje.getCiudad().equalsIgnoreCase("")
                        || nuevoViaje.getFechaInicio() == null
                        || nuevoViaje.getFechaFinal() == null) {
                    Toast.makeText(NewTripForm.this, "All Fields Required.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(NewTripForm.this, TripList.class);
                    intent.putExtra("nombre", nuevoViaje.getNombre());
                    intent.putExtra("pais", nuevoViaje.getPais());
                    intent.putExtra("ciudad", nuevoViaje.getCiudad());
                    intent.putExtra("fechaInicio", nuevoViaje.getFechaInicio());
                    intent.putExtra("fechaFinal", nuevoViaje.getFechaFinal());
                    intent.putExtra("imagen", String.valueOf(nuevoViaje.getImagen()));
                    intent.putExtra("myUser", myUser);
                    String idCiudad = null;
                    try {
                        idCiudad = getIdCiudad(nuevoViaje);
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                    nuevoViaje.setCiudad(idCiudad);
                    Boolean savedCorrectly = false;
                    try {
                        savedCorrectly = GuardarViajeBDD(nuevoViaje);
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                    if(savedCorrectly ) {
                        ///creeem el grup
                        //AFEGIM AL GRUP
                        for (int i = 0; i < includedFriends.size(); i++) {
                            HashMap<String, Object> params = new HashMap<String, Object>();
                            params.put("Id_Viaje", nuevoViaje.getId());
                            params.put("Id_Usuario", includedFriends.get(i));
                            try {
                                ParseCloud.callFunction("addGroupFriend", params);
                            } catch (ParseException e2) {
                                e2.printStackTrace();
                            }
                            //ENVIEM NOTIFICACIONS ALS UDUARIS AFEGITS DE TIPUS add
                            //(SEND GENERIC NOTIFICATION)
                            HashMap<String, Object> params2 = new HashMap<String, Object>();
                            params2.put("transmitterId", myUser.getObjectId());
                            params2.put("receiverId", includedFriends.get(i));
                            params2.put("type", "add"); //HARDCODED
                            //estat a false per defecte ja al cloud

                            try {
                                ParseCloud.callFunction("addNotification", params2);
                                int prova = 0;
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                        }
                        //////////////////////////////////////
                        startActivity(intent);
                    }else{
                        Toast.makeText(NewTripForm.this, "Can't create de Trip, please try again." , Toast.LENGTH_SHORT).show();
                    }
                }
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
    public Date ConvertStringToDate(String fecha) {
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