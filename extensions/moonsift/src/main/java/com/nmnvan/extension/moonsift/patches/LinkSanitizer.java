package com.nmnvan.extension.moonsift.patches;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Turns an affiliate or tracking link into the plain store link.
 * <p>
 * moonsift.com wraps product links in redirectors (Skimlinks, VigLink, digidip) and adds its own
 * Amazon tag. This unwraps the redirectors and removes tracking query parameters.
 * <p>
 * Plain Java with no Android classes, so it can be tested on a desktop JVM.
 */
public final class LinkSanitizer {

    private static final int MAX_UNWRAP_DEPTH = 5;

    /** Query parameters that are never needed to show the page. */
    private static final Set<String> TRACKING_PARAMS = new HashSet<>(Arrays.asList(
            // Ad click ids.
            "gclid", "gclsrc", "gbraid", "wbraid", "dclid", "fbclid", "msclkid", "ttclid", "twclid",
            "li_fat_id", "yclid", "srsltid",
            // Mailchimp, Google Analytics, HubSpot, Instagram.
            "mc_cid", "mc_eid", "_ga", "_gl", "_hsenc", "_hsmi", "igshid",
            // Affiliate networks (Impact, CJ, Rakuten, ShareASale, Awin, Skimlinks, Pepperjam).
            "irclickid", "irgwc", "cjevent", "cjdata", "ranmid", "raneaid", "ransiteid", "sscid",
            "awc", "xcust", "sref", "skimlinks", "clickid", "epik", "affid", "aff_id"
    ));

    /** Extra parameters removed only on Amazon links (affiliate tag and referral data). */
    private static final Set<String> AMAZON_PARAMS = new HashSet<>(Arrays.asList(
            "tag", "linkcode", "linkid", "ascsubtag", "creative", "creativeasin", "camp", "ref_",
            "pd_rd_w", "pd_rd_wg", "pd_rd_r", "pd_rd_i", "pf_rd_p", "pf_rd_r",
            "content-id", "qid", "sr", "sprefix", "crid", "dib", "dib_tag"
    ));

    /** Parameters, in order of preference, that hold the real link on a redirector. */
    private static final String[] TARGET_PARAMS = {"url", "u", "murl", "ued", "urllink", "dest", "destination"};

    private LinkSanitizer() {
    }

    /**
     * @return the cleaned link, or the input unchanged if it is not an http(s) link or anything fails.
     */
    public static String sanitize(String url) {
        if (url == null) return null;
        try {
            String current = url.trim();
            for (int i = 0; i < MAX_UNWRAP_DEPTH; i++) {
                String target = unwrap(current);
                if (target == null) break;
                current = target;
            }
            return stripTracking(current);
        } catch (Exception ex) {
            return url;
        }
    }

    /**
     * @return true if [url] is an affiliate redirector link that {@link #sanitize} would unwrap.
     */
    public static boolean isRedirector(String url) {
        if (url == null) return false;
        try {
            return unwrap(url.trim()) != null;
        } catch (Exception ex) {
            return false;
        }
    }

    // ---------------------------------------------------------------------------------------------

    /** @return the link a redirector points to, or null if [url] is not a known redirector. */
    private static String unwrap(String url) {
        Parts parts = Parts.parse(url);
        if (parts == null || !isRedirector(parts.host, parts.path)) return null;

        for (String name : TARGET_PARAMS) {
            String value = parts.queryValue(name);
            if (value == null || value.isEmpty()) continue;

            String lower = value.toLowerCase(Locale.ROOT);
            if (lower.startsWith("http://") || lower.startsWith("https://")) return value;
            // ShareASale and some others omit the scheme.
            if (lower.startsWith("www.") || lower.matches("^[a-z0-9.-]+\\.[a-z]{2,}/.*")) return "https://" + value;
        }
        return null;
    }

