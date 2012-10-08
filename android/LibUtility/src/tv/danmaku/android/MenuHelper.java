package tv.danmaku.android;

import android.view.Menu;
import android.view.MenuItem;

public class MenuHelper {
    public static void setItemVisible(Menu menu, int itemId, boolean visible) {
        MenuItem item = menu.findItem(itemId);
        if (item != null)
            item.setVisible(visible);
    }

    public static boolean expandActionView(MenuItem item) {
        if (BuildHelper.isApi14_IceCreamSandwichOrLater()) {
            item.expandActionView();
            return true;
        }

        return false;
    }
}
