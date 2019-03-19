package server;
/**
 * A utility class which holds some global variables and global helper functions.
 */
public class Utilities
{
    // Whether or not to print messages.
    public static final boolean g_printDebugMessage = true;

    // Constants for data types received from devices.
    public static final String g_tempHumidIrSensor = "THIR_SENSOR";

    /**
     * Asserts that b is true, if false it prints msg to System.err if thr is false,
     *     else it throws a new exception with msg.
     * @param b - the value to assert is true.
     * @param thr - whether or not to throw an exception.
     * @param msg - the message to print or throw
     * @return b
     * @throws Exception if b is false and thr is true
     */
    public static boolean AssertMessage(boolean b, boolean thr, String msg) throws Exception
    {
        if (!b)
        {
            if(thr)
            {
                throw new Exception("Assertion failed: " + msg);
            }
            else
            {
                System.err.println("Assertion failed: " + msg);
            }
        }
        return b;
    }

    /**
     * Debugging print. Only prints s if g_printDebugMessage is true.
     * @param s - the string to print
     */
    public static void PrintMessage(String s)
    {
        if(g_printDebugMessage)
        {
            System.out.println(s);
        }
    }
}
