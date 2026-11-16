package com.nmnvan.extension.moonsift.patches;

import android.util.Log;
import android.webkit.WebView;

@SuppressWarnings("unused")
public final class ProfileHeaderPatch {

    private static final String TAG = "NeilMorphe";

    /**
     * The home screen is moonsift.com's profile page in a WebView. Its header (avatar, name,
     * share button, @username) is {@code .Profile-heading}. {@code .Profile-tabs} (Saved,
     * Bookmarked, Purchased) is a later sibling that is only rendered on your own profile, so the
     * {@code :has} check leaves other people's profiles alone.
     * <p>
     * The site is a single-page app, so a style element in the head stays applied across
     * in-app navigation.
     */
    private static final String SCRIPT = "(function(){"
            + "var id='neil-morphe-hide-profile-heading';"
            + "if(document.getElementById(id))return;"
            + "var s=document.createElement('style');"
            + "s.id=id;"
            + "s.textContent='.Profile-heading:has(~ .Profile-tabs){display:none !important;}';"
            + "(document.head||document.documentElement).appendChild(s);"
            + "})();";

    private ProfileHeaderPatch() {
    }

    /**
     * Injection point: start of LoggedInComposeables$CustomWebViewClient.onPageFinished().
     */
    public static void onPageFinished(WebView view) {
        if (view == null) return;
        try {
            view.evaluateJavascript(SCRIPT, null);
        } catch (Exception ex) {
            Log.e(TAG, "Failed to hide profile header", ex);
        }
    }
}
