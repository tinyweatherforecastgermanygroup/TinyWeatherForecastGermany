/**
 * This file is part of TinyWeatherForecastGermany.
 *
 * Copyright (c) 2020, 2021, 2022, 2023 Pawel Dube
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

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import org.astronomie.info.Astronomy;

import java.text.SimpleDateFormat;
import java.util.*;

public class WeatherSliderView extends HorizontalScrollView {

    private class ValueSet{
        int popupWidth;
        int popupHeight;
        int marginImageLeft;
        boolean isLandscape;
        int iconSize;
        int XSCALE;
        int imageHeight;
        int imageWidth;
        double minTemperatureKelvin=Weather.KelvinConstant*2;
        double maxTemperatureKelvin=0f;
        double temperatureRange;
        double minTemperatureCelsius;
        double maxTemperatureCelsius;
        double pixelPerDegree;
        double pixelPerProb;
        float paddingX;
        float paddingY;
        float labelTextSize;
        double millisPerPixel;
        float pixelPer1hItem;
        float pixelPer6hItem;
        long timeXOffset;
        float cloudsOffsetHigh;
        float cloudsOffsetMid;
        float cloudsOffsetLow;
        float cloudsImageHight;
        float cloudsImageWidth;
        float cloudsImageinSampleSize;
        float cloudsImageXSampleFactor;
        float cloudsImageYSampleFactor;

        final class COLOR {
            final static int Sky = 0xff50b2f0;
            final static int Night = 0xff2e373c;
        }

        public ValueSet(int popupWidth, int popupHeight){
            this.imageHeight = popupHeight;
            this.marginImageLeft = popupWidth/2;
            this.XSCALE = popupWidth/12;
            if (popupWidth>popupHeight){
                this.XSCALE = popupWidth/36;
            }
            if (isLandscape){
                this.XSCALE = popupWidth/64;
            }
            //this.XSCALE=20;
            for (int i=0; i<weatherInfos.size(); i++){
                if (weatherInfos.get(i).getTemperature()>maxTemperatureKelvin){
                    maxTemperatureKelvin=weatherInfos.get(i).getTemperature();
                }
                if (weatherInfos.get(i).getTemperature()<minTemperatureKelvin){
                    minTemperatureKelvin=weatherInfos.get(i).getTemperature();
                }
            }
            temperatureRange=maxTemperatureKelvin-minTemperatureKelvin;
            minTemperatureCelsius=minTemperatureKelvin-Weather.KelvinConstant;
            maxTemperatureCelsius=maxTemperatureKelvin-Weather.KelvinConstant;
            pixelPerDegree = (imageHeight*0.9f / temperatureRange);
            pixelPerProb   = (imageHeight*0.9f / 100);
            paddingY = imageHeight * 0.05f;
            this.cloudsOffsetHigh=imageHeight*0.25f;
            this.cloudsOffsetMid=imageHeight*0.5f;
            this.cloudsOffsetLow=imageHeight*0.75f;
            paddingX=XSCALE;
            this.imageWidth=Math.round(weatherInfos.size()*XSCALE);
            this.timeXOffset = weatherInfos.get(0).getTimestamp();
            this.pixelPer1hItem = (imageWidth-paddingX*2)/weatherInfos.size();
            this.pixelPer6hItem = (imageWidth-paddingX*2)/weatherInfos6h.size();
            this.cloudsImageHight = imageHeight/5f;
            this.cloudsImageWidth = pixelPer6hItem;
            final float staticCloudMultiplier = 2f;
            this.cloudsImageinSampleSize = Math.max(700/cloudsImageWidth,700/cloudsImageHight)/staticCloudMultiplier;
            DisplayMetrics displayMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
            // px = imageOriginalPixels * 0.39 * (dpiDevice/160)
            this.cloudsImageXSampleFactor  = (float) (0.39 * (displayMetrics.xdpi/160))*staticCloudMultiplier;
            this.cloudsImageYSampleFactor  = (float) (0.39 * (displayMetrics.ydpi/160))*staticCloudMultiplier;
            long timeSpan = weatherInfos.get(weatherInfos.size()-1).getTimestamp() - weatherInfos.get(0).getTimestamp();
            this.millisPerPixel = (double) (timeSpan/(imageWidth-paddingX*2));
            /*
            Log.v("twfg","imageWidth: "+imageWidth);
            Log.v("twfg","imageHeight: "+imageHeight+" ("+imageHeight/4+")");
            Log.v("twfg","timespan  : "+timeSpan);
            Log.v("twfg","pixelPerMilli: "+millisPerPixel);
            Log.v("twfg","pixelPer1hItem: "+pixelPer1hItem);
            Log.v("twfg","pixelPer6hItem: "+pixelPer6hItem);
             */
            Paint textPaint = new Paint();
            labelTextSize = paddingX;
            do {
                labelTextSize--;
                textPaint.setTextSize(labelTextSize);
            } while (textPaint.measureText("XXX°")>paddingX);
        }
    }

    private Context context;
    private static final SparseArray<Bitmap> BITMAP_CACHE = new SparseArray<>();
    private Weather.WeatherLocation weatherLocation;
    private ValueSet valueSet;
    private ArrayList<Weather.WeatherInfo> weatherInfos;
    private ArrayList<Weather.WeatherInfo> weatherInfos6h;
    private boolean isScrollable = false;
    private TextView temperatureTextView;
    private TextView dayTextView;
    private ImageView windImageView;
    private TextView windTextView;
    private ImageView labellingImageView;
    private TextView clouds1;
    private TextView clouds2;
    private TextView clouds3;
    private WindowManager windowManager;


    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, HH:mm");

    @Override
    public void onScrollChanged(int x, int y, int oldx, int oldy){
        int itemPosition = Math.round(x / valueSet.pixelPer1hItem);
        upateViews(itemPosition);
    }

    public void setScrollPosition(int itemPosition){
        smoothScrollTo(Math.round(valueSet.pixelPer1hItem*itemPosition),0);
    }

    public void upateViews(int itemPosition){
        if ((itemPosition>=0) && (itemPosition<weatherInfos.size())) {
            if ((temperatureTextView != null) && (weatherInfos.get(itemPosition).hasTemperature())){
                temperatureTextView.setText(weatherInfos.get(itemPosition).getTemperatureInCelsiusInt()+"°");
            }
            if (dayTextView != null){
                dayTextView.setText(simpleDateFormat.format(new Date(weatherInfos.get(itemPosition).getTimestamp())));
            }
            if ((windImageView != null) && (weatherInfos.get(itemPosition).hasWindDirection())){
                windImageView.setImageBitmap(weatherInfos.get(itemPosition).getWindSymbol(context,WeatherSettings.getWindDisplayType(context),false));
                windImageView.setColorFilter(Color.WHITE,PorterDuff.Mode.SRC_IN);
            }
            if ((windTextView != null) && (weatherInfos.get(itemPosition).hasWindSpeed())){
                final String windspeed;
                switch (WeatherSettings.getWindDisplayUnit(context)) {
                    case Weather.WindDisplayUnit.METERS_PER_SECOND:
                        windspeed = String.valueOf(weatherInfos.get(itemPosition).getWindSpeedInMsInt()) + ' ';
                        break;
                    case Weather.WindDisplayUnit.BEAUFORT:
                        windspeed = String.valueOf(weatherInfos.get(itemPosition).getWindSpeedInBeaufortInt()) + ' ';
                        break;
                    case Weather.WindDisplayUnit.KILOMETERS_PER_HOUR:
                        windspeed = String.valueOf(weatherInfos.get(itemPosition).getWindSpeedInKmhInt()) + ' ';
                        break;
                    case Weather.WindDisplayUnit.KNOTS:
                        windspeed = String.valueOf(weatherInfos.get(itemPosition).getWindSpeedInKnotsInt()) + ' ';
                        break;
                    default:
                        windspeed = "";
                }
                final StringBuilder windstring = new StringBuilder();
                windstring.append(windspeed).append(Weather.getWindUnitString(WeatherSettings.getWindDisplayUnit(context)));
                if (weatherInfos.get(itemPosition).hasFlurries()) {
                    switch (WeatherSettings.getWindDisplayUnit(context)) {
                        case Weather.WindDisplayUnit.METERS_PER_SECOND:
                            windstring.append(" (");
                            windstring.append(weatherInfos.get(itemPosition).getFlurriesInMsInt());
                            windstring.append(")");
                            break;
                        case Weather.WindDisplayUnit.BEAUFORT:
                            windstring.append(" (");
                            windstring.append(weatherInfos.get(itemPosition).getFlurriesInBeaufortInt());
                            windstring.append(")");
                            break;
                        case Weather.WindDisplayUnit.KILOMETERS_PER_HOUR:
                            windstring.append(" (");
                            windstring.append(weatherInfos.get(itemPosition).getFlurriesInKmhInt());
                            windstring.append(")");
                            break;
                        case Weather.WindDisplayUnit.KNOTS:
                            windstring.append(" (");
                            windstring.append(weatherInfos.get(itemPosition).getFlurriesInKnotsInt());
                            windstring.append(")");
                            break;
                    }
                }
                windTextView.setText(windstring.toString());
            }
            if ((clouds1!=null) && (weatherInfos.get(itemPosition).hasClouds_Nh())){
                clouds1.setText(weatherInfos.get(itemPosition).getClouds_Nh()+"%");
            }
            if ((clouds2!=null) && (weatherInfos.get(itemPosition).hasClouds_Nm())){
                clouds2.setText(weatherInfos.get(itemPosition).getClouds_Nm()+"%");
            }
            if ((clouds3!=null) && (weatherInfos.get(itemPosition).hasClouds_Nl())){
                clouds3.setText(weatherInfos.get(itemPosition).getClouds_Nl()+"%");
            }
        }

    }

    public Integer get6hPosition(int position1h){
        long targetTime = weatherInfos.get(position1h).getTimestamp();
        int position = 0;
        while ((weatherInfos6h.get(position).getTimestamp()<targetTime) && (position<weatherInfos6h.size()-1)){
            position++;
        }
        if (position<weatherInfos6h.size()-1){
            return position;
        }
        return null;
    }

    public Integer get6hPixelOffset(int position6h){
        int position = 0;
        while ((weatherInfos.get(position).getTimestamp()<weatherInfos6h.get(position6h).getTimestamp()) && (position<weatherInfos.size()-1)){
            position++;
        }
        if (position<weatherInfos.size()-1){
            return position*valueSet.XSCALE;
        }
        return null;
    }

    public WeatherSliderView(Context context, WindowManager windowManager, CurrentWeatherInfo currentWeatherInfo, int popUpWidth, int popupHeight) {
        super(context);
        this.context = context;
        this.windowManager = windowManager;
        //this.weatherInfos = currentWeatherInfo.forecast1hourly;
        //this.weatherInfos6h = currentWeatherInfo.forecast6hourly;
        this.weatherInfos = new ArrayList<Weather.WeatherInfo>();
        for (int i=0; i<currentWeatherInfo.forecast1hourly.size(); i++){
            if (currentWeatherInfo.forecast1hourly.get(i).getTimestamp()>= Calendar.getInstance().getTimeInMillis()){
                this.weatherInfos.add(currentWeatherInfo.forecast1hourly.get(i));
            }
        }
        this.weatherInfos6h = new ArrayList<Weather.WeatherInfo>();
        for (int i=0; i<currentWeatherInfo.forecast6hourly.size(); i++){
            if (currentWeatherInfo.forecast6hourly.get(i).getTimestamp()>= Calendar.getInstance().getTimeInMillis()){
                this.weatherInfos6h.add(currentWeatherInfo.forecast6hourly.get(i));
            }
        }
        this.weatherLocation = WeatherSettings.getSetStationLocation(context);
        this.valueSet = new ValueSet(popUpWidth,popupHeight);
        this.valueSet.popupWidth = popUpWidth;
        this.valueSet.popupHeight = popupHeight;
        this.valueSet.iconSize = valueSet.popupWidth/3;
        if (valueSet.popupWidth>valueSet.popupHeight){
            this.valueSet.isLandscape = true;
        } else {
            this.valueSet.isLandscape = false;
        }
        setItems(popUpWidth);
    }

    public WeatherSliderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WeatherSliderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public void setViews(TextView dayTextView, TextView temperatureTextView, ImageView windImageView, TextView windTextView, ImageView labellingImageView, TextView clouds1, TextView clouds2, TextView clouds3) {
        this.dayTextView = dayTextView;
        this.temperatureTextView = temperatureTextView;
        this.windImageView = windImageView;
        this.windTextView = windTextView;
        this.labellingImageView = labellingImageView;
        this.clouds1 = clouds1;
        this.clouds2 = clouds2;
        this.clouds3 = clouds3;
    }

    public boolean isScrollable(){
        return isScrollable;
    }

    private Bitmap getCloudBitmap(Context context, int cloudID){
        // set default resource to not available;
        int resource = R.mipmap.not_available;
        resource = WeatherIcons.getIconResource(context,cloudID);
        final int key = Objects.hash(resource);
        Bitmap bitmap = BITMAP_CACHE.get(key);
        if (bitmap == null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(context.getResources(), resource, options);
            options.inSampleSize = Math.round(valueSet.cloudsImageinSampleSize);
            options.inJustDecodeBounds = false;
            bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), resource, options),
                    Math.round(options.outWidth*valueSet.cloudsImageXSampleFactor),
                    Math.round(options.outHeight*valueSet.cloudsImageYSampleFactor),
                    true);
            /*
            Log.v("twfg","-----------------------------");
            Log.v("twfg","cloudsItemWidth        :"+valueSet.cloudsImageWidth);
            Log.v("twfg","cloudsItemHeight       :"+valueSet.cloudsImageHight);
            Log.v("twfg","cloudsImageinSampleSize:"+valueSet.cloudsImageinSampleSize);
            Log.v("twfg","inSampleSize           :"+options.inSampleSize);
            Log.v("twfg","Bitmap original        :"+options.outWidth+"x"+options.outHeight);
            Log.v("twfg","Bitmap result          :"+bitmap.getWidth()+"x"+bitmap.getHeight());
             */
            BITMAP_CACHE.put(key, bitmap);
        }
        return bitmap;
    }
    public static void clearBitmapCache() {
        BITMAP_CACHE.clear();
    }

    private int getProbImageView(int i){
        switch (i){
            case 1: return R.id.hzitem_var1_symbol;
            case 2: return R.id.hzitem_var2_symbol;
            case 3: return R.id.hzitem_var3_symbol;
            case 4: return R.id.hzitem_var4_symbol;
            case 5: return R.id.hzitem_var5_symbol;
            case 6: return R.id.hzitem_var6_symbol;
        }
        return 0;
    }

    private int getProbTextView(int i){
        switch (i){
            case 1: return R.id.hzitem_var1_text;
            case 2: return R.id.hzitem_var2_text;
            case 3: return R.id.hzitem_var3_text;
            case 4: return R.id.hzitem_var4_text;
            case 5: return R.id.hzitem_var5_text;
            case 6: return R.id.hzitem_var6_text;
        }
        return 0;
    }

    private void setProbValue(View view, int symbol, int position, int value, boolean applyThemeFilter){
        ImageView imageView = (ImageView) view.findViewById(getProbImageView(position));
        if (imageView!=null){
            imageView.setImageBitmap(WeatherIcons.getIconBitmap(context,symbol,false,applyThemeFilter));
        }
        TextView textView = (TextView) view.findViewById(getProbTextView(position));
        if (textView!=null){
            textView.setText(value+"%");
        }
    }

    private float calcTempY(double temperature){
        return (float) (valueSet.imageHeight-(temperature-valueSet.minTemperatureKelvin)*valueSet.pixelPerDegree)-valueSet.paddingY;
    }

    private float calcRainY(int probability){
        return (float) (valueSet.imageHeight-(probability*valueSet.pixelPerProb)-valueSet.paddingY);
    }

    private void drawDawn(Canvas canvas, float y0, float y1){
        //LinearGradient linearGradient = new LinearGradient(y0,0,y1,valueSet.imageHeight, ValueSet.COLOR.Night,ValueSet.COLOR.Sky, Shader.TileMode.CLAMP);
        LinearGradient linearGradient = new LinearGradient(y0,0,y1,0, ValueSet.COLOR.Night,ValueSet.COLOR.Sky, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setAlpha(255);
        paint.setShader(linearGradient);
        canvas.drawRect(y0,0,y1,valueSet.imageHeight,paint);
    }

    private void drawDusk(Canvas canvas, float y0, float y1){
        LinearGradient linearGradient = new LinearGradient(y1,0,y0,0, ValueSet.COLOR.Night,ValueSet.COLOR.Sky, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setAlpha(255);
        paint.setShader(linearGradient);
        canvas.drawRect(y0,0,y1,valueSet.imageHeight,paint);
    }

    private static class CloudType{
        private static final int LOW = 0;
        private static final int MID = 1;
        private static final int HIGH = 2;
    }

    private int getCloudName(int cloudType, int number){
        if (cloudType==CloudType.LOW){
            if (number==0){
                return WeatherIcons.CLOUDS_LOW1;
            }
            if (number==1){
                return WeatherIcons.CLOUDS_LOW2;
            }
            if (number==2){
                return WeatherIcons.CLOUDS_LOW3;
            }
            if (number==3){
                return WeatherIcons.CLOUDS_LOW4;
            }
        }
        if (cloudType==CloudType.MID){
            if (number==0){
                return WeatherIcons.CLOUDS_MID1;
            }
            if (number==1){
                return WeatherIcons.CLOUDS_MID2;
            }
            if (number==2){
                return WeatherIcons.CLOUDS_MID3;
            }
            if (number==3){
                return WeatherIcons.CLOUDS_MID4;
            }
        }
        if (number==0){
            return WeatherIcons.CLOUDS_HIGH1;
        }
        if (number==1){
            return WeatherIcons.CLOUDS_HIGH2;
        }
        if (number==2){
            return WeatherIcons.CLOUDS_HIGH3;
        }
        return WeatherIcons.CLOUDS_HIGH4;
    }

    private void drawCloud(Canvas canvas, int cloudType, int position6h, int count){
        Random random = new Random();
        int yoffset = Math.round(valueSet.cloudsOffsetHigh);
        if (cloudType == CloudType.MID){
            yoffset = Math.round(valueSet.cloudsOffsetMid);
        }
        if (cloudType == CloudType.LOW){
            yoffset = Math.round(valueSet.cloudsOffsetLow);
        }
        for (int i=0; i<count; i++){
            int x = random.nextInt(Math.round(valueSet.cloudsImageWidth)) + get6hPixelOffset(position6h);
            int y = random.nextInt(Math.round(valueSet.cloudsImageHight/2)) + yoffset + Math.round(valueSet.cloudsImageHight/4);
            int cloudPicture = random.nextInt(4);
            Bitmap cloudBitmap = getCloudBitmap(context,getCloudName(cloudType,cloudPicture));
            // center clouds, allow overlap
            x = x - cloudBitmap.getWidth()/2;
            if (x<0){
                x = 0;
            }
            if (x+cloudBitmap.getWidth()>valueSet.imageWidth){
                x = valueSet.imageWidth - cloudBitmap.getWidth();
            }
            Paint cloudPaint = new Paint();
            canvas.drawBitmap(cloudBitmap,x,y,cloudPaint);
        }
    }

    public void setItems(int popUpWidth) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout mainLayout = (RelativeLayout) layoutInflater.inflate(R.layout.sliderview,null);
        RelativeLayout.LayoutParams mainLayoutParams = (RelativeLayout.LayoutParams) mainLayout.getLayoutParams();
        addView(mainLayout);
        ImageView imageView = mainLayout.findViewById(R.id.sliderview_image);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        layoutParams.setMargins(popUpWidth/2,0,popUpWidth/2,0);
        Bitmap bitmap = Bitmap.createBitmap(valueSet.imageWidth,valueSet.imageHeight, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(ValueSet.COLOR.Sky);
        Canvas canvas = new Canvas(bitmap);
        // paints
        Path temperaturePath = new Path();
        Path rainPath = new Path();
        Paint temperaturePaint = new Paint();
        temperaturePaint.setColor(ThemePicker.getColor(context,ThemePicker.ThemeColor.RED));
        temperaturePaint.setStyle(Paint.Style.STROKE);
        temperaturePaint.setAntiAlias(true);
        temperaturePaint.setStrokeWidth(5);
        temperaturePaint.setShadowLayer(5,5,5,Color.BLACK);
        Paint rainPaint = new Paint();
        rainPaint.setColor(ThemePicker.getColor(context,ThemePicker.ThemeColor.BLUE));
        rainPaint.setStyle(Paint.Style.STROKE);
        rainPaint.setAntiAlias(true);
        rainPaint.setStrokeWidth(5);
        rainPaint.setShadowLayer(5,5,5,Color.BLACK);
        Paint labelTextPaint = new Paint();
        labelTextPaint.setColor(Color.WHITE);
        labelTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        labelTextPaint.setTextSize(valueSet.labelTextSize);
        labelTextPaint.setAntiAlias(true);
        // determine night
        ArrayList<Long> dawnStarts  = new ArrayList<Long>();
        ArrayList<Long> dawnStops   = new ArrayList<Long>();
        ArrayList<Long> duskStarts  = new ArrayList<Long>();
        ArrayList<Long> duskStops   = new ArrayList<Long>();
        // insert dummy set at the beginning, will mostly be not visible
        long fakeDayTime = weatherInfos.get(0).getTimestamp()-24*60*60*1000; // -24h
        Astronomy.Riseset riseset = Weather.getRiseset(weatherLocation,fakeDayTime);
        long dawnStart = Weather.getCivilTwilightMorning(riseset,fakeDayTime)-valueSet.timeXOffset;
        long dawnStop  = Weather.getSunriseInUTC(riseset,fakeDayTime)-valueSet.timeXOffset;
        long duskStart = Weather.getSunsetInUTC(riseset, fakeDayTime)-valueSet.timeXOffset;
        long duskStop  = Weather.getCivilTwilightEvening(riseset,fakeDayTime)-valueSet.timeXOffset;
        dawnStarts.add(dawnStart); dawnStops.add(dawnStop); duskStarts.add(duskStart); duskStops.add(duskStop);
        // calculate times from database entries
        for (int dayProbe=0; dayProbe<weatherInfos.size(); dayProbe=dayProbe+24){
            riseset = Weather.getRiseset(weatherLocation,weatherInfos.get(dayProbe).getTimestamp());
            dawnStart = Weather.getCivilTwilightMorning(riseset,weatherInfos.get(dayProbe).getTimestamp())-valueSet.timeXOffset;
            dawnStop  = Weather.getSunriseInUTC(riseset,weatherInfos.get(dayProbe).getTimestamp())-valueSet.timeXOffset;
            duskStart = Weather.getSunsetInUTC(riseset, weatherInfos.get(dayProbe).getTimestamp())-valueSet.timeXOffset;
            duskStop  = Weather.getCivilTwilightEvening(riseset,weatherInfos.get(dayProbe).getTimestamp())-valueSet.timeXOffset;
            dawnStarts.add(dawnStart); dawnStops.add(dawnStop); duskStarts.add(duskStart); duskStops.add(duskStop);
            fakeDayTime = dayProbe;
        }
        // insert dummy set at the end, will mostly be not visible
        fakeDayTime = weatherInfos.get((int) fakeDayTime).getTimestamp() +24*60*60*1000; // +24h
        riseset = Weather.getRiseset(weatherLocation,fakeDayTime);
        dawnStart = Weather.getCivilTwilightMorning(riseset,fakeDayTime)-valueSet.timeXOffset;
        dawnStop  = Weather.getSunriseInUTC(riseset,fakeDayTime)-valueSet.timeXOffset;
        duskStart = Weather.getSunsetInUTC(riseset, fakeDayTime)-valueSet.timeXOffset;
        duskStop  = Weather.getCivilTwilightEvening(riseset,fakeDayTime)-valueSet.timeXOffset;
        dawnStarts.add(dawnStart); dawnStops.add(dawnStop); duskStarts.add(duskStart); duskStops.add(duskStop);
        for (int i=0; i<dawnStarts.size(); i++){
            float x = (float) (dawnStarts.get(i)/valueSet.millisPerPixel);
            drawDawn(canvas,(float) (dawnStarts.get(i)/valueSet.millisPerPixel),(float) (dawnStops.get(i)/valueSet.millisPerPixel));
            //canvas.drawLine(x,0,x,valueSet.imageHeight,labelTextPaint);
        }
        for (int i=0; i<dawnStops.size(); i++){
            float x = (float) (dawnStops.get(i)/valueSet.millisPerPixel);
            //canvas.drawLine(x,0,x,valueSet.imageHeight,labelTextPaint);
        }
        for (int i=0; i<duskStarts.size(); i++){
            float x = (float) (duskStarts.get(i)/valueSet.millisPerPixel);
            drawDusk(canvas,(float) (duskStarts.get(i)/valueSet.millisPerPixel),(float) (duskStops.get(i)/valueSet.millisPerPixel));
            //canvas.drawLine(x,0,x,valueSet.imageHeight,labelTextPaint);
        }
        for (int i=0; i<duskStops.size(); i++){
            float x = (float) (duskStops.get(i)/valueSet.millisPerPixel);
            //canvas.drawLine(x,0,x,valueSet.imageHeight,labelTextPaint);
        }
        Paint nightPaint = new Paint();
        nightPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        nightPaint.setColor(ValueSet.COLOR.Night);
        nightPaint.setAlpha(255);
        Paint cloudPaint = new Paint();
        Random random = new Random();
        Bitmap starBitmap = getCloudBitmap(context,WeatherIcons.STAR1);
        for (int i=0; i<duskStops.size()-1; i++){
            if (i<dawnStarts.size()){
                canvas.drawRect((float) (duskStops.get(i)/valueSet.millisPerPixel),0f,(float) (dawnStarts.get(i+1)/valueSet.millisPerPixel),valueSet.imageHeight,nightPaint);
                int nightWidth = (int) Math.round(dawnStarts.get(i+1)/valueSet.millisPerPixel - duskStops.get(i)/valueSet.millisPerPixel);
                for (int star=0; star<30; star++){
                    int starX = (int) Math.round(duskStops.get(i)/valueSet.millisPerPixel + random.nextInt(nightWidth));
                    if (starX+starBitmap.getWidth()>Math.round(duskStops.get(i)/valueSet.millisPerPixel+nightWidth)){
                        starX = (int) (Math.round(duskStops.get(i)/valueSet.millisPerPixel+nightWidth) - starBitmap.getWidth());
                    }
                    int starY = random.nextInt(valueSet.imageHeight);
                    canvas.drawBitmap(starBitmap,starX,starY,cloudPaint);
                }
            }
        }
        // clouds
        for (int position=0; position<weatherInfos6h.size(); position++) {
            int cloudCoverHigh = weatherInfos6h.get(position).getClouds_Nh();
            int cloudCoverMid = weatherInfos6h.get(position).getClouds_Nm();
            int cloudCoverLow = weatherInfos6h.get(position).getClouds_Nl();
            /*
            int tcc = 75;
            drawCloud(canvas,CloudType.HIGH,position,tcc/4);
            drawCloud(canvas,CloudType.MID,position,tcc/8);
            drawCloud(canvas,CloudType.LOW,position,tcc/16);
             */
            drawCloud(canvas,CloudType.HIGH,position,cloudCoverHigh/4);
            drawCloud(canvas,CloudType.MID,position,cloudCoverMid/8);
            drawCloud(canvas,CloudType.LOW,position,cloudCoverLow/16);
        }

        float x = 0;
        for (int i=0; i<weatherInfos.size()-2; i++){
            float temperature_y1=calcTempY(weatherInfos.get(i).getTemperature());
            float temperature_y2=calcTempY(weatherInfos.get(i+1).getTemperature());
            if (i==0){
                temperaturePath.moveTo(x,temperature_y1);
            }
            //canvas.drawLine(x,temperature_y1,x+valueSet.XSCALE,temperature_y2,temperaturePaint);
            temperaturePath.quadTo(x,temperature_y1,x+valueSet.XSCALE,temperature_y2);
            float rain_y1 = calcRainY(weatherInfos.get(i).getProbPrecipitation());
            float rain_y2 = calcRainY(weatherInfos.get(i+1).getProbPrecipitation());
            if (i==0){
                rainPath.moveTo(x,rain_y1);
            }
            //canvas.drawLine(x,rain_y1,x+valueSet.XSCALE,rain_y2,rainPaint);
            rainPath.quadTo(x,rain_y1,x+valueSet.XSCALE,rain_y2);
            x=x+valueSet.XSCALE;
        }
        canvas.drawPath(temperaturePath,temperaturePaint);
        canvas.drawPath(rainPath,rainPaint);
        imageView.setImageBitmap(bitmap);
    }

    public void setLabelImage(){
        // draw labels
        Bitmap bitmap = Bitmap.createBitmap(Math.round(valueSet.paddingX),Math.round(valueSet.imageHeight), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        bitmap.eraseColor(Color.TRANSPARENT);
        Paint labelTextPaint = new Paint();
        labelTextPaint.setColor(Color.WHITE);
        labelTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        labelTextPaint.setTextSize(valueSet.labelTextSize);
        labelTextPaint.setAntiAlias(true);
        for (int i=(int) Math.round(valueSet.minTemperatureKelvin); i<valueSet.maxTemperatureKelvin; i++){
            if ((i-Math.round(Weather.KelvinConstant))%5==0){
                int temperatureCelsius = (int) (i-Math.round(Weather.KelvinConstant));
                String s = temperatureCelsius+"°";
                int x = Math.round(valueSet.paddingX - labelTextPaint.measureText(s));
                canvas.drawText(s,x,calcTempY(i),labelTextPaint);
            }
        }
        if (labellingImageView != null){
            labellingImageView.setImageBitmap(bitmap);
        }

    }

}

