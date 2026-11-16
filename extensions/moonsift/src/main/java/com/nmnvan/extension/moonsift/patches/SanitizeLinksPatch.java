package com.nmnvan.extension.moonsift.patches;

import android.net.Uri;
import android.util.Log;

@SuppressWarnings("unused")
public final class SanitizeLinksPatch {

    private static final String TAG = "NeilMorphe";

    private SanitizeLinksPatch() {
    }

    /**
     * Injection point: before every {@code new Intent(action, uri)} and
     * {@code CustomTabsIntent.launchUrl(context, uri)} in the app.
     */
    public static Uri sanitize(Uri uri) {
        if (uri == null) return null;
        try {
            String scheme = uri.getScheme();
            if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) return uri;

            String original = uri.toString();
            String cleaned = LinkSanitizer.sanitize(original);
            if (cleaned == null || cleaned.equals(original)) return uri;

            Log.d(TAG, "Sanitized link: " + original + " -> " + cleaned);
            return Uri.parse(cleaned);
        } catch (Exception ex) {
            Log.e(TAG, "Failed to sanitize link", ex);
            return uri;
        }
    }
}
