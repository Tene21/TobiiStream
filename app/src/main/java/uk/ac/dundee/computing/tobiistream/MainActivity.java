package uk.ac.dundee.computing.tobiistream;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.*;
import java.net.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }
    public boolean connectToGlasses(final View view)
    {
            Runtime runtime = Runtime.getRuntime();
            try
            {
                Process mIpAddrProcess = runtime.exec("/system/bin/ping -w 1 -c 1 192.168.71.50");
                int mExitValue = mIpAddrProcess.waitFor();
                if(mExitValue==0){
                    //connection successful
                    new AlertDialog.Builder(this)
                            .setTitle("Glasses detected")
                            .setMessage("Successfully located glasses on local network. Would you like to proceed?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //continue to next screen
                                    Intent changeToStream = new Intent(view.getContext(),StreamActivity.class);
                                    startActivity(changeToStream);
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .show();
                }else{
                    //unsuccessful
                    new AlertDialog.Builder(this)
                            .setTitle("Glasses not detected")
                            .setMessage("Could not locate glasses on local network. Please check connection and try again.")
                            .setPositiveButton(android.R.string.yes, null)
                            //adding a proceed statement to allow testing
                            /*.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //continue to next screen
                                    Intent changeToStream = new Intent(view.getContext(),StreamActivity.class);
                                    startActivity(changeToStream);
                                }
                            })
                            */
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
            catch (InterruptedException ignore)
            {
                ignore.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return false;
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
