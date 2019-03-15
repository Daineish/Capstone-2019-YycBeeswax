package com.example.dainemcniven.yycbeeswaxcapstone;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener
{
    public int m_hour = -1;
    public int m_minute = -1;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog tpd = new TimePickerDialog(getActivity(), this,
                hour, minute, DateFormat.is24HourFormat(getActivity()));

        tpd.setOnShowListener(new DialogInterface.OnShowListener()
        {

            @Override
            public void onShow(DialogInterface dialog) {
                    ((TimePickerDialog)dialog).getButton(TimePickerDialog.BUTTON_NEGATIVE).setEnabled(false);
            }
        });
        return tpd;
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute)
    {
        // Do something with the time chosen by the user
        m_hour = hourOfDay;
        m_minute = minute;
    }

    @Override
    public void onDismiss(final DialogInterface dialog)
    {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener)
        {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }
}