package group.lis.uab.trip2gether.controller;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;

import group.lis.uab.trip2gether.R;
import group.lis.uab.trip2gether.model.Site;

/**
 * Created by Mireia on 21/04/2015.
 */
public class EditSiteForm extends ActionBarActivity {

    private static final int LOAD_IMAGE = 1;
    private ParseFile file;

    private Site mySite;
    public Site getMySite() {
        return mySite;
    }
    public void setMySite (Site mySite) {
        this.mySite = mySite;
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
        setContentView(R.layout.activity_edit_site_form);

        //mToolbar = (Toolbar) findViewById(R.id.action_bar_edit_site);
        //setSupportActionBar(mToolbar);

        Intent intent = this.getIntent();
        setMySite((Site) intent.getSerializableExtra("mySite")); //serialització de l'objecte
        mySite.getId();

        this.initializeButtons();
        this.initializeSiteData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_site_form, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.saveEditSite) {
            Site nuevoSitio = CargarSitio();
            nuevoSitio.setId(mySite.getId());
            Intent intent = new Intent (EditSiteForm.this, SiteList.class);
            intent.putExtra("mySite", mySite);

            try {
                String idViaje = getIdViaje(nuevoSitio);
                nuevoSitio.setIdViaje(idViaje);
                EditarSitioBDD(nuevoSitio);


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

            ImageView imageView = (ImageView) findViewById(R.id.imageSite);
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

    /////////////////INTERFÍCIE////////////////////////////////////
    public void initializeButtons() {

        //Button sendDeleteThisSite = (Button) findViewById(R.id.sendDeleteThisSite);
        //sendDeleteThisSite.setOnClickListener(clickSendDeleteThisSite);

        Button gallery = (Button)findViewById(R.id.gallery);
        gallery.setOnClickListener(clickGallery);
        Button google = (Button)findViewById(R.id.google);
        google.setOnClickListener(clickGoogle);

        ImageButton backActivity = (ImageButton)findViewById(R.id.backActvity);
        backActivity.setOnClickListener(doBackActivity);
    }
    public void initializeSiteData() {
        EditText name = (EditText)findViewById(R.id.EditTextSiteNombre);
        name.setText(getMySite().getNombre());

        EditText descripcion = (EditText)findViewById(R.id.EditTextSiteDescripcion);
        descripcion.setText(getMySite().getDescripcion());

        EditText duracion = (EditText)findViewById(R.id.EditTextSiteDuracion);
        Double duracionDouble = getMySite().getDuracion();
        String duracionString = new Double(duracionDouble).toString();
        duracion.setText(duracionString);


        EditText precio = (EditText)findViewById(R.id.EditTextSitePrecio);
        Double precioDouble = getMySite().getPrecio();
        String precioString =  new Double(precioDouble).toString();
        precio.setText(precioString);

        try {
            List<ParseObject> site;
            site = getValueBBDD(mySite.getId(), "Sitio", "objectId");
            getMySite().setImagen(site.get(0).getParseFile("Imagen"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ParseFile file = getMySite().getImagen();
        if (file != null) {
            ImageView imageView = (ImageView) findViewById(R.id.imageSite);
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

    /**
     * Method ImageButton.OnClickListener doBackActivity
     */
    public ImageButton.OnClickListener doBackActivity = new Button.OnClickListener() {
        public void onClick(View v) {
            Intent i = new Intent(EditSiteForm.this, SiteView.class);
            i.putExtra("currentSite", mySite); //NUSE

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

            EditText TextNombre =(EditText)findViewById(R.id.EditTextSiteNombre);
            String search = TextNombre.getText().toString();
            Uri uri = Uri.parse("https://www.google.com/search?hl=en&site=imghp&tbm=isch&source=hp&q="+search);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    };

    /**
     * CargarViaje. Recuperamos la información de los datos del viaje especificado por el usuario
     */
    public Site CargarSitio(){
        EditText TextNombre =(EditText)findViewById(R.id.EditTextSiteNombre);
        String nombre = TextNombre.getText().toString();

        EditText TextDescripcion =(EditText)findViewById(R.id.EditTextSiteDescripcion);
        String descripcion = TextDescripcion.getText().toString();

        EditText TextDuracion =(EditText)findViewById(R.id.EditTextSiteDuracion);
        String duracionString = TextDuracion.getText().toString();
        int duracion = Integer.parseInt(duracionString);

        EditText TextPrecio =(EditText)findViewById(R.id.EditTextSitePrecio);
        String precioString = TextPrecio.getText().toString();
        int precio = Integer.parseInt(precioString);

        //String idViaje = mySite.getIdViaje();

        ParseFile imagen = getFile();
        double latitud = mySite.getLatitud();
        double longitud = mySite.getLongitud();

        return new Site(nombre, descripcion,imagen, mySite.getIdViaje(),"", duracion, precio, latitud, longitud);
    }

    /**
     * Method EditarViajeBDD
     * @param nuevoSitio
     * @return success
     * @throws ParseException
     */
    public boolean EditarSitioBDD(Site nuevoSitio) throws ParseException{
        boolean success = false;
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("name", nuevoSitio.getNombre());
        params.put("descripcion", nuevoSitio.getDescripcion());
        params.put("idViaje", nuevoSitio.getIdViaje());
        params.put("precio", nuevoSitio.getPrecio());
        params.put("duracion", nuevoSitio.getDuracion());
        params.put("imagen", nuevoSitio.getImagen());
        params.put("objectId", nuevoSitio.getId());

        String editSiteResponse = ParseCloud.callFunction("updateSiteData", params);
        if(!editSiteResponse.isEmpty())
            //CrearGrupoBDD(nuevoSitio);
            success = true;
        Log.i("Add editSite:", editSiteResponse);

        return success;
    }

    /**
     * Method getIdViaje
     * @param nuevoSitio
     * @return idViaje
     * @throws ParseException
     */
    public String getIdViaje(Site nuevoSitio) throws ParseException{
        boolean success = false;
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("idViaje", nuevoSitio.getIdViaje());
        String idViaje = ParseCloud.callFunction("getIdViaje", params);
        return idViaje;
    }

    public Button.OnClickListener clickSendDeleteThisSite = new Button.OnClickListener() {
        public void onClick(View v) {
            String msn="";
            ParseObject sitioAEliminar = null;
            mySite.getId();

            try {
                sitioAEliminar = (ParseObject) getValueBBDD(mySite.getId(), "Sitio", "objectId");
                sitioAEliminar.deleteInBackground();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            msn = "Deleted Site " + mySite.getNombre();

            Toast.makeText(getApplicationContext(), msn, Toast.LENGTH_SHORT).show();
            Intent siteList = new Intent(EditSiteForm.this, SiteList.class);
            siteList.putExtra("mySite", mySite); //NUSE
            startActivity(siteList);

        }
    };

    /**
     * Method getIdsBBDD. Métode genéric per obtenir una llista de valors d'un camp que pertany a una entitat de la BBDD
     * @param valueFieldTable Valor del <field> de la <table>
     * @param table Taula de la BBDD
     * @param field Camp de la <table>
     * @return List<ParseObject>
     * @throws com.parse.ParseException
     */
    public List<ParseObject> getValueBBDD(String valueFieldTable, String table, String field) throws com.parse.ParseException {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("valueFieldTable", valueFieldTable);
        params.put("table", table);
        params.put("field", field);
        return ParseCloud.callFunction("getId", params);
    }

    /**
     * Method setValueBBDD. Métode genéric per actualitzar un camp amb un objectId donat.
     * @param newValueBoolean Valor a posar al <field> de la <table>
     * @param table Taula de la BBDD
     * @param field Camp de la <table> a actualizar.
     * @param objectId del registre a actualitzar.
     * @throws com.parse.ParseException
     */
    public void setValueBBDD(Boolean newValueBoolean, String table, String field, String objectId) throws com.parse.ParseException {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("newValue", newValueBoolean);
        params.put("table", table);
        params.put("field", field);
        params.put("objectId", objectId);
        ParseCloud.callFunction("setValueOfTableId", params);
    }

}
