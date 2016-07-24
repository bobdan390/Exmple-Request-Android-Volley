package com.tecnogg.palabras;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;



public class ServiceBoot extends BroadcastReceiver {



    @Override
    public void onReceive(Context context, Intent intent) {


        // LANZAR SERVICIO
        Intent startServiceIntent = new Intent(context, Servicio.class);
        context.startService(startServiceIntent);


    }
}
