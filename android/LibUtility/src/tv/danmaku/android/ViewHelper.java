package tv.danmaku.android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ViewHelper {

    // View BackgroundColor
    public static void setBackgroundColor(View container, int id, int color) {
        if (container == null)
            return;

        container.findViewById(id).setBackgroundColor(color);
    }

    @SuppressWarnings("deprecation")
    public static void setBackgroundDrawable(View container, int id,
            Drawable drawable) {
        if (container == null)
            return;

        container.findViewById(id).setBackgroundDrawable(drawable);
    }

    public static void setBackgroundResource(View container, int id,
            int resource) {
        if (container == null)
            return;

        container.findViewById(id).setBackgroundResource(resource);
    }

    /*--------------------------------------
     * View Visibility
     */
    public static void setVisibility(View container, int id, int visibility) {
        if (container == null)
            return;

        container.findViewById(id).setVisibility(visibility);
    }

    /*--------------------------------------
     * View OnClickListener
     */
    public static void setOnClickListener(View container, int id,
            OnClickListener listener) {
        if (container == null)
            return;

        container.findViewById(id).setOnClickListener(listener);
    }

    /*--------------------------------------
     * View OnTouchListener
     */
    public static void setOnTouchListener(View container, int id,
            View.OnTouchListener listener) {
        if (container == null)
            return;

        container.findViewById(id).setOnTouchListener(listener);
    }

    /*--------------------------------------
     * ImageView setImageDrawable
     */
    public static void setImageDrawable(View container, int id, int drawableId) {
        if (container == null)
            return;

        setImageDrawable(container.findViewById(id),
                ResourcesHelper.getDrawable(container.getContext(), drawableId));
    }

    public static void setImageDrawable(View container, int id,
            Drawable drawable) {
        if (container == null)
            return;

        setImageDrawable(container.findViewById(id), drawable);
    }

    public static void setImageDrawable(View imageView, Drawable drawable) {
        ((ImageView) imageView).setImageDrawable(drawable);
    }

    public static void setImageDrawable(ImageView imageView, Drawable drawable) {
        imageView.setImageDrawable(drawable);
    }

    /*--------------------------------------
     * TextView setText
     */
    public static void setText(Activity container, int id, int resource) {
        if (container == null)
            return;

        setText(container, id, container.getString(resource));
    }

    public static void setText(View container, int id, int resource) {
        if (container == null)
            return;

        setText(container, id, container.getContext().getString(resource));
    }

    public static void setText(Activity container, int id, CharSequence text) {
        if (container == null)
            return;

        setText(container.findViewById(id), text);
    }

    public static void setText(View container, int id, CharSequence text) {
        if (container == null)
            return;

        setText(container.findViewById(id), text);
    }

    public static void setText(View textView, CharSequence text) {
        ((TextView) textView).setText(text);
    }

    public static void setText(TextView textView, CharSequence text) {
        textView.setText(text);
    }

    /*--------------------------------------
     * TextView setTypeface
     */
    public static void setTypeface(View container, int id, Typeface face) {
        if (container == null)
            return;

        setTypeface(container.findViewById(id), face);
    }

    public static void setTypeface(View textView, Typeface face) {
        ((TextView) textView).setTypeface(face);
    }

    // ListView
    public static View getChildAtPosition(ListView listView, int position) {
        int firstPostion = listView.getFirstVisiblePosition();
        int lastPostion = listView.getLastVisiblePosition();

        if ((position < firstPostion) || (position > lastPostion)) {
            return null;
        }

        return listView.getChildAt(position - firstPostion
                + listView.getHeaderViewsCount());
    }

    @TargetApi(11)
    public static void setLayerType(View view, int layerType, Paint paint) {
        if (BuildHelper.isApi11_HoneyCombOrLater()) {
            view.setLayerType(layerType, paint);
        }
    }
}
