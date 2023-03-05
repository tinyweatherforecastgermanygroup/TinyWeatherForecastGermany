package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

public class LEDColorPicker {

    int chosenColorItem=WeatherSettings.PREF_LED_COLOR_DEFAULT;
    private OnColorPickedListener onColorPickedListener;
    private Context context;

    public LEDColorPicker(Context context){
        this.context = context;
    }

    public interface OnColorPickedListener{
        void onColorSelected(int color);
    }

    public void setOnColorPickedListener(OnColorPickedListener onColorPickedListener){
        this.onColorPickedListener = onColorPickedListener;
    }

    private void updateColorDialogItem(final View view, final ImageView[] buttons, final ImageView[] checkmarks){
        chosenColorItem = (int) view.getTag();
        for (int i=0; i<16; i++){
            GradientDrawable gradientDrawable = (GradientDrawable) buttons[i].getDrawable();
            gradientDrawable.setStroke(16, Color.TRANSPARENT);
            checkmarks[i].setVisibility(View.INVISIBLE);
        }
        GradientDrawable gradientDrawable = (GradientDrawable) ((ImageView) view).getDrawable();
        gradientDrawable.setStroke(16, ThemePicker.getColor(context,ThemePicker.ThemeColor.ORANGE));
        checkmarks[chosenColorItem].setVisibility(View.VISIBLE);
    }

    public void show(){
        chosenColorItem = WeatherSettings.getLEDColorItem(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context,0);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View colorView = layoutInflater.inflate(R.layout.colordialog,null);
        final ImageView[] buttons = new ImageView[16];
        final ImageView[] checkmarks = new ImageView[16];

        final View.OnClickListener colorClickedListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateColorDialogItem(view,buttons,checkmarks);
            }
        };
        for (int i=0; i<16; i++){
            buttons[i] = colorView.findViewById(context.getResources().getIdentifier("colordialog_color"+i,"id",context.getPackageName()));
            buttons[i].setTag(i);
            buttons[i].setOnClickListener(colorClickedListener);
            checkmarks[i] = colorView.findViewById(context.getResources().getIdentifier("colordialog_checkmark"+i,"id",context.getPackageName()));
            GradientDrawable gradientDrawable = (GradientDrawable) buttons[i].getDrawable();
            if (i==chosenColorItem){
                gradientDrawable.setStroke(16, ThemePicker.getColor(context,ThemePicker.ThemeColor.ORANGE));
                checkmarks[i].setVisibility(View.VISIBLE);
            } else {
                gradientDrawable.setStroke(16, Color.TRANSPARENT);
            }
            gradientDrawable.setColor(WeatherSettings.NotificationLEDcolors[i]);
        }
        builder.setView(colorView);
        builder.setPositiveButton(R.string.alertdialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onColorPickedListener.onColorSelected(chosenColorItem);
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}
