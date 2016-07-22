package ie.ul.csis.nutrition.user_interface;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;

import api.dto.accounts.AccountRegisterDto;
import ie.ul.csis.nutrition.R;

import ie.ul.csis.nutrition.threading.networking.RegisterRequest;
import ie.ul.csis.nutrition.utilities.Tools;


public class RegisterActivity extends AppCompatActivity {

    private ImageButton btnRegister;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPass;
    private Context context;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        context = this;

        btnRegister = (ImageButton)findViewById(R.id.btnRegister);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etRegisterPassword);
        etConfirmPass = (EditText) findViewById(R.id.etRegisterComfirmPassword);
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Registering...");
        pDialog.setCancelable(false);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pDialog.show();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String confirmPassword = etConfirmPass.getText().toString();


                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Tools.toast(context, context.getString(R.string.error_invalid_email));
                    pDialog.dismiss();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Tools.toast(context, context.getString(R.string.error_passwords_matching));
                    pDialog.dismiss();
                    return;
                }

                register(email, password);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register_user, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void register(String email, String password) {

        AccountRegisterDto dto = new AccountRegisterDto(email, password, password);
        RegisterRequest register = new RegisterRequest(this);
        register.execute(dto);

    }

    public ProgressDialog getProgressDialog() {
        return pDialog;
    }

}
