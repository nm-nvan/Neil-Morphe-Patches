package com.nmnvan.extension.moonsift.patches;

import android.net.Uri;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

@SuppressWarnings("unused")
public final class SanitizeLinksPatch {

    private static final String TAG = "NeilMorphe";

    private SanitizeLinksPatch() {
    }

    /**
     * Injection point: before every {@code new Intent(action, uri)} and
     * {@code CustomTabsIntent.launchUrl(context, uri)} in the app.
     *
     * @return a cleaned Uri, or the same instance if nothing changed.
     */
    public static Uri sanitize(Uri uri) {
        if (uri == null) return null;
        try {
            if (!isHttp(uri)) return uri;

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

    /**
     * Injection point: start of the add-to-collection browser's
     * WebViewClient.shouldOverrideUrlLoading(). That WebView loads links in place, so affiliate
     * redirects are swapped for the clean link there.
     * <p>
     * Only redirector links are handled. Plain store links are left alone, so a site that
     * redirects to add its own parameters cannot cause a reload loop.
     *
     * @return true if the load was replaced and the original should be cancelled.
     */
    public static boolean overrideUrlLoading(WebView view, WebResourceRequest request) {
        if (view == null || request == null || !request.isForMainFrame()) return false;
        try {
            Uri uri = request.getUrl();
            if (uri == null || !isHttp(uri)) return false;

            String original = uri.toString();
            if (!LinkSanitizer.isRedirector(original)) return false;

            String cleaned = LinkSanitizer.sanitize(original);
            if (cleaned == null || cleaned.equals(original)) return false;

            Log.d(TAG, "Sanitized WebView link: " + original + " -> " + cleaned);
            view.loadUrl(cleaned);
            return true;
        } catch (Exception ex) {
            Log.e(TAG, "Failed to sanitize WebView link", ex);
            return false;
        }
    }

    private static boolean isHttp(Uri uri) {
        String scheme = uri.getScheme();
        return "http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme);
    }
}
