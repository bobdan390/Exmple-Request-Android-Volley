package com.tecnogg.palabras;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends Activity
{
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    private WebView mWebView;
    private static final String TAG = "MyActivity";
    public String rutaimagen;
    public String Versiculo_="null";
    public boolean isWhassap_ = true;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        setContentView(R.layout.activity_main);
        startService(new Intent(MainActivity.this, Servicio.class));

        boolean lol = isNetworkConnected(this);
        if (lol==false){
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Info");
            dialogo1.setMessage("No se detecto ninguna red de datos..!!");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Salir", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                }
            });
            dialogo1.show();
        }else {

            mWebView = (WebView) findViewById(R.id.activity_main_webview);
            WebSettings webSettings = mWebView.getSettings();
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setJavaScriptEnabled(true);
            mWebView.loadUrl("http://palabrasdevida.info.ve/android/");
            mWebView.setWebViewClient(new WebViewClient());
            mWebView.setWebViewClient(new MyAppWebViewClient());
            mWebView.loadUrl("javascript:Android.getIds(Ids);");
            mWebView.addJavascriptInterface(this, "Android");
            /*mWebView.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    WebView.HitTestResult hr = ((WebView) v).getHitTestResult();
                    presionar(hr.getExtra());
                    return false;
                }
            });*/
        }



    }

    @Override
    public void onBackPressed() {

        // Check if there's history
        if (this.mWebView.canGoBack())
            this.mWebView.goBack();
        else
            super.onBackPressed();

    }
    private boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info == null || !info.isConnected() || !info.isAvailable()) {
            return false;
        }
        return true;
    }

    @android.webkit.JavascriptInterface
    public void tw(String t){
        String seeAllProjectURL="http://palabrasdevida.info.ve/android/data_share.php?id="+t;
        JsonArrayRequest jor = new JsonArrayRequest(
                Request.Method.GET, seeAllProjectURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            JSONObject project = response.getJSONObject(0);
                            Versiculo_ = project.getString("versiculo");
                            isWhassap_ = false;
                            CargaImagenes nuevaTarea = new CargaImagenes();
                            nuevaTarea.execute(project.getString("imagen"));


                            /*String msg = project.getString("versiculo");
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_TEXT, msg);
                            intent.setType("text/plain");
                            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(ruta));
                            intent.setType("image/jpeg");
                            intent.setPackage("com.twitter.android");
                            startActivity(intent);*/

                        }catch (org.json.JSONException e){
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //startActivity(new Intent(getApplicationContext(), ConnectionProblemActivity.class));
                    }
                }
        );
        NetworkTasksVolley.getInstance(this.getApplicationContext()).addToRequestQueue(jor);

    };

    @android.webkit.JavascriptInterface
    public void cambiar(String t){
        /*Toast toast = Toast.makeText(this, t, Toast.LENGTH_SHORT);
         toast.show();*/
       /* Intent i = new Intent(getApplicationContext(), Main2Activity.class );
        i.putExtra("parametro", t);
        startActivity(i);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);*/
        /*
        CargaImagenes nuevaTarea = new CargaImagenes();
        nuevaTarea.execute(t,URL);
        */
        String seeAllProjectURL="http://palabrasdevida.info.ve/android/data_share.php?id="+t;
        JsonArrayRequest jor = new JsonArrayRequest(
                Request.Method.GET, seeAllProjectURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            JSONObject project = response.getJSONObject(0);
                            //String estado_ = project.getString("estado");

                            if (ShareDialog.canShow(ShareLinkContent.class)) {
                                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                        .setContentTitle(project.getString("titulo"))
                                        .setQuote(project.getString("versiculo"))
                                        .setContentDescription(
                                                project.getString("contenido"))
                                        .setImageUrl(Uri.parse(project.getString("imagen")))
                                        .setContentUrl(Uri.parse("www.palabrasdevida.info.ve"))
                                        .build();

                                shareDialog.show(linkContent);
                            }

                        }catch (org.json.JSONException e){
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //startActivity(new Intent(getApplicationContext(), ConnectionProblemActivity.class));
                    }
                }
        );
        NetworkTasksVolley.getInstance(getContex()).addToRequestQueue(jor);


    }

    @android.webkit.JavascriptInterface
    public void wh(String t){
        String seeAllProjectURL="http://palabrasdevida.info.ve/android/data_share.php?id="+t;
        JsonArrayRequest jor = new JsonArrayRequest(
                Request.Method.GET, seeAllProjectURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            JSONObject project = response.getJSONObject(0);
                            //String estado_ = project.getString("estado");

                            Versiculo_ = project.getString("versiculo");
                            isWhassap_ = true;
                            CargaImagenes nuevaTarea = new CargaImagenes();
                            nuevaTarea.execute(project.getString("imagen"));

                        }catch (org.json.JSONException e){
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //startActivity(new Intent(getApplicationContext(), ConnectionProblemActivity.class));
                    }
                }
        );
        NetworkTasksVolley.getInstance(this.getApplicationContext()).addToRequestQueue(jor);

    };

    public class CargaImagenes extends AsyncTask<String, Bitmap, Bitmap> {
        ProgressDialog pDialog;
        ProgressDialog pDialog2;

        @Override
        protected Bitmap doInBackground(String... params) {
            // TODO Auto-generated method stub
            String url = params[0];
            Bitmap imagen = descargarImagen(url);
            return imagen;
        }


        @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();

        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setMessage("Cargando Informacion");
        pDialog.setCancelable(true);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.show();

    }


    @Override
    protected void onPostExecute(Bitmap result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        //String ruta_ = guardarImagen(getContex(),"imagen",result);
        ContextWrapper cw = new ContextWrapper(getContex());
        File dirImages = cw.getDir("Imagenes", Context.MODE_PRIVATE);
        File myPath = new File(dirImages, "imagen" + ".jpg");
        FileOutputStream fos = null;
        try{
            fos = new FileOutputStream(myPath);
            result.compress(Bitmap.CompressFormat.JPEG, 10, fos);
            fos.flush();
            fos.close();
            myPath.setReadable(true,false);
        }catch (FileNotFoundException ex){
            ex.printStackTrace();
        }catch (IOException ex){
            ex.printStackTrace();
        }

        pDialog.dismiss();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_TEXT, Versiculo_);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(myPath));
        intent.setType("image/jpeg");
        if (isWhassap_==true){
            intent.setPackage("com.whatsapp");
        }else{
            intent.setPackage("com.twitter.android");
        }
        try {
            startActivity(intent);
        }catch (ActivityNotFoundException e){
            pDialog2 = new ProgressDialog(MainActivity.this);
            pDialog2.setMessage("Aplicacion no instalada en su dispositivo!!");
            pDialog2.setCancelable(true);
            pDialog2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog2.show();
        }
    }



    }

    public Context getContex() {
        return this.getApplicationContext();
    }

    private Bitmap descargarImagen (String imageHttpAddress){
        URL imageUrl = null;
        Bitmap imagen = null;
        try{
            imageUrl = new URL(imageHttpAddress);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.connect();
            imagen = BitmapFactory.decodeStream(conn.getInputStream());
        }catch(IOException ex){
            ex.printStackTrace();
        }

        return imagen;
    }

}



