package com.example.dainemcniven.yycbeeswaxcapstone;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener
{
    public int m_year = -1;
    public int m_month = -1;
    public int m_day = -1;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog dpd = new DatePickerDialog(getActivity(), this, year, month, day);
        dpd.setOnShowListener(new DialogInterface.OnShowListener()
        {

            @Override
            public void onShow(DialogInterface dialog) {
                ((DatePickerDialog)dialog).getButton(DatePickerDialog.BUTTON_NEGATIVE).setEnabled(false);
            }
        });
        return dpd;
    }

    public void onDateSet(DatePicker view, int year, int month, int day)
    {
        // Do something with the date chosen by the user
        m_year = year;
        m_month = month;
        m_day = day;
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