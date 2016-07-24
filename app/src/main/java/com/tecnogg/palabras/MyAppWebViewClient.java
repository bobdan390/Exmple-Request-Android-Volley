package com.tecnogg.palabras;

import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Bob on 21/05/2016.
 */
public class MyAppWebViewClient extends WebViewClient {


    public boolean shouldOverrideUrlLoading(WebView view, String url) {

// Url base de la APP (al salir de esta url, abre el navegador) poner como se muestra, sin http://
        if(Uri.parse(url).getHost().endsWith("palabrasdevida.info.ve")) {
            return false;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        view.getContext().startActivity(intent);
        System.out.print("La url---&gt;"+url);
        return true;

    }
}
