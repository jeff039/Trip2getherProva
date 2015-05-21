package group.lis.uab.trip2gether.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.parse.ParseException;
import group.lis.uab.trip2gether.R;
import group.lis.uab.trip2gether.Resources.Encrypt;
import group.lis.uab.trip2gether.Resources.Utils;
import group.lis.uab.trip2gether.model.User;

public class EditPasswordForm extends ActionBarActivity {

    @SuppressWarnings("FieldCanBeLocal")
    private Toolbar mToolbar;
    User myUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password_form);
        this.initializeButtons();

        mToolbar = (Toolbar) findViewById(R.id.action_bar_edit_password);
        setSupportActionBar(mToolbar);

        Intent intent = getIntent();
        myUser = (User) intent.getSerializableExtra("myUser");
    }

    public void initializeButtons() {
        Button updatePassword = (Button)findViewById(R.id.sendUpdatePassword);
        updatePassword.setOnClickListener(clickSendUpdatePassword);
    }

    public Button.OnClickListener clickSendUpdatePassword = new Button.OnClickListener() {
        public void onClick(View v) {
        Encrypt encrypt = new Encrypt(getApplicationContext());
        try {
            if (EditPasswordForm.this.getOldPassword().equalsIgnoreCase("")
                    || EditPasswordForm.this.getNewPassword().equalsIgnoreCase("")) {
                Toast.makeText(EditPasswordForm.this, R.string.allFieldsRequired, Toast.LENGTH_SHORT).show();
            }
            else {
                if (EditPasswordForm.this.getOldPassword().compareTo(EditPasswordForm.this.getNewPassword())!= 0) {
                    User myUpdatedUser = EditPasswordForm.this.updatePassword(myUser.getObjectId(),
                        EditPasswordForm.this.getOldPassword(), EditPasswordForm.this.getNewPassword());
                    Toast.makeText(getApplicationContext(), "Updated user profile", Toast.LENGTH_SHORT).show();
                    Intent userProfile = new Intent();
                    userProfile.putExtra("myUser", myUser);
                    setResult(Activity.RESULT_OK, userProfile);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Old and new passwords are equal", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        }
    };

    /**
     * Method UPDATE PASSWORD PROFILE
     * @param objectId
     * @param oldPassword
     * @param newPassword
     * @return
     * @throws ParseException
     */
    public User updatePassword(String objectId,
                               String oldPassword, String newPassword) throws ParseException {
        Encrypt encrypt = new Encrypt(getApplicationContext());
        boolean success = false;
        oldPassword = encrypt.encryptPassword(oldPassword);
        newPassword = encrypt.encryptPassword(newPassword);

        //Comprovacions: correcta password antiga y que la nova password sigui diferent
        if (oldPassword.compareTo(myUser.getPassword())==0 && oldPassword.compareTo(newPassword)!=0) {
            //Actualitzem el password
            Utils.setValueBBDD(newPassword, "Usuario", "Password", objectId);
            myUser.setPassword(newPassword);
        }
        return myUser;
    }

    /**
     * Method getOldPassword
     * @return oldPasswordText
     */
    public String getOldPassword() {
        EditText oldPassword = (EditText) findViewById(R.id.oldPassword);
        String oldPasswordText = oldPassword.getText().toString();
        return oldPasswordText;
    }

    /**
     * Method getNewPassword
     * @return newPasswordText
     */
    public String getNewPassword() {
        EditText newPassword = (EditText) findViewById(R.id.newPassword);
        String newPasswordText = newPassword.getText().toString();
        return newPasswordText;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_password_form, menu);
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