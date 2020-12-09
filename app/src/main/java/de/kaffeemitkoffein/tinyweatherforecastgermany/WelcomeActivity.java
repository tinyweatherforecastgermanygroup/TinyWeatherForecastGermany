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

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WelcomeActivity extends Activity {

    RelativeLayout pager;
    LayoutInflater layoutInflater;

    int page = 1;
    ImageView dot1;
    ImageView dot2;
    ImageView dot3;
    ImageView arrow_right;
    ImageView arrow_left;
    TextView skip;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Boolean force_replay = false;
        Intent intent = getIntent();
        if (intent!=null){
            Bundle bundle = intent.getExtras();
            if (bundle!=null){
                String action = bundle.getString("mode");
                if (action!=null){
                    if (action.equals("replay")){
                        force_replay = true;
                    }
                }
            }
        }
        if ((WeatherSettings.isFirstAppLaunch(getApplicationContext())) || (force_replay)){
            WeatherSettings.setAppLaunchedFlag(getApplicationContext());
            setContentView(R.layout.activity_welcome);
            // action bar layout
            ActionBar actionBar = getActionBar();
            if (actionBar!=null){
                actionBar.hide();
            }
            pager = (RelativeLayout) findViewById(R.id.welcome_pager);
            layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // View screen1 = layoutInflater.inflate(R.layout.welcome_screen1,pager,true);
            dot1 = (ImageView) findViewById(R.id.welcome_dot1);
            dot1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    page = 1;
                    setPage(page);
                }
            });
            dot2 = (ImageView) findViewById(R.id.welcome_dot2);
            dot2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    page = 2;
                    setPage(page);
                }
            });
            dot3 = (ImageView) findViewById(R.id.welcome_dot3);
            dot3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    page = 3;
                    setPage(page);
                }
            });
            pager.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (page<3) {
                        page++;
                        setPage(page);
                    } else {
                        startMainActivityAndShowCircle();
                    }
                }
            });
            setPage(page);
            arrow_right = (ImageView) findViewById(R.id.welcome_arrow_right);
            arrow_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (page<3) {
                        page++;
                        setPage(page);
                    }
                }
            });
            arrow_left = (ImageView) findViewById(R.id.welcome_arrow_left);
            arrow_left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (page>1){
                        page--;
                        setPage(page);
                    }
                }
            });
            skip = (TextView) findViewById(R.id.welcome_skip);
            skip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startMainActivityAndShowCircle();
                }
            });
        } else {
            startMainActivity();
        }
    }

    private View setPage(int page){
        View result_view = null;
        if (page==1){
            result_view = layoutInflater.inflate(R.layout.welcome_screen1,pager,true);
            dot1.setImageResource(R.mipmap.ic_radio_button_checked_white_24dp);
            dot2.setImageResource(R.mipmap.ic_radio_button_unchecked_white_24dp);
            dot3.setImageResource(R.mipmap.ic_radio_button_unchecked_white_24dp);
        }
        if (page==2){
            result_view = layoutInflater.inflate(R.layout.welcome_screen2,pager,true);
            dot1.setImageResource(R.mipmap.ic_radio_button_unchecked_white_24dp);
            dot2.setImageResource(R.mipmap.ic_radio_button_checked_white_24dp);
            dot3.setImageResource(R.mipmap.ic_radio_button_unchecked_white_24dp);
        }
        if (page==3){
            result_view = layoutInflater.inflate(R.layout.welcome_screen3,pager,true);
            dot1.setImageResource(R.mipmap.ic_radio_button_unchecked_white_24dp);
            dot2.setImageResource(R.mipmap.ic_radio_button_unchecked_white_24dp);
            dot3.setImageResource(R.mipmap.ic_radio_button_checked_white_24dp);
        }
        if (page==4){
            result_view = layoutInflater.inflate(R.layout.welcome_spinner,pager,true);
            dot1.setVisibility(View.GONE);
            dot2.setVisibility(View.GONE);
            dot3.setVisibility(View.GONE);
            arrow_left.setVisibility(View.GONE);
            arrow_right.setVisibility(View.GONE);
            skip.setVisibility(View.GONE);
        }
        return result_view;
    }

    private void startMainActivity(){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private void startMainActivityAndShowCircle(){
        setPage(4);
        startMainActivity();
        finish();
    }


}
