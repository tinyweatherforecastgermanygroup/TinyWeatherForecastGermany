/*
 * This file is part of TinyWeatherForecastGermany.
 *
 * Copyright (c) 2020 Pawel Dube
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

import android.os.Bundle;
import android.app.*;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LoggingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logging);
        // action bar layout
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_HOME_AS_UP|ActionBar.DISPLAY_SHOW_TITLE);

        final TextView logview = (TextView) findViewById(R.id.logging_infoTextView);
        logview.setText("Loading...");
        // Read the logs asynchronously
        PrivateLog.AsyncGetLogs asyncGetLogs = new PrivateLog.AsyncGetLogs(this){
            @Override
            public void onPositiveResult(String result) {
                logview.setText(result);
            }

            @Override
            public void onNegativeResult(){
                if (logview!=null){
                    logview.setText("No logs.");
                }
            }
        };
        asyncGetLogs.execute();
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
    }

}
