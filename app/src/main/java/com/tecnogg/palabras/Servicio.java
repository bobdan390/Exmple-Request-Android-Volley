package com.tecnogg.palabras;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Bob on 27/05/2016.
 */
public class Servicio extends Service {
    static final int UPDATE_INTERVAL = 20000;
    private Timer timer = new Timer();

    @Override

    public void onCreate() {


        //Toast.makeText(this, "Servicio creado",Toast.LENGTH_SHORT).show();


    }



    @Override

    public int onStartCommand(Intent intenc, int flags, int idArranque) {

       // Toast.makeText(this,"Servicio arrancado "+ idArranque,Toast.LENGTH_SHORT).show();
        doSomethingRepeatedly();
        return START_STICKY;

    }



    @Override

    public void onDestroy() {

        Toast.makeText(this,"Servicio detenido",
                Toast.LENGTH_SHORT).show();

    }



    @Override

    public IBinder onBind(Intent intencion) {

        return null;

    }

    public void msg(String text){
        Toast.makeText(this, text,Toast.LENGTH_SHORT).show();
    }

    private void doSomethingRepeatedly() {

        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {

                actualizar();

            }

        }, 0, UPDATE_INTERVAL);


    }

    public void actualizar(){
        String id = Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        //http://mywebhtml5.site88.net/palabras/logic.php?var1=1&var2=2016-05-29 23:00:00
        String f = "0";
        final String eulaKey = "mykey";
        Context mContext = getApplicationContext();
        SharedPreferences mPrefs = mContext.getSharedPreferences("myAppPrefs", Context.MODE_PRIVATE);
        /*SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(eulaKey, "0");
        editor.commit();*/
        String h =mPrefs.getString("mykey", "");
        //msg(h);

        String seeAllProjectURL="http://palabrasdevida.info.ve/android/logic.php?var1="+id+"&var2="+h;
        JsonArrayRequest jor = new JsonArrayRequest(
                Request.Method.GET, seeAllProjectURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            JSONObject project = response.getJSONObject(0);
                            String estado_ = project.getString("estado");

                            if(estado_.equals("none")){
                                //NO HACEMOS NADA
                                //msg("No hacemos nada");
                            }
                            if(estado_.equals("new")){
                                //INSERTAMOS LA FECHA EN LA BD - NUEVO USUARIO
                                String nuevaFecha = project.getString("insertar");
                                //msg("INSERTAMOS LA FECHA EN LA BD-SIN ACUTALIZACION");
                                //insertar(nuevaFecha);
                                final String eulaKey = "mykey";
                                Context mContext = getApplicationContext();
                                SharedPreferences mPrefs = mContext.getSharedPreferences("myAppPrefs", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = mPrefs.edit();
                                editor.putString(eulaKey, nuevaFecha);
                                editor.commit();

                            }
                            if(estado_.equals("update")){
                                //CREAMOS UN ALERT
                                String nuevaFecha = project.getString("insertar");
                                String titulo = project.getString("titulo");
                                String versiculo = project.getString("versiculo");
                                //msg("MOSTRAMOS UN MNENSAJE");
                                final String eulaKey = "mykey";
                                Context mContext = getApplicationContext();
                                SharedPreferences mPrefs = mContext.getSharedPreferences("myAppPrefs", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = mPrefs.edit();
                                editor.putString(eulaKey, nuevaFecha);
                                editor.commit();
                                NotificationCompat.Builder mBuilder =
                                        new NotificationCompat.Builder(Servicio.this)
                                                .setSmallIcon(R.mipmap.ic_launcher)
                                                .setContentTitle("[Palabras De Vida] " + titulo)
                                                .setContentText(versiculo)
                                                .setContentInfo("")
                                                .setTicker("[Palabras De Vida] "+titulo)
                                                .setAutoCancel(true);
                                Intent notIntent =
                                        new Intent(Servicio.this, MainActivity.class);

                                PendingIntent contIntent =
                                        PendingIntent.getActivity(
                                                Servicio.this, 0, notIntent, 0);

                                mBuilder.setContentIntent(contIntent);

                                NotificationManager mNotificationManager =
                                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                                mNotificationManager.notify(20760933, mBuilder.build());
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
        NetworkTasksVolley.getInstance(this.getApplicationContext()).addToRequestQueue(jor);
    }

}
