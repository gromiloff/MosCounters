/*
 * Copyright (C) 2016 Jared Rummler <jared.rummler@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package gromiloff.widget.materialspinner;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

final class Utils {

    /**
     * Darkens a color by a given factor.
     *
     * @param color  the color to darken
     * @param factor The factor to darken the color.
     * @return darker version of specified color.
     */
    static int darker(int color, float factor) {
        return Color.argb(Color.alpha(color), Math.max((int) (Color.red(color) * factor), 0),
                Math.max((int) (Color.green(color) * factor), 0),
                Math.max((int) (Color.blue(color) * factor), 0));
    }

    /**
     * Lightens a color by a given factor.
     *
     * @param color  The color to lighten
     * @param factor The factor to lighten the color. 0 will make the color unchanged. 1 will make the
     *               color white.
     * @return lighter version of the specified color.
     */
    static int lighter(int color, float factor) {
        int red = (int) ((Color.red(color) * (1 - factor) / 255 + factor) * 255);
        int green = (int) ((Color.green(color) * (1 - factor) / 255 + factor) * 255);
        int blue = (int) ((Color.blue(color) * (1 - factor) / 255 + factor) * 255);
        return Color.argb(Color.alpha(color), red, green, blue);
    }

    /**
     * Check if layout direction is RTL
     *
     * @param context the current context
     * @return {@code true} if the layout direction is right-to-left
     */
    static boolean isRtl(Context context) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 &&
                context.getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    /**
     * Return a drawable object associated with a particular resource ID.
     * <p>
     * <p>Starting in {@link Build.VERSION_CODES#LOLLIPOP}, the returned drawable will be styled for the
     * specified Context's theme.</p>
     *
     * @param id The desired resource identifier, as generated by the aapt tool.
     *           This integer encodes the package, type, and resource entry.
     *           The value 0 is an invalid identifier.
     * @return Drawable An object that can be used to draw this resource.
     */
    static Drawable getDrawable(Context context, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getDrawable(id);
        }
        return context.getResources().getDrawable(id);
    }

}
