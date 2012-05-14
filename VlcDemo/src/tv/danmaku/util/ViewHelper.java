package tv.danmaku.util;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ViewHelper {

    // View BackgroundColor
    public static void setViewBackgroundColor(View container, int id, int color) {
        if (container == null)
            return;

        container.findViewById(id).setBackgroundColor(color);
    }

    public static void setViewBackgroundDrawable(View container, int id,
            Drawable drawable) {
        if (container == null)
            return;

        container.findViewById(id).setBackgroundDrawable(drawable);
    }

    public static void setViewBackgroundResource(View container, int id,
            int resource) {
        if (container == null)
            return;

        container.findViewById(id).setBackgroundResource(resource);
    }

    // View Visibility
    public static void setViewVisibility(View container, int id, int visibility) {
        if (container == null)
            return;

        container.findViewById(id).setVisibility(visibility);
    }

    // View OnClickListener
    public static void setViewOnClickListener(View container, int id,
            OnClickListener listener) {
        if (container == null)
            return;

        container.findViewById(id).setOnClickListener(listener);
    }

    // ImageView Drawable
    public static void setViewDrawable(View container, int id, int drawableId) {
        if (container == null)
            return;

        setViewDrawable(container.findViewById(id), AppResources.getDrawable(container.getContext(), drawableId));
    }
    
    public static void setViewDrawable(View container, int id, Drawable drawable) {
        if (container == null)
            return;

        setViewDrawable(container.findViewById(id), drawable);
    }

    public static void setViewDrawable(View imageView, Drawable drawable) {
        ((ImageView) imageView).setImageDrawable(drawable);
    }

    public static void setViewDrawable(ImageView imageView, Drawable drawable) {
        imageView.setImageDrawable(drawable);
    }

    // TextView Text
    public static void setViewText(View container, int id, int resource) {
        if (container == null)
            return;

        setViewText(container, id, container.getContext().getString(resource));
    }

    public static void setViewText(View container, int id, String text) {
        if (container == null)
            return;

        setViewText(container.findViewById(id), text);
    }

    public static void setViewText(View textView, String text) {
        ((TextView) textView).setText(text);
    }

    public static void setViewText(TextView textView, String text) {
        textView.setText(text);
    }

    // TextView Text
    public static void setViewTypeface(View container, int id, Typeface face) {
        if (container == null)
            return;

        setViewTypeface(container.findViewById(id), face);
    }

    public static void setViewTypeface(View textView, Typeface face) {
        ((TextView) textView).setTypeface(face);
    }

    // ListView
    public static View getChildAtPosition(ListView listView, int position) {
        int firstPostion = listView.getFirstVisiblePosition();
        int lastPostion = listView.getLastVisiblePosition();

        if ((position < firstPostion) || (position > lastPostion)) {
            return null;
        }
        
        return listView.getChildAt(position - firstPostion + listView.getHeaderViewsCount());
    }
}
