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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import android.support.v7.widget.Toolbar;
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

public class EditTripForm extends ActionBarActivity {

    private static final int LOAD_IMAGE = 1;
    private EditText pickDateIni;
    private DatePickerDialog pickDateDialogIni;
    private EditText pickDateFin;
    private DatePickerDialog pickDateDialogFin;
    private SimpleDateFormat dateFormatter;
    private static Intent intentR = null;
    private User myUser;
    private Trip myTrip;
    private ParseFile file;
    private static String[] paises = { "Ninguno", "España", "Alemania", "Francia"};

    //LLISTES PER GESTIONAR ELS AMICS
    private ArrayList<String> friendsIdList = new ArrayList<String>(); //llista de ids de amics TOTS (en ordre amb checks)
    private ArrayList<Integer> checkIdList = new ArrayList<Integer>(); //llista de ids dels checks (TOTS)
    private ArrayList<String> friendsMailList = new ArrayList<String>(); //llista dels mails dels amics TOTS
    private ArrayList<Integer> textViewIdList = new ArrayList<Integer>(); //lista de ids dels text view
    // TOT ESTÀ EN ORDRE
    private ArrayList<String> includedFriends = new ArrayList<String>(); //llista de ids dels amics SI INCLOSOS
    // en tot moment
    private ArrayList<Integer> checkedBoxes = new ArrayList<Integer>(); //llista amb els id dels boxes checked
    // abans de tancar el popup
    private List<ParseObject> participantesViaje; //llista amb els participants del viatje.
    private ArrayList<ParseObject> datosUsuarioGrupo; //llista amb les dades d'usuari del grup del viaje.
    private ArrayList<String> participantGroupToAdd = new ArrayList<String>(); //llista de ids dels amics que s'han d'afegir
    private ArrayList<String> participantGroupToDelete = new ArrayList<String>(); //llista de ids dels amics que s'han d'eliminar
    private ArrayList<String> participantGroupTrip = new ArrayList<String>(); //llista de ids dels participants actuals
    // del grup d'un viaje
    ///////////////////////////////////////////

    public User getMyUser() {
        return myUser;
    }
    public void setMyUser(User user) {
        this.myUser = user;
    }

    public Trip getMyTrip() {
        return myTrip;
    }
    public void setMyTrip (Trip myTrip) {
        this.myTrip = myTrip;
    }

