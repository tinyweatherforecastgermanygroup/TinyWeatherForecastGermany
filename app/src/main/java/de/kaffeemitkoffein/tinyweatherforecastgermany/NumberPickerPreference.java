package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;

public class NumberPickerPreference extends DialogPreference {

    private NumberPicker numberPicker;
    private int resultNumber;
    private Context context;

    public NumberPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected View onCreateDialogView() {
        if (context==null){
            context = getContext();
        }
        numberPicker = new NumberPicker(context);
        numberPicker.setMinValue(3);
        numberPicker.setMaxValue(10);
        numberPicker.setValue(getPersistedInt(10));
        return numberPicker;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult){
            resultNumber = numberPicker.getValue();
            notifyChanged();
            persistInt(resultNumber);
        }
        super.onDialogClosed(positiveResult);
    }
}
