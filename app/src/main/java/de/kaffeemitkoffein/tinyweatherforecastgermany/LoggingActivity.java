/**
 * This file is part of TinyWeatherForecastGermany.
 *
 * Copyright (c) 2020, 2021 Pawel Dube
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.*;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LoggingActivity extends Activity {

    Executor executor;
    String logs;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = getApplicationContext();
        ThemePicker.SetTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logging);
        executor = Executors.newSingleThreadExecutor();
        // action bar layout
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_HOME_AS_UP|ActionBar.DISPLAY_SHOW_TITLE);

        final TextView logview = (TextView) findViewById(R.id.logging_infoTextView);
        logview.setText("Loading...");
        // Read the logs asynchronously
        PrivateLog.AsyncGetLogs asyncGetLogs = new PrivateLog.AsyncGetLogs(this){
            @Override
            public void onPositiveResult(ArrayList<String> result) {
                SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
                for (int i=0; i<result.size(); i++){
                    String line = result.get(i) + System.lineSeparator();
                    Spannable spannable = new SpannableString(line);
                    int color = Color.WHITE;
                    if (result.get(i).contains("[0]")){
                        color = Color.GREEN;
                    }
                    if (result.get(i).contains("[1]")){
                        color = Color.YELLOW;
                    }
                    if (result.get(i).contains("[2]")){
                        color = Color.RED;
                    }
                    if (result.get(i).contains("[3]")){
                        color = Color.MAGENTA;
                    }
                    spannable.setSpan(new ForegroundColorSpan(color),0,spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    stringBuilder.append(spannable);
                }
                logs = stringBuilder.toString();
                logview.setText(stringBuilder);
            }

            @Override
            public void onNegativeResult(){
                if (logview!=null){
                    logview.setText("No logs.");
                }
            }
        };
        executor.execute(asyncGetLogs);
        // register buttons
        final Button button_back = (Button) findViewById(R.id.logging_button_back);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        final Button button_copy = (Button) findViewById(R.id.logging_button_copy);
        button_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PrivateLog.copyLogsToClipboard(getApplicationContext())){
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.logging_copy_success),Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.logging_copy_fail),Toast.LENGTH_LONG).show();
                }
            }
        });
        final Button button_clear = (Button) findViewById(R.id.logging_button_clear);
        button_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PrivateLog.clearLogs(getApplicationContext())){
                    logview.setText("");
                }
            }
        });

        final Button button_share = (Button) findViewById(R.id.logging_button_share);
        button_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareLogs();
            }
        });
    }

    public void shareLogs(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this,0);
        builder.setTitle(getApplicationContext().getResources().getString(R.string.logging_comment_title));
        Drawable drawable = new BitmapDrawable(getResources(),WeatherIcons.getIconBitmap(context,WeatherIcons.IC_ANNOUNCEMENT,false));
        builder.setIcon(drawable);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.logcomment,null,false);
        builder.setView(view);
        final EditText comment = (EditText) view.findViewById(R.id.logcomment_input);
        builder.setNegativeButton(R.string.geoinput_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // do nothing
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton(R.string.alertdialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Uri uri = PrivateLog.getLogUri(getApplicationContext());
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setDataAndType(uri, "text/plain");
                Log.v("TWFG","URI: "+uri.toString());
                String subject=getApplicationContext().getResources().getString(R.string.logging_title);
                if (comment!=null){
                    String s = comment.getText().toString();
                    if (s!=null){
                        if (s.length()>50){
                            subject = subject+": " +s.substring(0,50)+"…";
                        } else {
                            subject = subject+":"  +s;
                        }
                    }
                    logs = s + System.lineSeparator() + System.lineSeparator() + logs;
                }
                intent.putExtra(Intent.EXTRA_SUBJECT,subject);
                intent.putExtra(Intent.EXTRA_TEXT,logs);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    try {
                        startActivity(Intent.createChooser(intent, "share logs"));
                    } catch (ActivityNotFoundException e) {
                        //
                    }
                } else {

                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
