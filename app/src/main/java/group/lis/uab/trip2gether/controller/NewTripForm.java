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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import group.lis.uab.trip2gether.R;

public class NewTripForm extends ActionBarActivity {

    private static final int LOAD_IMAGE = 1; //static final -> no canviarà (s'ha d'inicializar)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trip_form);

        //Mostrem la Action Bar en la activity
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(75, 74, 104)));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_action_cancel);
        getSupportActionBar().setTitle("      Nuevo Viaje");

        this.initializeButtons();
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
     * Interficie
     */
    private void initializeButtons()
    {
        Button gallery = (Button)findViewById(R.id.gallery);
        gallery.setOnClickListener(clickGallery);
        Button google = (Button)findViewById(R.id.google);
        google.setOnClickListener(clickGoogle);
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
            String search = "fish";
            Uri uri = Uri.parse("https://www.google.com/search?hl=en&site=imghp&tbm=isch&source=hp&q="+search);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    };

    /**
     * Action Bar
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_trip_form, menu);
        return true;
    }

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

        return new Trip(nombre, pais, ciudad, fechaInicio, fechaFinal);
    }
}
