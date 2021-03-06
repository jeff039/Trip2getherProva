package group.lis.uab.trip2gether.controller;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import group.lis.uab.trip2gether.R;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import group.lis.uab.trip2gether.model.Site;
import group.lis.uab.trip2gether.model.User;

public class NewSiteForm extends ActionBarActivity {
    private static final int LOAD_IMAGE = 1; //static final -> no canviarà (s'ha d'inicializar)
    private static Intent intentR = null;
    private User myUser;
    private ParseFile file;
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
    private ListView leftDrawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_site_form);
        intentR = this.getIntent();

        this.initializeButtons();

        mToolbar = (Toolbar) findViewById(R.id.action_bar_new_site);
        setSupportActionBar(mToolbar);

        myUser = (User) intentR.getSerializableExtra("myUser");
        nombreViaje = intentR.getExtras().getString("nombre_viaje");
        setIdViaje(intentR.getExtras().getString("tripId"));
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
            Bitmap image = BitmapFactory.decodeFile(picturePath, options);
            imageView.setImageBitmap(image);

            //file it's a ParseFile that contains the image selected
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 75, stream);
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
     * Method initializeButtons
     */
    private void initializeButtons() {
        Button gallery = (Button)findViewById(R.id.gallery);
        gallery.setOnClickListener(clickGallery);
        Button google = (Button)findViewById(R.id.google);
        google.setOnClickListener(clickGoogle);
        ImageButton backActvity = (ImageButton) findViewById(R.id.backActvity);
        backActvity.setOnClickListener(doBackActivity);
    }

    public Button.OnClickListener clickGallery = new Button.OnClickListener() {
        public void onClick(View v) {
        Intent i = new Intent(
            Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, LOAD_IMAGE);
        }
    };

    public Button.OnClickListener clickGoogle = new Button.OnClickListener() {
        public void onClick(View v) {
            EditText TextNombre =(EditText)findViewById(R.id.Nombre);
            String search = TextNombre.getText().toString();
            Uri uri = Uri.parse("https://www.google.com/search?hl=en&site=imghp&tbm=isch&source=hp&q="+search);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    };

    /**
     * Method ImageButton.OnClickListener doBackActivity
     */
    public ImageButton.OnClickListener doBackActivity = new Button.OnClickListener() {
        public void onClick(View v) {
            Intent i = new Intent(NewSiteForm.this, SiteList.class);
            i.putExtra("myUser", myUser);
            i.putExtra("id_viaje", idViaje);
            i.putExtra("nombre_viaje", nombreViaje);
            startActivity(i);
        }
    };

    /**
     * Method onCreateOptionsMenu
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_site_form, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.saveSite:
                Site nuevoSitio = CargarSitio();
                //double testDuracion = nuevoSitio.getDuracion();
                if (nuevoSitio.getNombre().equalsIgnoreCase("")){
                    Toast.makeText(NewSiteForm.this, getResources().getString(R.string.nameRequired), Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(NewSiteForm.this, SiteList.class);
                    intent.putExtra("nombre", nuevoSitio.getNombre());
                    intent.putExtra("descripcion", nuevoSitio.getDescripcion());
                    intent.putExtra("duracion", nuevoSitio.getDuracion());
                    intent.putExtra("precio", nuevoSitio.getPrecio());
                    intent.putExtra("id_viaje", nuevoSitio.getIdViaje());
                    intent.putExtra("latitud", nuevoSitio.getLatitud());
                    intent.putExtra("longitud", nuevoSitio.getLongitud());
                    intent.putExtra("myUser", myUser);

                    intent.putExtra("id_viaje", idViaje);
                    intent.putExtra("nombre_viaje", nombreViaje);

                    try {
                        GuardarSitioBDD(nuevoSitio);
                        Toast.makeText(NewSiteForm.this, getResources().getString(R.string.addSiteCorrectly), Toast.LENGTH_SHORT).show();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    startActivity(intent);

                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Method CargarSitio
     * @return
     */
    public Site CargarSitio(){
        EditText TextNombre =(EditText)findViewById(R.id.Nombre);
        String nombre = TextNombre.getText().toString();

        EditText TextDescripcion =(EditText)findViewById(R.id.Descripcion);
        String descripcion = TextDescripcion.getText().toString();

        double duracion;
        EditText TextDuracion =(EditText)findViewById(R.id.Duracion);
        String duracionString = TextDuracion.getText().toString();
        if (duracionString.equals("")){
            duracion = 0.0;
        }
        else{
            duracion = Integer.parseInt(duracionString);
        }

        double precio;
        EditText TextPrecio =(EditText)findViewById(R.id.Precio);
        String precioString = TextPrecio.getText().toString();
        if (precioString.equals("")){
            precio = 0.0;
        }
        else {
            precio = Integer.parseInt(precioString);
        }
        ParseFile imagen = getFile();
        Double latitud = intentR.getExtras().getDouble("latitude");
        Double longitud =intentR.getExtras().getDouble("longitude");
        return new Site(nombre, descripcion, imagen, getIdViaje(), "", duracion, precio, latitud, longitud);
    }

    /**
     * Method GuardarSitioBDD
     * @param nuevoSitio
     * @return success
     * @throws ParseException
     */
    public boolean GuardarSitioBDD(Site nuevoSitio) throws ParseException{
        boolean success = false;
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("name", nuevoSitio.getNombre());
        params.put("description", nuevoSitio.getDescripcion());
        params.put("duracion", nuevoSitio.getDuracion());
        params.put("precio", nuevoSitio.getPrecio());
        params.put("idViaje", nuevoSitio.getIdViaje());
        params.put("imagen", nuevoSitio.getImagen());
        params.put("latitud", nuevoSitio.getLatitud());
        params.put("longitud", nuevoSitio.getLongitud());

        String addSiteResponse = ParseCloud.callFunction("addSite", params);
        if(!addSiteResponse.isEmpty())
            nuevoSitio.setId(addSiteResponse);
        success = true;
        Log.i("Add newSite:", addSiteResponse);
        return success;
    }
}