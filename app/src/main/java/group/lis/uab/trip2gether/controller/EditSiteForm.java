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
import group.lis.uab.trip2gether.Resources.Utils;
import group.lis.uab.trip2gether.model.User;

public class EditSiteForm extends ActionBarActivity {

    private static final int LOAD_IMAGE = 1;
    private ParseFile file;

    private String mySiteId;
    private User myUser;
    public User getMyUser() {
        return myUser;
    }
    public void setMyUser (User myUser) {
        this.myUser = myUser;
    }

    public ParseFile getFile() {
        return file;
    }
    public void setFile(ParseFile file) {
        this.file = file;
    }

    private String idViaje="";
    private String nombreViaje="";
    public void setIdViaje(String idViaje) { this.idViaje = idViaje; }
    public String getIdViaje() { return idViaje; }

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_site_form);

        mToolbar = (Toolbar) findViewById(R.id.action_bar_edit_site);
        setSupportActionBar(mToolbar);

        Intent intent = this.getIntent();
        this.mySiteId = intent.getStringExtra("mySiteId"); //serialització de l'objecte
        setMyUser((User) intent.getSerializableExtra("myUser")); //serialització de l'objecte
        nombreViaje = intent.getStringExtra("nombre_viaje");
        setIdViaje(intent.getExtras().getString("id_viaje"));

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
            nuevoSitio.setId(mySiteId);
            nuevoSitio.setIdViaje(idViaje);
            if (nuevoSitio.getNombre().equalsIgnoreCase("")){
                Toast.makeText(EditSiteForm.this, "Nombre obligatorio", Toast.LENGTH_SHORT).show();
            }
            else {
                Intent intent = new Intent(EditSiteForm.this, SiteView.class);
                intent.putExtra("currentSiteId", nuevoSitio.getId());
                intent.putExtra("myUser", myUser);
                intent.putExtra("id_viaje", idViaje);
                intent.putExtra("nombre_viaje", nombreViaje);

                try {
                    EditarSitioBDD(nuevoSitio);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                startActivity(intent);

                return true;
            }
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

            //Try to reduce the necessary memory
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inSampleSize = 2;

            // String picturePath contains the path of selected Image
            ImageView imageView = (ImageView) findViewById(R.id.imageSite);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath, options));

            //file it's a ParseFile that contains the image selected
            Bitmap image = BitmapFactory.decodeFile(picturePath, options);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 50, stream);
            byte[] dataImage = stream.toByteArray();
            setFile(new ParseFile("imagenViaje.JPEG", dataImage));
            try {
                file.save();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    /////////////////INTERFÍCIE////////////////////////////////////
    public void initializeButtons() {
        Button sendDeleteThisSite = (Button) findViewById(R.id.sendDeleteThisSite);
        sendDeleteThisSite.setOnClickListener(clickSendDeleteThisSite);

        Button gallery = (Button)findViewById(R.id.gallery);
        gallery.setOnClickListener(clickGallery);
        Button google = (Button)findViewById(R.id.google);
        google.setOnClickListener(clickGoogle);

        ImageButton backActivity = (ImageButton)findViewById(R.id.backActvity);
        backActivity.setOnClickListener(doBackActivity);
    }


    public void initializeSiteData() {
        ParseObject site = null;
        try {
            site = Utils.getRegistersFromBBDD(mySiteId, "Sitio", "objectId").get(0);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        EditText name = (EditText)findViewById(R.id.EditTextSiteNombre);
        name.setText(site.getString("Nombre"));

        EditText descripcion = (EditText)findViewById(R.id.EditTextSiteDescripcion);
        descripcion.setText(site.getString("Descripcion"));

        EditText duracion = (EditText)findViewById(R.id.EditTextSiteDuracion);
        Double duracionDouble = site.getDouble("Duracion");
        String duracionString = new Double(duracionDouble).toString();
        duracion.setText(duracionString);


        EditText precio = (EditText)findViewById(R.id.EditTextSitePrecio);
        Double precioDouble = site.getDouble("Precio");
        String precioString =  new Double(precioDouble).toString();
        precio.setText(precioString);

        ParseFile file = site.getParseFile("Imagen");
        if (file != null) {
            ImageView imageView = (ImageView) findViewById(R.id.imageSite);
            byte[] bitmapdata = new byte[0];
            try {
                bitmapdata = file.getData();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //Try to reduce the necessary memory
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inSampleSize = 2;

            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length, options);
            imageView.setImageBitmap(bitmap);
        }
    }

    /**
     * Method ImageButton.OnClickListener doBackActivity
     */
    public ImageButton.OnClickListener doBackActivity = new Button.OnClickListener() {
        public void onClick(View v) {
            Intent i = new Intent(EditSiteForm.this, SiteView.class);
            i.putExtra("currentSiteId", mySiteId);
            i.putExtra("id_viaje", idViaje);
            i.putExtra("myUser", myUser);
            i.putExtra("nombre_viaje", nombreViaje);
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
     * Method CargarViaje. Recuperamos la información de los datos del viaje especificado por el usuario
     * @return Site
     */
    public Site CargarSitio(){
        EditText TextNombre =(EditText)findViewById(R.id.EditTextSiteNombre);
        String nombre = TextNombre.getText().toString();

        EditText TextDescripcion =(EditText)findViewById(R.id.EditTextSiteDescripcion);
        String descripcion = TextDescripcion.getText().toString();

        double duracion;
        EditText TextDuracion =(EditText)findViewById(R.id.EditTextSiteDuracion);
        String duracionString = TextDuracion.getText().toString();
        if (duracionString.equals("")){
            duracion = 0.0;
        }
        else{
            duracion = Double.parseDouble(duracionString);
        }

        double precio;
        EditText TextPrecio =(EditText)findViewById(R.id.EditTextSitePrecio);
        String precioString = TextPrecio.getText().toString();
        if (precioString.equals("")){
            precio = 0.0;
        }
        else {
            precio = Double.parseDouble(precioString);
        }

        String idViaje = getIdViaje();
        String objectId = mySiteId;
        ParseFile imagen = getFile();

        ParseObject site = null;
        try {
            site = Utils.getRegistersFromBBDD(mySiteId, "Sitio", "objectId").get(0);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        double latitud = site.getDouble("Latitud");
        double longitud = site.getDouble("Longitud");

        return new Site(nombre, descripcion, imagen, idViaje, objectId, duracion, precio, latitud, longitud);
    }

    /**
     * Method EditarViajeBDD
     * @param nuevoSitio
     * @return success
     * @throws ParseException
     */
    public boolean EditarSitioBDD(Site nuevoSitio) throws ParseException{
        nuevoSitio.getIdViaje();
        boolean success = false;
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("nombre", nuevoSitio.getNombre());
        params.put("idViaje", nuevoSitio.getIdViaje());
        params.put("descripcion", nuevoSitio.getDescripcion());
        params.put("duracion", nuevoSitio.getDuracion());
        params.put("precio", nuevoSitio.getPrecio());
        params.put("imagen", nuevoSitio.getImagen());
        params.put("objectId", nuevoSitio.getId());

        String editSiteResponse = ParseCloud.callFunction("updateSiteData", params);
        if(!editSiteResponse.isEmpty())
            success = true;
        Log.i("Add editSite:", editSiteResponse);

        return success;
    }

    public Button.OnClickListener clickSendDeleteThisSite = new Button.OnClickListener() {
        public void onClick(View v) {
            String msn="";
            try {
                List<ParseObject> participantes = Utils.getRegistersFromBBDD(idViaje,"Grupo", "Id_Viaje");
                ParseObject sitio = Utils.getRegistersFromBBDD(mySiteId,"Sitio", "objectId").get(0);
                for (int i=0;i<participantes.size();i++) {
                    if (participantes.get(i).getBoolean("Administrador")) {
                        if (myUser.getObjectId().equals(participantes.get(i).getString("Id_Usuario"))) {
                            ParseObject.createWithoutData("Sitio", mySiteId).deleteEventually();
                            //TODO eliminar también las puntuaciones.


                            msn = "Deleted Site " + mySiteId;
                        } else {
                            msn = "Solo el administrador puede eliminar el sitio" + sitio.getString("Nombre");
                        }
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }


            Toast.makeText(getApplicationContext(), msn, Toast.LENGTH_SHORT).show();
            Intent siteList = new Intent(EditSiteForm.this, SiteList.class);
            siteList.putExtra("id_viaje", idViaje);
            siteList.putExtra("myUser", myUser);
            siteList.putExtra("nombre_viaje", nombreViaje);
            startActivity(siteList);
        }
    };
}
