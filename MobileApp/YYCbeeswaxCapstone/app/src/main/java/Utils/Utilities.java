package Utils;

import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.dainemcniven.yycbeeswaxcapstone.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public final class Utilities
{
    private Utilities()
    {
        throw new UnsupportedOperationException();
    }

    public static ArrayList<Integer> GetAllHiveIds()
    {
        ResultSet hives = Database.getInstance().GetHives();
        ArrayList<Integer> rv = new ArrayList<>();
        try
        {
            while(hives.next())
            {
                rv.add(hives.getInt("HiveId"));
            }
        }
        catch(SQLException e){}
        return rv;
    }

    public static void SQLRequestFromServer(String request)
    {

    }
}
