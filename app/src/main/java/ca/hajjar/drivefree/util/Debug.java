package ca.hajjar.drivefree.util;

import android.util.Log;

/**
 * Created by bilal on 2017-01-30.
 */

public class Debug {

    public static final boolean IS_DEBUG = false;

    public static final void d(Class objectClass, String msg){
        d(objectClass.getName(),msg);
    }
    public static final void d(String tag,String msg){
        if(IS_DEBUG){
            Log.d(tag, msg);
        }
    }

    public static final void e(Class objectClass, String msg){
        e(objectClass.getName(),msg);
    }
    public static final void e(String tag,String msg){
        if(IS_DEBUG){
            Log.e(tag, msg);
        }
    }

    public static final void i(Class objectClass, String msg){
        i(objectClass.getName(),msg);
    }
    public static final void i(String tag,String msg){
        if(IS_DEBUG){
            Log.i(tag, msg);
        }
    }

}
