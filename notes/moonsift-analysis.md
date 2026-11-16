# Moonsift analysis (v1.31)

Package `com.moonsift.wish.list.maker.christmas.birthday.baby.shopping.gift.list`, versionName `1.31`, single APK.
Kotlin / Compose Multiplatform app; all app code is in `classes5.dex` under `com.moonsift.app`. Class names are not obfuscated.

## Logged-in screen = moonsift.com in a WebView

`ui/loggedin/LoggedInComposeables` hosts a WebView that loads `https://www.moonsift.com` (plus last location / token).

- `LoggedInComposeables$CustomWebViewClient`
  - `shouldOverrideUrlLoading` always returns `true` and calls `LoggedInViewModel.onShouldOverrideUrlChange(url)`.
    URLs starting with the website stay in the WebView; anything else goes to `events.popup(url)`.
  - `onPageFinished(WebView, String)` injects the `ReactNativeWebView` bridge and a history listener.
- `ReactNativeWebViewInterface.postMessage` handles LOGOUT, NAVIGATE/Onboarding, HISTORY:, notifications, review. No "open URL" message.

## How outbound links leave the app

1. Web page link → `shouldOverrideUrlLoading` → `onShouldOverrideUrlChange` → `UiEventEmitter.popup`
   → `MainViewModel` emits `MainContract.Label.Popup(url)` → `MainActivity$bindLabels$1`:
   `startActivity(new Intent(ACTION_VIEW, Uri.parse(url)))`.
2. `util/launch/AndroidMoonLauncher.launchCollection/launchMoonsiftUrl` → `new Intent(ACTION_VIEW, uri)`.
3. `ui/components/TextComponents.HtmlText` link annotations → `CustomTabsIntent.launchUrl(context, uri)`.

The "Sanitize outbound links" patch rewrites the Uri before every `Intent.<init>(String, Uri)` and
`CustomTabsIntent.launchUrl(Context, Uri)` call in `Lcom/moonsift/` classes.

## Where the trackers come from (moonsift.com web bundle, `/assets/index-*.js`)

The product link component builds the href:
- `amazon.co.uk` → adds `tag=moonsift03-21`; `amazon.com` → adds `tag=moonsiftlive-20`.
- `commissionType === "sovrn"` → `http://redirect.viglink.com/?u=<url>&key=…&utm_content=…`.
- `*.temu.com` → `https://moonsiftcom.digidip.net/visit?url=<url>&ref=…`.
- Otherwise (unless the site is excluded or the user has `skimlinks_disabled`) →
  `https://go.skimresources.com/?id=126855X1588436&url=<url>&sref=<page>&xcust=…`.
- Plain `href` links → Skimlinks too.

## Profile header

Web component with stable class names (styled-components):
- `.Profile-heading`: avatar (`.Profile-avatar`), name + share/edit menu, bio, `@username`, profile link.
- `.Profile-sustainabilityPoll`, then `.Profile-tabs` (Saved / Bookmarked / Purchased), both only rendered on your own profile.
- `.Profile-collectionListContainer`.

The "Hide profile header" patch injects `.Profile-heading:has(~ .Profile-tabs){display:none}` from `onPageFinished`.
The style element survives the SPA's client-side navigation.
