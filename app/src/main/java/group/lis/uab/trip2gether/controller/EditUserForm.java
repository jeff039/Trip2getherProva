package group.lis.uab.trip2gether.controller;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import group.lis.uab.trip2gether.R;
import group.lis.uab.trip2gether.Resources.Utils;
import group.lis.uab.trip2gether.model.User;
import android.support.v7.widget.Toolbar;

public class EditUserForm extends ActionBarActivity {

    private static final int LOAD_IMAGE = 1;
    private ParseFile file;
    //UI References
    private EditText pickDate;
    private DatePickerDialog pickDateDialog;
    private SimpleDateFormat dateFormatter;
    @SuppressWarnings("FieldCanBeLocal")
    private Toolbar mToolbar;

    User myUser;

    public ParseFile getFile() {
        return file;
    }
    public void setFile(ParseFile file) {
        this.file = file;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_form);
        this.initializeButtons();
        this.setDateTimeField();

        mToolbar = (Toolbar) findViewById(R.id.action_bar_edit_user);
        setSupportActionBar(mToolbar);

        Intent intent = getIntent();
        myUser = (User) intent.getSerializableExtra("myUser");
        this.initializeUserData();
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
            ImageView imageView = (ImageView) findViewById(R.id.imageUser);
            Bitmap image = BitmapFactory.decodeFile(picturePath, options);
            imageView.setImageBitmap(image);