    private static boolean isRedirector(String host, String path) {
        return host.equals("go.skimresources.com")
                || host.equals("go.redirectingat.com")
                || host.equals("redirect.viglink.com")
                || host.equals("redirect.sovrn.com")
                || (host.endsWith(".digidip.net") && path.startsWith("/visit"))
                || host.equals("click.linksynergy.com")
                || host.endsWith("awin1.com")
                || (host.endsWith("shareasale.com") && path.startsWith("/r.cfm"))
                || host.equals("l.facebook.com")
                || host.equals("lm.facebook.com")
                || host.equals("l.instagram.com")
                || host.equals("out.reddit.com")
                || host.equals("href.li");
    }

    private static String stripTracking(String url) {
        Parts parts = Parts.parse(url);
        if (parts == null || parts.query == null || isMoonsift(parts.host)) return url;

        boolean amazon = isAmazon(parts.host);
        StringBuilder query = new StringBuilder();
        for (String pair : parts.query.split("&")) {
            if (pair.isEmpty()) continue;
            int eq = pair.indexOf('=');
            String name = decode(eq >= 0 ? pair.substring(0, eq) : pair).toLowerCase(Locale.ROOT);
            if (name.startsWith("utm_") || TRACKING_PARAMS.contains(name)) continue;
            if (amazon && (AMAZON_PARAMS.contains(name) || name.startsWith("pd_rd_") || name.startsWith("pf_rd_"))) continue;
            if (query.length() > 0) query.append('&');
            query.append(pair);
        }

        StringBuilder out = new StringBuilder(parts.beforeQuery);
        if (query.length() > 0) out.append('?').append(query);
        if (parts.fragment != null) out.append('#').append(parts.fragment);
        return out.toString();
    }

    private static boolean isMoonsift(String host) {
        return host.equals("moonsift.com") || host.endsWith(".moonsift.com");
    }

    private static boolean isAmazon(String host) {
        return host.startsWith("amazon.") || host.contains(".amazon.") || host.equals("a.co") || host.equals("amzn.to");
    }

    private static String decode(String s) {
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException | IllegalArgumentException ex) {
            return s;
        }
    }

    /** A link split into the part before the query, the raw query and the raw fragment. */
    private static final class Parts {
        final String beforeQuery;
        final String query;
        final String fragment;
        final String host;
        final String path;

        private Parts(String beforeQuery, String query, String fragment, String host, String path) {
            this.beforeQuery = beforeQuery;
            this.query = query;
            this.fragment = fragment;
            this.host = host;
            this.path = path;
        }

        static Parts parse(String url) {
            String lower = url.toLowerCase(Locale.ROOT);
            int schemeEnd;
            if (lower.startsWith("https://")) schemeEnd = 8;
            else if (lower.startsWith("http://")) schemeEnd = 7;
            else return null;

            String rest = url;
            String fragment = null;
            int hash = rest.indexOf('#');
            if (hash >= 0) {
                fragment = rest.substring(hash + 1);
                rest = rest.substring(0, hash);
            }
            String query = null;
            int q = rest.indexOf('?');
            if (q >= 0) {
                query = rest.substring(q + 1);
                rest = rest.substring(0, q);
            }

            int pathStart = rest.indexOf('/', schemeEnd);
            String authority = pathStart >= 0 ? rest.substring(schemeEnd, pathStart) : rest.substring(schemeEnd);
            String path = pathStart >= 0 ? rest.substring(pathStart) : "/";
            int at = authority.lastIndexOf('@');
            if (at >= 0) authority = authority.substring(at + 1);
            int colon = authority.indexOf(':');
            if (colon >= 0) authority = authority.substring(0, colon);

            return new Parts(rest, query, fragment, authority.toLowerCase(Locale.ROOT), path.toLowerCase(Locale.ROOT));
        }

        /** @return the decoded value of the first parameter called [name], or null. */
        String queryValue(String name) {
            if (query == null) return null;
            for (String pair : query.split("&")) {
                int eq = pair.indexOf('=');
                if (eq < 0) continue;
                if (decode(pair.substring(0, eq)).equalsIgnoreCase(name)) return decode(pair.substring(eq + 1));
            }
            return null;
        }
    }
}
