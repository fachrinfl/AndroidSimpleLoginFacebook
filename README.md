# AndroidSimpleLoginFacebook
Simple login with social media using Facebook SDK

![alt text](https://lh3.googleusercontent.com/970S1GiDgCYBbD4Rk3V7bKUYAuV0xdNVtsvFgOEY6VqOk6O_fLMdCLuqdYHsUea18XHvhIVhuoBnGCXi2-Wq4MNLrseuEnuUdASUhLvOrnlE7fZ8bZ6Uy27lOt_fBV8vZf0N7ARiOWjy0nMUlPmc73cC5whPoNOfURjsgwPeZuW1KMG_6Hkp8LM0AoPqz0xGB09yVnhTBCOqfiS-cmbGPm2AgNkoYLXV1muoKKjecLCPGjUXAPbaU8LUqtbFyXWmtcR9EUVy2yeSk88jVPZ45TYsFQXDwalxbvaG74Qo0E2B_S5XuYcrCBH8SuWnLjwAzYrKHz33UA5JQDGfRT5c19Ckk8vNr3T7c7mdRe65qSLWnFyRZDc2-qQCqFeD1lkbNtBx4dGPde2OIDVz7Nc2M4orhsL2lOXJl7UzRUzcvdyEOXy620MTSziETukZX9GAVsLOmPy-LnzC5qZCdUBCjZ6ZB_BZjjGVyQ7djiprXKIFiwwOQJBgvwq_J5Cb83QHrGUwvPtH9WakAfg632_OewmcRPoDTCdavDIc-pLQMLBERflqDeyGX4mh5NK4dYhbgTdK5clJU7kj_IqpC2qS1ki_whXz_TtRNMoS1gqJow=w980-h623-no)

## Get your hash key
Before getting hash key you have to create key store
```
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
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
```

## Adding Dependencies
```
implementation 'de.hdodenhof:circleimageview:2.1.0'
    implementation 'com.github.bumptech.glide:glide:4.3.1'
    implementation 'com.android.support:design:26.1.0'
    implementation 'com.android.support:cardview-v7:26.1.0'
    implementation'com.jakewharton:butterknife:8.8.1'
    annotationProcessor 'com.jakewharton:butterknife-compiler:8.8.1'
    implementation 'com.facebook.android:facebook-android-sdk:4.7.0'
```

## Making Layout Card Profile

```
<android.support.v7.widget.CardView
        android:layout_centerInParent="true"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:cardElevation="5dp"
        app:cardCornerRadius="5dp">

        <LinearLayout
            android:orientation="vertical"
            android:layout_width="match_parent"
            android:layout_height="match_parent">

            <LinearLayout
                android:layout_margin="15dp"
                android:layout_width="match_parent"
                android:orientation="horizontal"
                android:layout_height="wrap_content">

                <de.hdodenhof.circleimageview.CircleImageView
                    xmlns:app="http://schemas.android.com/apk/res-auto"
                    android:layout_gravity="center"
                    android:id="@+id/iv_profile"
                    android:layout_width="100dp"
                    android:layout_height="100dp"
                    android:src="@drawable/browse"/>

                <RelativeLayout
                    android:layout_width="match_parent"
                    android:layout_height="match_parent">

                    <TextView
                        android:id="@+id/tvProfile"
                        android:layout_centerInParent="true"
                        android:text="Hey, Login your account here"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content" />

                </RelativeLayout>

            </LinearLayout>

            <Button
                android:id="@+id/btnLogin"
                android:background="#3b5998"
                android:text="Login with Facebook"
                android:textColor="@android:color/white"
                android:textAllCaps="false"
                android:layout_width="match_parent"
                android:layout_height="wrap_content" />

        </LinearLayout>

    </android.support.v7.widget.CardView>
```
## Login Process
```
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
```
## Logout Process
```
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
```
