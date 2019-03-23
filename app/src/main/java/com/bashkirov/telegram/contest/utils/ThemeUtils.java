package com.bashkirov.telegram.contest.utils;

import android.content.Context;
import android.util.TypedValue;

public class ThemeUtils {

    public static int getColorForAttrId(Context context, int attrId) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attrId, typedValue, true);
        return context.getResources().getColor(typedValue.resourceId);
    }
}