    public ParseFile getFile() {
        return file;
    }
    public void setFile(ParseFile file) {
        this.file = file;
    }

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trip_form);

        mToolbar = (Toolbar) findViewById(R.id.action_bar_edit_trip);
        setSupportActionBar(mToolbar);

        Intent intent = getIntent();
        setMyUser((User) intent.getSerializableExtra("myUser"));
        setMyTrip((Trip) intent.getSerializableExtra("myTrip"));
        this.initializeButtons();
        this.setDateTimeFieldIni();
        this.setDateTimeFieldFin();
        try {
            this.initializeTripData();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_trip_form, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.saveEditTrip) {
            Trip nuevoViaje = CargarViaje();
            nuevoViaje.setId(myTrip.getId());
            //Trip nuevoViaje = getMyTrip();
            Intent intent = new Intent (EditTripForm.this, TripList.class);
            intent.putExtra("myUser", myUser);

            try {
                String idCiudad = getIdCiudad(nuevoViaje);
                nuevoViaje.setCiudad(idCiudad);
                EditarViajeBDD(nuevoViaje);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            //añadir los nuevos participantes al grupo de Viaje, quitar los que ya no estan
            //y mandar notificacion
            List<ParseObject> participantsObjectsToDelete = new ArrayList<>(); // los registros de los participantes a eliminar

            String participanteActual="";
            String participantePosterior="";

            //recuperamos los registros a eliminar
            //aquellos que estan en el Grupo en BBDD y no en la lista seleccionada en la App.
            for (int i=0; i< participantesViaje.size();i++){
                participanteActual=participantesViaje.get(i).getString("Id_Usuario");
                participantGroupTrip.add(participanteActual);
                if((!includedFriends.contains(participanteActual)) & (!participanteActual.equals(myUser.getObjectId()))){
                    participantGroupToDelete.add(participanteActual);
                    participantsObjectsToDelete.add(participantesViaje.get(i));
                }
            }

            //recuperamos los objectId de usuario a añadir
            //aquellos que estan en la lista seleccionada en la App y no en el Grupo en BBDD.
            for (int i=0; i< includedFriends.size();i++){
                participantePosterior=includedFriends.get(i);
                if(!participantGroupTrip.contains(participantePosterior)){
                    participantGroupToAdd.add(participantePosterior);
                }
            }

            //añadir al grupo a los usuarios
            for(int i = 0; i < participantGroupToAdd.size(); i++) {
                try {
                    Utils.addGroupFriend(nuevoViaje.getId(), participantGroupToAdd.get(i));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            //eliminar del grupo a los usuarios
            try {
                if(!participantsObjectsToDelete.isEmpty()) {
                    ParseObject.deleteAll(participantsObjectsToDelete);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //Notificacion a todos los participantes del viaje tras actualizar
            try {
                participantesViaje = Utils.getRegistersFromBBDD(myTrip.getId(),"Grupo", "Id_Viaje");
                //notificacion a los componentes de todas las personas añadidas
                for (int i=0;i<participantGroupToAdd.size();i++){
                    for (int j=0;j<participantesViaje.size();j++) {
                        try {
                            Utils.addNotification(participantGroupToAdd.get(i), participantesViaje.get(j).getString("Id_Usuario"), "Grupo", "add", myTrip.getId());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
                //notificacion a los componentes de todas las personas eliminadas
                for (int i=0;i<participantGroupToDelete.size();i++){
                    for (int j=0;j<participantesViaje.size();j++) {
                        try {
                            Utils.addNotification(participantGroupToDelete.get(i), participantesViaje.get(j).getString("Id_Usuario"), "Grupo", "delete", myTrip.getId());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
                //notificacion a las personas eliminadas
                for (int i=0;i<participantGroupToDelete.size();i++){
                    try {
                        Utils.addNotification(participantGroupToDelete.get(i), participantGroupToDelete.get(i), "Grupo", "drop", myTrip.getId());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                image.compress(Bitmap.CompressFormat.PNG, 50, stream);
                byte[] dataImage = stream.toByteArray();
                setFile(new ParseFile("imagenViaje.png", dataImage));
                try {
                    file.save();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        /////////////////INTERFÍCIE////////////////////////////////////
        public void initializeButtons() {

            Button sendDeleteThisTrip = (Button) findViewById(R.id.sendDeleteThisTrip);
            sendDeleteThisTrip.setOnClickListener(clickSendDeleteThisTrip);


            Button gallery = (Button)findViewById(R.id.gallery);
            gallery.setOnClickListener(clickGallery);
            Button google = (Button)findViewById(R.id.google);
            google.setOnClickListener(clickGoogle);

            final ImageButton addFriendButton = (ImageButton)findViewById(R.id.ImageButtonAddFirends);
            addFriendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //quan clickem canviem el color
                    addFriendButton.setImageResource(R.drawable.ic_action_add_group_pulsado);
                    ////////////////////////////POPUP add friend/////////////////////////////////////////////////
                    LayoutInflater layoutInflater
                            = (LayoutInflater)getBaseContext()
                            .getSystemService(LAYOUT_INFLATER_SERVICE);

                    final View popupView = layoutInflater.inflate(R.layout.add_friends_popup, null);

                    final PopupWindow popupWindow = new PopupWindow(
                            popupView,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    popupWindow.setFocusable(true); //per evitar back

                    //PRIMER COP?
                    if(!checkIdList.isEmpty()) //al eliminar el popup, es reseteja la vista
                    {
                        //RECORDEM ELS AMICS ANTERIORS JA CLICATS SI HAVIA
                        setPopUpContent(popupView, false);
                        for (int i = 0; i < checkedBoxes.size(); i++)
                        {
                            CheckBox cb = (CheckBox) popupView.findViewById(checkedBoxes.get(i));
                            cb.setChecked(true);
                        }
                    }
                    else
                    {
                        setPopUpContent(popupView, true); //posem el content del primer cop
                    }
                    ///////////////////////////////////////////////

                    //////////////////////////////BOTÓ OK DEL POPUP////////////////////////////////////////////////////////////////////
                    //afegim els amics marcats al grup
                    Button btnOK = (Button)popupView.findViewById(R.id.okFriends);
                    btnOK.setOnClickListener(new Button.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            addFriendButton.setImageResource(R.drawable.ic_action_add_group);
                            ///////////////PER TOT ELS CHECKS MIREM QUIN ESTÀ CHECKED///////////////
                            for(int i = 0; i < checkIdList.size(); i++)
                            {
                                //  AGAFEM ELS ELEMENTS
                                CheckBox cb = (CheckBox) popupView.findViewById(checkIdList.get(i));
                                String idFriend = friendsIdList.get(i);
                                String mailFriend = friendsMailList.get(i);

                                //////////////////////////////
                                if(cb.isChecked())
                                {
                                    if(!includedFriends.contains(idFriend)) { //NO AMIC REPETIT

                                        //GUARDEM ELS AMICS I BOXES SELECCIONATS LÒGICAMENT
                                        includedFriends.add(idFriend);
                                        checkedBoxes.add(cb.getId());

                                        //MOSTREM EN PANTALLA
                                        LinearLayout addedFriends = (LinearLayout) //linear dels texts views
                                                EditTripForm.this.findViewById(R.id.addedFriendsList);
                                        //addedFriends.removeViewAt(Integer.MAX_VALUE);
                                        TextView newTv = new TextView(getApplicationContext());
                                        newTv.setTextColor(Color.BLACK);
                                        int textViewId = Utils.generateViewId();
                                        newTv.setId(textViewId);
                                        textViewIdList.add(textViewId);
                                        newTv.setText(mailFriend);
                                        addedFriends.addView(newTv);
                                        ////////////////////////
                                    }
                                }
                                else //SI NO ESTÀN CHECKED
                                {
                                    if(includedFriends.contains(idFriend)) //SI ESTAVA INCLÒS
                                    // I EL "DESCHECKEGEM" --> ELIMINEM
                                    {
                                        //VISTA
                                        LinearLayout addedFriends = (LinearLayout) //linear dels texts views
                                                EditTripForm.this.findViewById(R.id.addedFriendsList);
                                        //si l'amic estava inclòs ja tenim el text view

                                        //busquem i eliminem PER EMAIL (la llista de text view no correspon amb la llista de amics
                                        //NOMÉS TEXT VIEW DELS SELECCIONATS!!
                                        boolean found = false;
                                        int iter = 0;
                                        int idRemoveText = 0;
                                        while(!found) {
                                            TextView tv = (TextView) EditTripForm.this.findViewById(textViewIdList.get(iter));

                                            if(tv.getText().equals(friendsMailList.get(i)))
                                            {
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
                    ////////////////////////////////
                    popupWindow.showAsDropDown(addFriendButton, 50, -30); //mostrem el popup

                }
            });

            ImageButton backActivity = (ImageButton)findViewById(R.id.backActvity);
            backActivity.setOnClickListener(doBackActivity);

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

        public void initializeTripData() throws ParseException {
            EditText nombre = (EditText)findViewById(R.id.EditTextNombre);
            nombre.setText(getMyTrip().getNombre());

            EditText fechaInicio = (EditText)findViewById(R.id.EditTextFechaInicio);
            dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
            fechaInicio.setText(dateFormatter.format(getMyTrip().getFechaInicio()));

            EditText fechaFinal = (EditText)findViewById(R.id.EditTextFechaFinal);
            dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
            fechaFinal.setText(dateFormatter.format(getMyTrip().getFechaFinal()));


            LinearLayout addedFriends = (LinearLayout) //linear dels texts views
                    EditTripForm.this.findViewById(R.id.addedFriendsList);
            //addedFriends.removeAllViews();
            TextView newTv = new TextView(getApplicationContext());
            newTv.setTextColor(Color.BLACK);
            int textViewId = Integer.MAX_VALUE;
            newTv.setId(textViewId);
            //textViewIdList.add(textViewId);
            participantesViaje = Utils.getRegistersFromBBDD(myTrip.getId(),"Grupo", "Id_Viaje");
            List<ParseObject> datosUsuario;
            String idUsario;
            String textoAMostrar = "";
            for (int i=0;i<participantesViaje.size();i++){
                idUsario = participantesViaje.get(i).getString("Id_Usuario");
                if(!idUsario.equals(myUser.getObjectId())) {
                    datosUsuario = Utils.getUserFromId(idUsario);
                    //registrosGrupo.add(datosUsuario.get(0));
                    textoAMostrar = textoAMostrar + datosUsuario.get(0).getString("Mail");
                    textoAMostrar = textoAMostrar + "\n";
                }
            }
            newTv.setText(textoAMostrar);
            addedFriends.addView(newTv);

            List <ParseObject> trip;
            trip = Utils.getRegistersFromBBDD(myTrip.getId(), "Viaje", "objectId");
            getMyTrip().setImagen(trip.get(0).getParseFile("Imagen"));

            ParseFile file = getMyTrip().getImagen();
            if (file != null) {
                ImageView imageView = (ImageView) findViewById(R.id.imageTrip);
                byte[] bitmapdata = new byte[0];
                try {
                    bitmapdata = file.getData();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
                imageView.setImageBitmap(bitmap);
            }
        }

    public void setPopUpContent(View popupView, boolean first)
    {
        ////Contingut de la vista DINÀMIC segons els amics///////
        //agafem de la popup view perquè encara no ha estat carregada
        final LinearLayout ll = (LinearLayout) popupView.findViewById(R.id.linearFriends);
        String userId = myUser.getObjectId();
        ///////QUERY AMICS/////////////////////
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
            // AFEGIM ELS ELEMENTS DE LA VISTA I ACTUALITZEM LES LLISTES AMB ELS AMICS (TOTS)/////////////

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
                String friendEmail = userParse.getString("Mail"); //email de l'amic

                ///////////////////////////////CHECKBOXES///////////
                CheckBox cb = new CheckBox(getApplicationContext());
                cb.setText(friendEmail);
                //SI ÉS EL PRIMER COP S'HA DE CREAR ID NOVA, PERÒ SINÓ HEM DE TORNAR A AFEGIR LA ID ANTIGA (ES MANTÉ)
                int checkId = 0;
                if(first) {
                    checkId = Utils.generateViewId();
                    //////////////////////////
                    //INICIALITZEM LLISTES AMB TOTS ELS AMICS I CHECKBOXES


                    LinearLayout addedFriends = (LinearLayout) EditTripForm.this.findViewById(R.id.addedFriendsList);
                    addedFriends.removeAllViews();

                    checkIdList.add(checkId);
                    friendsMailList.add(friendEmail);
                    friendsIdList.add(friendId);
                }
                else
                {
                    checkId = checkIdList.get(i);
                }
                cb.setId(checkId); //li donem una id
                ll.addView(cb); //AFEGIM A LA VISTA
            }

        }else //no tenim amics
        {
            Utils.showInfoAlert(getResources().getString(R.string.noFriendsAlert), EditTripForm.this);
        }
    }
    /**
     * Method ImageButton.OnClickListener doBackActivity
     */
    public ImageButton.OnClickListener doBackActivity = new Button.OnClickListener() {
        public void onClick(View v) {
            Intent i = new Intent(EditTripForm.this, TripList.class);
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
         * CargarViaje. Recuperamos la información de los datos del viaje especificado por el usuario
         */
        public Trip CargarViaje(){
            EditText TextNombre =(EditText)findViewById(R.id.EditTextNombre);
            String nombre = TextNombre.getText().toString();

            EditText TextFechaInicio =(EditText)findViewById(R.id.EditTextFechaInicio);
            String fechaInicio = TextFechaInicio.getText().toString();

            EditText TextFechaFinal =(EditText)findViewById(R.id.EditTextFechaFinal);
            String fechaFinal = TextFechaFinal.getText().toString();

            Date dataInicial = ConvertStringToDate(fechaInicio);
            Date dataFinal = ConvertStringToDate(fechaFinal);

            ParseFile imagen = getFile();
            return new Trip(nombre, myTrip.getPais(), myTrip.getCiudad(), dataInicial, dataFinal, imagen);
        }

        /**
         * Method EditarViajeBDD
         * @param nuevoViaje
         * @return success
         * @throws ParseException
         */
        public boolean EditarViajeBDD(Trip nuevoViaje) throws ParseException{
            boolean success = false;
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("name", nuevoViaje.getNombre());
            params.put("idCiudad", nuevoViaje.getCiudad());
            params.put("fechaInicial", nuevoViaje.getFechaInicio());
            params.put("fechaFinal", nuevoViaje.getFechaFinal());
            params.put("imagen", nuevoViaje.getImagen());
            params.put("objectId", nuevoViaje.getId());

            String editTripResponse = ParseCloud.callFunction("updateTripData", params);
            if(!editTripResponse.isEmpty())
                //CrearGrupoBDD(nuevoViaje);
                success = true;
            Log.i("Add editTrip:", editTripResponse);

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

            }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
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

    public Button.OnClickListener clickSendDeleteThisTrip = new Button.OnClickListener() {
        public void onClick(View v) {
            String msn="";
            List<ParseObject> sitiosAEliminar;
            List<ParseObject> puntuacionesAEliminar = null;
            List<ParseObject> componentesDelViaje;
            Boolean cambiarAdministrador = false;

            try {
                componentesDelViaje = Utils.getRegistersFromBBDD(myTrip.getId(), "Grupo", "Id_Viaje");
                if (!componentesDelViaje.isEmpty()) {
                    switch (componentesDelViaje.size()) {
                        case 0:
                            //se elimina viaje, sitios y puntuaciones, con datos consistentes
                            // no deberia de entrar nunca aqui
                            sitiosAEliminar = Utils.getRegistersFromBBDD(myTrip.getId(), "Sitio", "Id_Viaje");
                            puntuacionesAEliminar = Utils.getRegistersFromBBDD(myTrip.getId(), "Puntuacion", "Id_Viaje");

                            if (!sitiosAEliminar.isEmpty()) {
                                ParseObject.deleteAll(sitiosAEliminar);
                            }
                            if (!puntuacionesAEliminar.isEmpty()) {
                                ParseObject.deleteAll(puntuacionesAEliminar);
                            }
                            ParseObject.createWithoutData("Viaje", myTrip.getId()).deleteEventually();

                            msn = "Deleted Trip " + myTrip.getNombre();
                            break;
                        case 1:
                            //se elimina el grupo, el viaje, los sitios y las puntuaciones
                            sitiosAEliminar = Utils.getRegistersFromBBDD(myTrip.getId(), "Sitio", "Id_Viaje");
                            for (int i = 0; i < sitiosAEliminar.size(); i++) {
                                puntuacionesAEliminar = Utils.getRegistersFromBBDD(sitiosAEliminar.get(i).getObjectId(), "Puntuacion", "Id_Sitio");
                                if (!puntuacionesAEliminar.isEmpty()) {
                                    ParseObject.deleteAll(puntuacionesAEliminar);
                                }
                            }
                            if (!sitiosAEliminar.isEmpty()){
                                ParseObject.deleteAll(sitiosAEliminar);
                            }
                            if(!componentesDelViaje.isEmpty()){
                                ParseObject.deleteAll(componentesDelViaje);
                            }

                            ParseObject.createWithoutData("Viaje", myTrip.getId()).deleteEventually();

                            msn = "Deleted Trip " + myTrip.getNombre();
                            break;
                        default:
                            //Se elimina el usuario del grupo y se asigna un nuevo administrador.
                            for(int i=0;i<componentesDelViaje.size();i++) {
                                ParseObject componente = componentesDelViaje.get(i);
                                if (componente.getString("Id_Usuario").equals(myUser.getObjectId())){
                                    if(componente.getBoolean("Administrador")){
                                        cambiarAdministrador = true;
                                    }
                                    //ParseObject.createWithoutData("Grupo", componente.getObjectId()).deleteEventually();
                                    ParseObject.createWithoutData("Grupo", componente.getObjectId()).delete();
                                }
                            }
                            if(cambiarAdministrador) {
                                componentesDelViaje = Utils.getRegistersFromBBDD(myTrip.getId(), "Grupo", "Id_Viaje");
                                Utils.setValueBBDD(true, "Grupo", "Administrador", componentesDelViaje.get(0).getObjectId());
                            }

                            msn = "Deleted Trip " + myTrip.getNombre();
                            break;
                    }
                }else {
                    //se elimina el viaje, ¿los sitios y las puntuaciones?
                    ParseObject.createWithoutData("Viaje", myTrip.getId()).deleteEventually();
                    /*
                    sitiosAEliminar = Utils.getRegistersFromBBDD(myTrip.getId(), "Sitio", "Id_Viaje");
                    puntuacionesAEliminar = Utils.getRegistersFromBBDD(myTrip.getId(), "Puntuacion", "Id_Viaje");

                    if(!sitiosAEliminar..isEmpty()){
                        ParseObject.deleteAll(sitiosAEliminar);
                    }
                    id(!puntuacionesAEliminar.isEmpty()){
                        ParseObject.deleteAll(puntuacionesAEliminar);
                    }
                    */
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

            Toast.makeText(getApplicationContext(), msn , Toast.LENGTH_SHORT).show();
            Intent tripList = new Intent(EditTripForm.this, TripList.class);
            tripList.putExtra("myUser", myUser);
            startActivity(tripList);

        }
    };
}