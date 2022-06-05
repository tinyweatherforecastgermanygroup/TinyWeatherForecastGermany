package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import java.util.ArrayList;
import java.util.Objects;

public class WeatherSliderView extends HorizontalScrollView {

    private Context context;
    private static final SparseArray<Bitmap> BITMAP_CACHE = new SparseArray<>();
    private Weather.WeatherLocation weatherLocation;
    private int displayWidth;
    private int displayHeight;
    private int iconSize;
    private CurrentWeatherInfo currentWeatherInfo;

    public WeatherSliderView(Context context, CurrentWeatherInfo currentWeatherInfo) {
        super(context);
        this.context = context;
        this.currentWeatherInfo = currentWeatherInfo;
        weatherLocation = WeatherSettings.getSetStationLocation(context);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        displayWidth  = Math.round(displayMetrics.widthPixels);
        displayHeight = Math.round(displayMetrics.heightPixels);
        iconSize = displayWidth/3;
        if (displayWidth>displayHeight){
            iconSize = displayHeight/3;
        }
    }

    public WeatherSliderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WeatherSliderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    private Bitmap getIconBitmap(Context context, Weather.WeatherInfo weatherInfo, int bitmapWidth, int bitmapHeight){
        // set default resource to not available;
        int resource = R.mipmap.not_available;
        if (weatherInfo.hasCondition()){
            // display always daytime
            resource = WeatherCodeContract.getWeatherConditionDrawableResource(context,weatherInfo.getCondition(),true);
            // calculate daytime precisely if location is set
            if (weatherLocation!=null){
                resource = WeatherCodeContract.getWeatherConditionDrawableResource(context,weatherInfo.getCondition(), weatherInfo.isDaytime(this.weatherLocation));
            }
        }

        final int key = Objects.hash(resource, bitmapHeight, bitmapWidth);
        Bitmap bitmap = BITMAP_CACHE.get(key);

        if (bitmap == null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(context.getResources(), resource, options);
            options.inSampleSize = ForecastAdapter.calculateInSampleSize(options,bitmapWidth,bitmapHeight);
            options.inJustDecodeBounds = false;
            bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), resource, options),bitmapWidth,
                    bitmapHeight,
                    true);
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

    public boolean setItems(int width, int height, ArrayList<Weather.WeatherInfo> weatherInfos) {
        final boolean isLandscape = width>height;
        LinearLayout linearLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayout.setLayoutParams(linearLayoutParams);
        addView(linearLayout);
        LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        int wholeScrollViewWidth = 0;
        for (int i = 0; i < weatherInfos.size(); i++) {
            Weather.WeatherInfo weatherInfo = weatherInfos.get(i);
            View newView = LayoutInflater.from(context).inflate(R.layout.horizontalinfoitem,linearLayout,false);
            // time
            TextView textViewHeading = (TextView) newView.findViewById(R.id.hzi_heading);
            textViewHeading.setText(Weather.GetDateString(Weather.SIMPLEDATEFORMATS.DETAILED_NO_SECONDS,weatherInfo.getTimestamp()));
            // weather icon
            ImageView weatherConditionImageView = (ImageView) newView.findViewById(R.id.hzi_weathericon);
            if (!weatherInfo.hasCondition()){
                weatherConditionImageView.setVisibility(INVISIBLE);
            } else {
                weatherConditionImageView.setImageBitmap(getIconBitmap(context,weatherInfo,iconSize,iconSize));
            }
            // temperature
            TextView textViewTemperature = newView.findViewById(R.id.hzi_temperature);
            if (!weatherInfo.hasTemperature()){
                textViewTemperature.setVisibility(INVISIBLE);
            } else {
                textViewTemperature.setText(weatherInfo.getTemperatureInCelsiusInt()+"°");
            }
            ImageView symbolTG = (ImageView) newView.findViewById(R.id.hzi_groundicon);
            TextView textViewTemperature5cm = (TextView) newView.findViewById(R.id.hzi_temperatureGround);
            if (!weatherInfo.hasTemperature5cm()){
                symbolTG.setVisibility(INVISIBLE);
                textViewTemperature5cm.setVisibility(INVISIBLE);
            } else {
                symbolTG.setColorFilter(ThemePicker.getColorTextLight(context), PorterDuff.Mode.SRC_IN);
                textViewTemperature5cm.setText(weatherInfo.getTemperature5cmInCelsiusInt()+"°");
            }
            // pressure
            TextView textViewPressure = (TextView) newView.findViewById(R.id.hzi_pressure);
            if (!weatherInfo.hasPressure()){
                textViewPressure.setVisibility(INVISIBLE);
            } else {
                textViewPressure.setText(weatherInfo.getPressure()/100+ " hPa");
            }
            // humidity
            ImageView symbolRH = (ImageView) newView.findViewById(R.id.hzi_humidityicon);
            TextView textViewRH = (TextView) newView.findViewById(R.id.hzi_humidity);
            if (!weatherInfo.hasRH()){
                symbolRH.setVisibility(INVISIBLE);
                textViewRH.setVisibility(INVISIBLE);
            } else {
                symbolRH.setColorFilter(ThemePicker.getColorTextLight(context), PorterDuff.Mode.SRC_IN);
                textViewRH.setText(weatherInfo.getRHInt()+"%");
            }
            // clouds
            ImageView symbolClouds = (ImageView) newView.findViewById(R.id.hzi_cloudsicon);
            TextView textViewClouds = (TextView) newView.findViewById(R.id.hzi_clouds);
            if (!weatherInfo.hasClouds()){
                symbolClouds.setVisibility(INVISIBLE);
                textViewClouds.setVisibility(INVISIBLE);
            } else {
                symbolClouds.setColorFilter(ThemePicker.getColorTextLight(context), PorterDuff.Mode.SRC_IN);
                textViewClouds.setText(weatherInfo.getRHInt()+"%");
            }
            // precipitation
            ImageView imageViewPrecipitation = (ImageView) newView.findViewById(R.id.hzi_precipitationicon);
            TextView textViewPrecipitation = (TextView) newView.findViewById(R.id.hzi_precipitation);
            TextView textViewPrecipitationUnit1 = (TextView) newView.findViewById(R.id.hzi_precipitation_unit1);
            TextView textViewPrecipitationUnit2 = (TextView) newView.findViewById(R.id.hzi_precipitation_unit2);
            if ((!weatherInfo.hasPrecipitation()) && (!weatherInfo.hasProbPrecipitation())){
                imageViewPrecipitation.setVisibility(GONE);
                textViewPrecipitation.setVisibility(GONE);
            } else {
                String precipitationText = "";
                if (weatherInfo.hasProbPrecipitation()){
                    precipitationText=weatherInfo.getProbPrecipitation()+"%";
                    if (weatherInfo.hasPrecipitation()){
                        precipitationText = precipitationText + ", ";
                    }
                }
                if (!weatherInfo.hasPrecipitation()) {
                    textViewPrecipitationUnit1.setVisibility(View.INVISIBLE);
                    textViewPrecipitationUnit2.setVisibility(View.INVISIBLE);
                } else {
                    precipitationText = precipitationText + weatherInfo.getPrecipitation();
                }
                textViewPrecipitation.setText(precipitationText);
            }
            // wind icon
            ImageView imageViewWind = (ImageView) newView.findViewById(R.id.hzi_windarrow);
            if (!weatherInfo.hasWindDirection()){
                imageViewWind.setVisibility(INVISIBLE);
            } else {
                if (WeatherSettings.getWindDisplayType(context)==Weather.WindDisplayType.ARROW){
                    if (WeatherSettings.getDisplayWindArc(context)) {
                        imageViewWind.setImageBitmap(Weather.WeatherInfo.getWindForecastTint(currentWeatherInfo.currentWeather.getBeaufortBitmap(context,true),currentWeatherInfo.getWindForecast(WeatherSettings.getWindArcPeriod(context))));
                    } else {
                        imageViewWind.setImageBitmap(currentWeatherInfo.currentWeather.getArrowBitmap(context,true));
                    }
                }
                if (WeatherSettings.getWindDisplayType(context)==Weather.WindDisplayType.BEAUFORT){
                    if (WeatherSettings.getDisplayWindArc(context)){
                        imageViewWind.setImageBitmap(Weather.WeatherInfo.getWindForecastTint(currentWeatherInfo.currentWeather.getBeaufortBitmap(context,true),currentWeatherInfo.getWindForecast(WeatherSettings.getWindArcPeriod(context))));
                    } else {
                        imageViewWind.setImageBitmap(currentWeatherInfo.currentWeather.getBeaufortBitmap(context,true));
                    }
                }
            }
            // wind values
            TextView textViewWindText = (TextView) newView.findViewById(R.id.hzi_wind);
            TextView textViewWindUnit = (TextView) newView.findViewById(R.id.hzi_windunit);
            String windstring = Weather.getWindString(context,currentWeatherInfo);
            if (windstring==null){
                textViewWindText.setVisibility(INVISIBLE);
                textViewWindUnit.setVisibility(INVISIBLE);
            } else {
                textViewWindText.setText(windstring);
                textViewWindUnit.setText(Weather.getWindUnitString(WeatherSettings.getWindDisplayUnit(context)));
            }
            // visibility
            ImageView imageViewVisibilityIcon = (ImageView) newView.findViewById(R.id.hzi_visibilityicon);
            TextView textViewVisibilityText = (TextView) newView.findViewById(R.id.hzi_visibility);
            if (!weatherInfo.hasVisibility()){
                imageViewVisibilityIcon.setVisibility(INVISIBLE);
                textViewVisibilityText.setVisibility(INVISIBLE);
            } else {
                imageViewVisibilityIcon.setColorFilter(ThemePicker.getColorTextLight(context), PorterDuff.Mode.SRC_IN);
                textViewVisibilityText.setText(ForecastAdapter.getVisibilityCharSequence(weatherInfo,WeatherSettings.getWindDisplayUnit(context)));
            }
            TextView textViewVisibilityBelow1km = (TextView) newView.findViewById(R.id.hzi_visibilitybelowprob);
            TextView textViewVisibilityBelow1kmUnit1 = (TextView) newView.findViewById(R.id.hzi_visibilitybelowprobunit1);
            TextView textViewVisibilityBelow1kmUnit2 = (TextView) newView.findViewById(R.id.hzi_visibilitybelowprobunit2);
            if (!weatherInfo.hasProbVisibilityBelow1km()){
                textViewVisibilityBelow1km.setVisibility(INVISIBLE);
                textViewVisibilityBelow1kmUnit1.setVisibility(INVISIBLE);
                textViewVisibilityBelow1kmUnit2.setVisibility(INVISIBLE);
            } else {
                textViewVisibilityBelow1km.setText(weatherInfo.getProbVisibilityBelow1km()+"%");
            }
            // calculate the space left for the charts
            LinearLayout topcontainer = (LinearLayout) newView.findViewById(R.id.hzi_topcontainer);
            topcontainer.measure(0,0);
            int topcontainerMeasuredHeight = topcontainer.getMeasuredHeight();
            int topcontainerMeasuredWidth  = topcontainer.getMeasuredWidth();
            RelativeLayout probcontainer = (RelativeLayout) newView.findViewById(R.id.hzi_probcontainer);
            int probcontainerMeasuredHight = 0;
            if (!isLandscape){
                probcontainer.measure(0,0);
                probcontainerMeasuredHight = probcontainer.getMeasuredHeight();
            }
            int chartWidth = topcontainerMeasuredWidth;
            int chartHeight = height - topcontainerMeasuredHeight - probcontainerMeasuredHight;
            // precipitation details
            ImageView imageViewPD = (ImageView) newView.findViewById(R.id.hzi_precipitationdetails);
            if (weatherInfo.hasPrecipitationDetails()){
                imageViewPD.setImageBitmap(ForecastBitmap.getPrecipitationChart(context, weatherInfo,Math.round(chartWidth*0.66f),chartHeight,isLandscape));
            } else {
                imageViewPD.setVisibility(GONE);
            }
            // cloud details
            ImageView imageViewCD = (ImageView) newView.findViewById(R.id.hzi_clouddetails);
            if (weatherInfo.clouds.hasHeightValues()){
                imageViewCD.setImageBitmap(ForecastBitmap.getCloudCoverChart(context, weatherInfo,Math.round(chartWidth*0.33f),chartHeight));
            } else {
                imageViewCD.setVisibility(GONE);
            }
            // add the probability displays
            int position = 1;
            // lightning
            if (weatherInfo.hasProbThunderstorms()){
                setProbValue(newView,WeatherIcons.SYMBOL_LIGHTNING,position,weatherInfo.getProbThunderStorms(),false);
                position++;
            }
            // hail
            if (weatherInfo.hasProbSolidPrecipitation()){
                setProbValue(newView,WeatherIcons.SYMBOL_HAIL,position,weatherInfo.getProbSolidPrecipitation(),true);
                position++;
            }
            // freezing rain
            if (weatherInfo.hasProbFreezingRain()){
                setProbValue(newView,WeatherIcons.SYMBOL_FREEZING_RAIN,position,weatherInfo.getProbFreezingRain(),false);
                position++;
            }
            // fog
            if (weatherInfo.hasProbFog()){
                setProbValue(newView,WeatherIcons.SYMBOL_FOG,position,weatherInfo.getProbFog(),true);
                position++;
            }
            // drizzle
            if (weatherInfo.hasProbDrizzle()){
                setProbValue(newView,WeatherIcons.SYMBOL_DRIZZLE,position,weatherInfo.getProbDrizzle(),true);
                position++;
            }
            // hide unused positions
            while (position<7){
                TextView probTextView   = (TextView) newView.findViewById(getProbTextView(position));
                ImageView probImageView = (ImageView) newView.findViewById(getProbImageView(position));
                if (probTextView!=null){
                    probTextView.setVisibility(GONE);
                }
                if (probImageView!=null){
                    probImageView.setVisibility(GONE);
                }
                position++;
            }
            // measure the new view
            newView.measure(0,0);
            int newViewWidth=newView.getMeasuredWidth();
            wholeScrollViewWidth = wholeScrollViewWidth + newViewWidth;
            // finally, add the view
            linearLayout.addView(newView);
        }
        if (wholeScrollViewWidth<width){
            View placeHolderView = LayoutInflater.from(context).inflate(R.layout.placeholder,linearLayout,false);
            placeHolderView.setLayoutParams(new LayoutParams(width-wholeScrollViewWidth,height));
            linearLayout.addView(placeHolderView);
            return false;
        }
        return true;
    }
}

