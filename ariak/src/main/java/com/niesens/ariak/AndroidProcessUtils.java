package com.niesens.ariak;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;

public class AndroidProcessUtils {
    public static Drawable getIconFromPackageName(String packageName, Context context)
    {
        PackageManager pm = context.getPackageManager();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
        {
            try
            {
                PackageInfo pi = pm.getPackageInfo(packageName, 0);
                Context otherAppCtx = context.createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY);

                int displayMetrics[] = {DisplayMetrics.DENSITY_XHIGH, DisplayMetrics.DENSITY_HIGH, DisplayMetrics.DENSITY_TV};

                for (int displayMetric : displayMetrics)
                {
                    try
                    {
                        Drawable d = otherAppCtx.getResources().getDrawableForDensity(pi.applicationInfo.icon, displayMetric);
                        if (d != null)
                        {
                            return d;
                        }
                    }
                    catch (Resources.NotFoundException e)
                    {
//                      Log.d(TAG, "NameNotFound for" + packageName + " @ density: " + displayMetric);
                        continue;
                    }
                }

            }
            catch (Exception e)
            {
                // Handle Error here
            }
        }

        ApplicationInfo appInfo = null;
        try
        {
            appInfo = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            return null;
        }

        return appInfo.loadIcon(pm);
    }

    public static String getNameFromPackageName(String packageName, Context context) {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
        }
        if (applicationInfo != null) {
            return packageManager.getApplicationLabel(applicationInfo).toString();
        } else {
            return packageName;
        }

    }
}