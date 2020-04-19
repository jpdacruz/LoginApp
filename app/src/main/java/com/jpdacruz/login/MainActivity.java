package com.jpdacruz.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private TextView wUserName, wUserEmail, wUserProvider;
    private CircleImageView wUserPhoto;

    private static final int REQUEST_CODE_SIGN_IN =123;
    private static final String TERMINOS_Y_CONDICIONES = "https://www.privacypolicyonline.com/live.php?token=reBRF1Tk2oAIupspj5e0X76qk30KUb8d";
    private static final String PROVEEDOR_DESCONOCIDO = "Proveedor desconocido";
    private static final String PROVEEDOR_FIREBASE = "password";
    private static final String PROVEEDOR_FACEBOOK = "facebook.com";
    private static final String PROVEEDOR_GOOGLE = "google.com";
    private static final String PROVEEDOR_PHONE = "phone";
    private static final String TAG = "MainActivity";
    //variable de firebase Auth y el listener para inicio o cierre de seción
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wUserPhoto = findViewById(R.id.imageView);
        wUserName = findViewById(R.id.textViewUserName);
        wUserEmail = findViewById(R.id.textViewUserEmail);
        wUserProvider = findViewById(R.id.textViewUserProvider);

        //toma la instancia de FireBaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();
        //se crea el listener de FirebaseAuth
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                // crea un usuario Firebase y le asigna el usuario que esta autorizado por parametro firebaseAuth
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if( user!= null){
                    //de aca se extrae la info del user logeado, como nombre usuario, su foto, su email
                    //se pasan 3 datos por parametros getDisplayName, getEmail, getProviderID si es distinto de null
                    onSetDataUser(user,user.getProviderId() != null ? //si lo es
                            user.getProviderData().get(1).getProviderId() : PROVEEDOR_DESCONOCIDO);//toma el dato de cual proveedor es, si no desconocido
                    Log.d(TAG,"PROVEEDOR onCreate: " + user.getProviderId());

                    Log.d(TAG, "PROVEEDOR onCreate DATA + ID :" + user.getProviderData().get(1).getProviderId());

                }else {

                    //LIMPIAR LOS DATOS AL INICIAR SIN LOGIN
                    onSignedOutCleaner();

                    //login con facebook
                    AuthUI.IdpConfig facebookIdp = new AuthUI.IdpConfig.FacebookBuilder().build();
                    AuthUI.IdpConfig emailIdp = new AuthUI.IdpConfig.EmailBuilder().build();
                    AuthUI.IdpConfig googleIdp = new AuthUI.IdpConfig.GoogleBuilder().build();
                    AuthUI.IdpConfig phoneIdp = new AuthUI.IdpConfig.PhoneBuilder().build();
                    //login con email
                    startActivityForResult(AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false) //INDICA QUE NO RECUERDE LAS CONTRASEÑAS
                            .setTosAndPrivacyPolicyUrls(TERMINOS_Y_CONDICIONES,TERMINOS_Y_CONDICIONES)
                            .setTheme(R.style.GreenTheme)
                            .setAvailableProviders(Arrays.asList(emailIdp, facebookIdp, phoneIdp, googleIdp))
                            .build(),REQUEST_CODE_SIGN_IN);
                    //usuario sin sesion activa o que la cerró
                }
            }
        };

        /*para generar la clave hash de facebook
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.jpdacruz.login",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== REQUEST_CODE_SIGN_IN){

            if(resultCode == RESULT_OK){

                Toast.makeText(this, "Inicio exitoso", Toast.LENGTH_SHORT).show();
            }
        }else {

            Toast.makeText(this, "Fallo en el inicio de sesion", Toast.LENGTH_SHORT).show();
        }
    }

    //si la aplicacion entra en pause pone el remueve el listener
    @Override
    protected void onPause() {
        super.onPause();

        if(mAuthStateListener!= null){

            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    //cuando la aplicacion vuelve lo agrega nuevamente al listener
    @Override
    protected void onResume() {
        super.onResume();

        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //caso de elegir del menu desconectar desconecta
        //se usa switch para permitir futura escalabilidad
        //retorna true para identificar que salio bien el proceso
        switch (item.getItemId()){
            case R.id.action_sign_out:
                AuthUI.getInstance().signOut(this);
                return true;

                // cierra sesion y vuelve al listener, que detecta un cambio, que no hay nadie logeado y pide login nuevamente

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onSetDataUser(FirebaseUser user, String provider) {

        wUserName.setText(user.getDisplayName());
        wUserEmail.setText(user.getEmail());

        Log.d(TAG, "PROVEEDOR onSetDataUser " + provider);

        int drawableRes;

        switch (provider){

            case PROVEEDOR_FIREBASE:

                drawableRes = R.drawable.ic_email;
                wUserPhoto.setImageResource(R.drawable.ic_account);
                break;

            case PROVEEDOR_FACEBOOK:

                drawableRes = R.drawable.ic_facebook;

                Uri image_uri = user.getPhotoUrl();

                Glide.with(MainActivity.this).load(image_uri).into(wUserPhoto);
                break;

            case PROVEEDOR_GOOGLE:

                drawableRes = R.drawable.ic_google;
                Glide.with(MainActivity.this).load(user.getPhotoUrl()).into(wUserPhoto);
                break;

            case PROVEEDOR_PHONE:

                drawableRes = R.drawable.ic_cellphone_android;
                Glide.with(MainActivity.this).load(user.getPhotoUrl()).into(wUserPhoto);
                wUserPhoto.setImageResource(R.drawable.ic_cellphone_android);
                break;

            default:

                drawableRes = R.drawable.ic_email_alert;
                provider = PROVEEDOR_DESCONOCIDO;
                break;
        }

        //muestra el icon correspondiente
        wUserProvider.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableRes,0,0,0);
        wUserProvider.setText(provider);
    }

    private void onSignedOutCleaner() {

        wUserName.setText("");
        wUserEmail.setText("");
        wUserProvider.setText("");
        wUserPhoto.setImageResource(R.drawable.ic_launcher_background);
    }
}
