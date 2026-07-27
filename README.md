# Azusa

A local HTML dynamic hijacking framework for Android WebView. Intercepts all clicks at the capture phase, bypasses page-level listeners, and redirects to native operations.

## Features

- **Capture-phase hijacking** -- Uses `addEventListener('click', handler, true)` to intercept every click before page listeners fire
- **No source modification** -- Injects hijack script via `evaluateJavascript` in `onPageFinished`; original HTML stays untouched
- **Attribute-driven routing** -- Reads `data-azusa-action` and `data-azusa-data` from the target or its ancestors
- **Fallback defaults** -- Buttons and anchors without data attributes default to `navigate` action using `href` or `innerText`
- **WebViewAssetLoader** -- Loads assets over a virtual HTTPS domain; no `file://` exposure
- **Single bridge entry** -- `AzusaBridge.postMessage(action, data)` handles navigate, toast, finish, log

## Requirements

- Android 8.0 (API 26) or higher
- WebView 65+ (bundled with the system WebView)
- Dependency: `androidx.webkit:webkit:1.11.0`

## Quick Start

1. Clone the repository and open in Android Studio.
2. Place your HTML pages under `app/src/main/assets/`.
3. Define interactive elements with `data-azusa-action` attributes:

```html
<button data-azusa-action="toast" data-azusa-data="Hello">Show Toast</button>
<a href="https://example.com" data-azusa-action="navigate">Go</a>
```

4. Build and run.

## How It Works

1. `WebViewAssetLoader` serves files under `/assets/` via `https://appassets.androidplatform.net/assets/`.
2. After each page load, `onPageFinished` injects a capture-phase click listener.
3. On click, the script walks up the DOM tree looking for `data-azusa-action`. If found, it calls `preventDefault()` + `stopPropagation()` and invokes `Azusa.postMessage(action, data)`.
4. The native `AzusaBridge` dispatches the action: navigate (opens URL), toast (shows a toast), finish (closes activity), or log (prints to Logcat).
5. A `__azusa_hijacked__` flag prevents duplicate injection on the same page.

## License

MIT