            //file it's a ParseFile that contains the image selected
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 75, stream);
            byte[] dataImage = stream.toByteArray();
            setFile(new ParseFile(myUser.getObjectId()+".JPEG", dataImage));
            try {
                file.save();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void initializeButtons() {
        Button gallery = (Button)findViewById(R.id.gallery);
        gallery.setOnClickListener(clickGallery);
        Button google = (Button)findViewById(R.id.google);
        google.setOnClickListener(clickGoogle);
        Button editPassword = (Button)findViewById(R.id.editPassword);
        editPassword.setOnClickListener(clickEditPassword);
        Button sendUpdateUserProfile = (Button) findViewById(R.id.sendUpdateUserProfile);
        sendUpdateUserProfile.setOnClickListener(clickSendUpdateUserProfile);
        pickDate = (EditText) findViewById(R.id.date_of_birth);
        pickDate.setOnClickListener(clickPickDate);
        pickDate.setInputType(InputType.TYPE_NULL);
        pickDate.setOnFocusChangeListener(focusPickDate);
    }

    public void initializeUserData() {
        EditText name = (EditText)findViewById(R.id.name);
        name.setText(myUser.getName());
        EditText surname = (EditText)findViewById(R.id.surname);
        surname.setText(myUser.getSurname());
        EditText city = (EditText)findViewById(R.id.city);
        city.setText(myUser.getCity());
        EditText country = (EditText)findViewById(R.id.country);
        country.setText(myUser.getCountry());
        EditText dateOfBirth = (EditText)findViewById(R.id.date_of_birth);
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        dateOfBirth.setText(dateFormatter.format(myUser.getDateOfBirth()));
        setFile(null);
        try
        {
            setFile(Utils.getRegistersFromBBDD(myUser.getObjectId(), "Usuario", "objectId").get(0).getParseFile("Imagen"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (getFile() != null) {
            ImageView imageView = (ImageView) findViewById(R.id.imageUser);
            Utils.setImageViewWithParseFile(imageView, file, false);
        }
    }

    public Button.OnClickListener clickSendUpdateUserProfile = new Button.OnClickListener() {
        public void onClick(View v) {
        try {
            if (EditUserForm.this.getName().equalsIgnoreCase("")
                    || EditUserForm.this.getSurname().equalsIgnoreCase("")
                    || EditUserForm.this.getCity().equalsIgnoreCase("")
                    || EditUserForm.this.getDateOfBirth()==null
                    || EditUserForm.this.getCountry().equalsIgnoreCase("")) {
                Toast.makeText(EditUserForm.this, "All Fields Required.", Toast.LENGTH_SHORT).show();
            }
            else {
                User myUpdatedUser = EditUserForm.this.updateUserData(EditUserForm.this.getName(),
                        EditUserForm.this.getSurname(), EditUserForm.this.getCity(),
                        myUser.getMail(), myUser.getPassword(),
                        EditUserForm.this.getCountry(), EditUserForm.this.getDateOfBirth(),
                        myUser.getObjectId());

                if (myUpdatedUser != null) {
                    Toast.makeText(getApplicationContext(), "Updated user profile", Toast.LENGTH_SHORT).show();
                    Intent userProfile = new Intent();
                    userProfile.putExtra("myUser", myUser);
                    setResult(Activity.RESULT_OK, userProfile);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Error updating the user profile", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
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
        EditText TextNombre =(EditText)findViewById(R.id.name);
        String search = TextNombre.getText().toString();
        Uri uri = Uri.parse("https://www.google.com/search?hl=en&site=imghp&tbm=isch&source=hp&q="+search);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
        }
    };

    public Button.OnClickListener clickEditPassword = new Button.OnClickListener() {
        public void onClick(View v) {
        Intent editPasswordForm = new Intent(EditUserForm.this, EditPasswordForm.class);
        editPasswordForm.putExtra("myUser", myUser);
        startActivityForResult(editPasswordForm, 0);
        }
    };

    public EditText.OnClickListener clickPickDate = new EditText.OnClickListener() {
        @Override
        public void onClick(View view) {
            pickDateDialog.show();
        }
    };

    public EditText.OnFocusChangeListener focusPickDate = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus)
                pickDateDialog.show();
            v.clearFocus();
        }
    };

    private void setDateTimeField() {
        Calendar newCalendar = Calendar.getInstance();
        pickDateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                pickDate.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }

    /**
     * Method updateUserData
     * @param name
     * @param surname
     * @param city
     * @param mail
     * @param password
     * @param country
     * @param date_of_birth
     * @param objectId
     * @return
     * @throws ParseException
     */
    public User updateUserData(String name, String surname, String city, String mail, String password,
                                  String country, Date date_of_birth, String objectId) throws ParseException {
        //Encrypt encrypt = new Encrypt(getApplicationContext());
        boolean success = false;
        //password = encrypt.encryptPassword(password);
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("name", name);
        params.put("surname", surname);
        params.put("city", city);
        params.put("mail", mail);
        params.put("password", password);
        params.put("country", country);
        params.put("date_of_birth", date_of_birth);
        params.put("Imagen", getFile());
        params.put("objectId", objectId);

        //Comprovem si el mail existeix a la BD
        if (!mail.equals(myUser.getMail())) {
            ArrayList checkRegistro = ParseCloud.callFunction("checkUserSignIn", params);
            if (checkRegistro.size() == 1) {//mail existente
                Log.e("Jofré", "alerta");
                return null;
            }
        }
        //Creem el nou usuari a la BD
        Log.e("Jofré", mail);
        ParseObject userParse = ParseCloud.callFunction("updateUserData", params); //crida al BE
        if(userParse.getObjectId() != null) { //S'ha creat l'usuari
            myUser = new User(userParse.getString("Mail"), userParse.getString("Password"),
                    userParse.getString("Nombre"), userParse.getString("Apellidos"),
                    userParse.getString("Pais"), userParse.getString("Ciudad"),
                    userParse.getDate("Fecha_Nacimiento"), userParse.getObjectId());
        }
        return myUser;
    }

    public String getName() {
        EditText name = (EditText) findViewById(R.id.name);
        String nameText = name.getText().toString();
        return nameText;
    }

    public String getSurname() {
        EditText surname = (EditText) findViewById(R.id.surname);
        String surnameText = surname.getText().toString();
        return surnameText;
    }

    public String getCity() {
        EditText city = (EditText) findViewById(R.id.city);
        String cityText = city.getText().toString();
        return cityText;
    }

    public Date getDateOfBirth() throws java.text.ParseException {
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        EditText dateOfBirth = (EditText) findViewById(R.id.date_of_birth);
        Date dateOfBirthDate;
        dateOfBirthDate = dateFormatter.parse(dateOfBirth.getText().toString());
        dateOfBirthDate.setHours(13);
        return dateOfBirthDate;
    }

    public String getCountry() {
        EditText country = (EditText) findViewById(R.id.country);
        String countryText = country.getText().toString();
        return countryText;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_launch_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}