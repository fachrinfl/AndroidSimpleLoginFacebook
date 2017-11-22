package com.example.fachrinfl.android_simple_login_facebook;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.iv_profile) ImageView iv_profile;
    @BindView(R.id.tvProfile) TextView tvProfile;
    @BindView(R.id.btnLogin) Button btnLogin;
    private AccessToken accessToken;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        String hashkey = _GetHashKey();
        Log.d("hashKey : ", hashkey);
        _LoginFacebook();

    }

    public String _GetHashKey(){
        try {
            PackageInfo info = this.getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures){
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                return Base64.encodeToString(md.digest(), Base64.DEFAULT);
            }
        }catch (PackageManager.NameNotFoundException e){
            return "SHA-1 generation; the key count not be generated: NameNotFoundException thrown";
        }catch (NoSuchAlgorithmException e){
            return "SHA-1 generation; the key count not be generated: NameNotFoundException thrown";
        }
        return "SHA-1 generation: epic failed";
    }

    private void _LoginFacebook() {
        if(accessToken !=null){
            accessToken = com.facebook.AccessToken.getCurrentAccessToken();
        }
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                System.out.println("Granted permission::"+loginResult.getRecentlyGrantedPermissions());

                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        String userDetail = response.getRawResponse();
                        try {
                            JSONObject jsonObject = new JSONObject(userDetail);
                            System.out.println("jsonObject::"+jsonObject);

                            String facebookId = jsonObject.getString("id");
                            String facebookName = jsonObject.getString("name");
                            String facebookImage = "https://graph.facebook.com/"+facebookId+"/picture?type=large";
                            String emailfb = jsonObject.getString("email");

                            tvProfile.setText("Helo, " + facebookName);
                            Glide.with(getApplicationContext()).load(facebookImage).into(iv_profile);
                            btnLogin.setText("Logout this account");
                            btnLogin.setBackgroundColor(Color.parseColor("#FF006C"));
                            btnLogin.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    _LogoutFacebook();
                                    finish();
                                    startActivity(getIntent());
                                }
                            });

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "name,email");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {
                System.out.println("LOGIN CANCEL");
            }

            @Override
            public void onError(FacebookException error) {
                System.out.println("NETWORK ERROR");

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile, email"));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void _LogoutFacebook() {

        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }

        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {

                LoginManager.getInstance().logOut();

            }
        }).executeAsync();
    }
}
