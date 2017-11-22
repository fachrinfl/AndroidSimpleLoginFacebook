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
