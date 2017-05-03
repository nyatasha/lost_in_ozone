package msh.nasa.hackathon17.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Method;


/**
 * @author mohsen_shahini on 5/3/17.
 */

public class AndroidDevice {
    /**
     * The user-visible SDK version of the framework;
     * its possible values are defined in Build.VERSION_CODES.
     */
    public static final int API_VERSION = Build.VERSION.SDK_INT;

    /**
     * The name of the Android version represented by two or three
     * letters. Example: KK -> KitKat, KKW -> KitKat Watches etc.
     */
    public static final String API_VERSION_NAME_SHORT;

    static {
        switch (API_VERSION) {
            case Build.VERSION_CODES.ICE_CREAM_SANDWICH:
                API_VERSION_NAME_SHORT = "ICS";
                break;
            case Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1:
                API_VERSION_NAME_SHORT = "ICS1";
                break;
            case Build.VERSION_CODES.JELLY_BEAN:
                API_VERSION_NAME_SHORT = "JB";
                break;
            case Build.VERSION_CODES.JELLY_BEAN_MR1:
                API_VERSION_NAME_SHORT = "JB1";
                break;
            case Build.VERSION_CODES.JELLY_BEAN_MR2:
                API_VERSION_NAME_SHORT = "JB2";
                break;
            case Build.VERSION_CODES.KITKAT:
                API_VERSION_NAME_SHORT = "KK";
                break;
            case Build.VERSION_CODES.KITKAT_WATCH:
                API_VERSION_NAME_SHORT = "KKW";
                break;
            case Build.VERSION_CODES.LOLLIPOP:
                API_VERSION_NAME_SHORT = "LP";
                break;
            case Build.VERSION_CODES.LOLLIPOP_MR1:
                API_VERSION_NAME_SHORT = "LP1";
                break;
            case Build.VERSION_CODES.M:
                API_VERSION_NAME_SHORT = "M";
                break;
            case Build.VERSION_CODES.N:
                API_VERSION_NAME_SHORT = "N";
                break;
            default:
                API_VERSION_NAME_SHORT = "WTF";
        }
    }

    /**
     * @return {@code true} if device is device supports given API version,
     * {@code false} otherwise.
     */
    public static boolean hasTargetApi(int api) {
        return API_VERSION >= api;
    }

    /**
     * @return {@code true} if device is running
     * {@link Build.VERSION_CODES#M Nougat 7.0 (API24)} or higher,
     * {@code false} otherwise.
     */
    public static boolean hasNougatApi() {
        return hasTargetApi(Build.VERSION_CODES.N);
    }

    /**
     * @return {@code true} if device is running
     * {@link Build.VERSION_CODES#M Marshmallow 6.0 (API23)} or higher,
     * {@code false} otherwise.
     */
    public static boolean hasMarshmallowApi() {
        return hasTargetApi(Build.VERSION_CODES.M);
    }

    /**
     * @return {@code true} if device is running
     * {@link Build.VERSION_CODES#LOLLIPOP_MR1 Lollipop 5.1 (API22)} or higher,
     * {@code false} otherwise.
     */
    public static boolean hasLollipopMR1Api() {
        return hasTargetApi(Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    /**
     * @return {@code true} if device is running
     * {@link Build.VERSION_CODES#LOLLIPOP Lollipop (API21)} or higher,
     * {@code false} otherwise.
     */
    public static boolean hasLollipopApi() {
        return hasTargetApi(Build.VERSION_CODES.LOLLIPOP);
    }

    /**
     * @return {@code true} if device is running
     * {@link Build.VERSION_CODES#KITKAT_WATCH KitKat watch (API20)} or higher,
     * {@code false} otherwise.
     */
    public static boolean hasKitKatWatchApi() {
        return hasTargetApi(Build.VERSION_CODES.KITKAT_WATCH);
    }

    /**
     * @return {@code true} if device is running
     * {@link Build.VERSION_CODES#KITKAT KitKat (API19)} or higher,
     * {@code false} otherwise.
     */
    public static boolean hasKitKatApi() {
        return hasTargetApi(Build.VERSION_CODES.KITKAT);
    }

    /**
     * @return {@code true} if device is running
     * {@link Build.VERSION_CODES#JELLY_BEAN_MR2 Jelly Bean 4.3 (API18)} or higher,
     * {@code false} otherwise.
     */
    public static boolean hasJellyBeanMR2Api() {
        return hasTargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2);
    }

    /**
     * @return {@code true} if device is running
     * {@link Build.VERSION_CODES#JELLY_BEAN_MR1 Jelly Bean 4.2 (API17)} or higher,
     * {@code false} otherwise.
     */
    public static boolean hasJellyBeanMR1Api() {
        return hasTargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1);
    }

    /**
     * @return {@code true} if device is running
     * {@link Build.VERSION_CODES#JELLY_BEAN Jelly Bean 4.1 (API16)} or higher,
     * {@code false} otherwise.
     */
    public static boolean hasJellyBeanApi() {
        return hasTargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1);
    }

    /**
     * @return {@code true} if device is running
     * {@link Build.VERSION_CODES#ICE_CREAM_SANDWICH ICS 4.0 (API14)} or higher,
     * {@code false} otherwise.
     */
    public static boolean hasIcsApi() {
        return hasTargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH);
    }

    /**
     * UDID - Android Id
     *
     * @param c
     * @return
     */
    public static String getUDID(Context c) {
        String id = android.provider.Settings.System.getString(c.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        return id;
    }

    /**
     * Return device screen size
     *
     * @param context
     * @return
     */
    public static Point getScreenSize(final @NonNull Context context) {
        WindowManager wm = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        if (AndroidDevice.hasJellyBeanApi()) {
            //new pleasant way to get real metrics
            DisplayMetrics realMetrics = new DisplayMetrics();
            display.getRealMetrics(realMetrics);
            size.x = realMetrics.widthPixels;
            size.y = realMetrics.heightPixels;
        } else if (AndroidDevice.hasIcsApi()) {
            //reflection for this weird in-between time
            try {
                Method getRawH = Display.class.getMethod("getRawHeight");
                Method getRawW = Display.class.getMethod("getRawWidth");
                size.x = (Integer) getRawW.invoke(display);
                size.y = (Integer) getRawH.invoke(display);
            } catch (Exception e) {
                //this may not be 100% accurate, but it's all we've got
                size.x = display.getWidth();
                size.y = display.getHeight();
            }

        } else {
            //This should be close, as lower API devices should not have window navigation bars
            size.x = display.getWidth();
            size.y = display.getHeight();
        }
        return size;
    }

    /**
     * A combination of AndroidDevice's Manufacture and Model (with no repetition)
     *
     * @return
     */
    public static String getModelName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        String phrase = "";
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase += Character.toUpperCase(c);
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase += c;
        }
        return phrase;
    }

    /**
     * Check weather device has Software Navigation Bar.
     *
     * @param context
     * @return true if device has NavBar
     */
    public static boolean hasNavBar(Context context) {
        Resources resources = context.getResources();
        int id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            return resources.getBoolean(id);
        } else {    // Check for keys
            boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
            boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            return !hasMenuKey && !hasBackKey;
        }
    }

    public static void hideKeyboard(Activity activity) {
        final View view = activity.getCurrentFocus();
        if (view != null) {
            final InputMethodManager inputMethodManager = (InputMethodManager) activity.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showKeyboard(final Activity activity) {
        activity.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
                        | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                        | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
        );
        final InputMethodManager inputMethodManager = (InputMethodManager) activity.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
}
