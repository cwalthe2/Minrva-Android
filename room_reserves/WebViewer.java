package edu.illinois.ugl.minrva.room_reserves;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import edu.illinois.ugl.minrva.R;
import edu.illinois.ugl.minrva.core.help.HelpActivity;
import greendroid.app.GDActivity;
import greendroid.widget.ActionBarItem;
import greendroid.widget.ActionBarItem.Type;

public class WebViewer extends GDActivity{
	private ProgressDialog progressBar;
	private Activity activity;
	
	
	@SuppressLint("SetJavaScriptEnabled")
	public void onCreate(Bundle savedInstanceState) {
		
	super.onCreate(savedInstanceState);
	setActionBarContentView(R.layout.roomreserves_webview);
	addActionBarItem(Type.Help);
	
	activity = this;
	
	
	 WebView mWebView = (WebView) findViewById(R.id.mweb);
	 WebSettings settings = mWebView.getSettings();
     settings.setJavaScriptEnabled(true);
     final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
     progressBar = ProgressDialog.show(WebViewer.this, "Loading D!BS", "Loading...");
     mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i("Web", "Processing webview url click...");
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                Log.i("Web", "Finished loading URL: " +url);
                if (progressBar.isShowing()) {
                    progressBar.dismiss();
                }
            }

            @SuppressWarnings("deprecation")
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.e("Web", "Error: " + description);
                Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
                alertDialog.setTitle("Error");
                alertDialog.setMessage(description);
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                alertDialog.show();
            }
        });
     
     // Display the web page
     mWebView.loadUrl(RoomReserves.d_url);
	}
	
	@Override
    public boolean onHandleActionBarItemClick(ActionBarItem item, int position) 
	{
		Intent intent = new Intent(this, HelpActivity.class);
		intent.putExtra("help_uri", getString(R.string.roomreserve_help));
		intent.putExtra(GD_ACTION_BAR_TITLE, getIntent().getStringExtra(GD_ACTION_BAR_TITLE));
		intent.putExtra(GD_ACTION_BAR_ICON, getIntent().getIntExtra(GD_ACTION_BAR_ICON, R.drawable.stub));
	    startActivity(intent);
	    return true;	                         
    }
}
