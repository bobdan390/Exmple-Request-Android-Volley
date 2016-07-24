package com.tecnogg.palabras;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by Bob on 29/05/2016.
 */
public class NetworkTasksVolley {
    private static NetworkTasksVolley selfinstance;
    private RequestQueue rq;
    private ImageLoader imgl;
    private static Context context;

    private NetworkTasksVolley(Context context) {
        this.context = context;
        this.rq = this.getRequestQueue();
        this.imgl = new ImageLoader(this.rq, new ImageLoader.ImageCache() {
            //puede que te pida que configures el API a 12 como minimo
            private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(20);
            @Override
            public Bitmap getBitmap(String url) {
                return this.cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                this.cache.put(url, bitmap);
            }
        });
    }

    public RequestQueue getRequestQueue() {
        if(this.rq == null) {this.rq = Volley.newRequestQueue(this.context.getApplicationContext());}
        return this.rq;
    }

    public <T> void addToRequestQueue(Request<T> rqq){
        this.getRequestQueue().add(rqq);
    }

    public ImageLoader getImageLoader() {
        return this.imgl;
    }

    public static synchronized NetworkTasksVolley getInstance(Context context){
        if(selfinstance == null) { selfinstance = new NetworkTasksVolley(context);}
        return selfinstance;
    }
}
