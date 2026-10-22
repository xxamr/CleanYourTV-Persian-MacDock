package com.gathin.tvhome;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.TextUtils;
import android.util.Xml;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** RSS ticker: DPAD navigation, original-article links, topics, sources and off switch. */
final class NewsTicker {
    private static final long REFRESH_MS = 5L * 60 * 1000;
    private static final long ADVANCE_MS = 13000;
    private static final int MAX_PER_FEED = 9;
    private static final int MAX_TOTAL = 24;
    private static final int MAX_BYTES = 512 * 1024;
    private static final String PREF = "persian_news_ticker_v1";
    private static final String KEY_SOURCES = "enabled_sources";
    private static final String KEY_TOPICS = "enabled_topics";
    private static final String KEY_ENABLED = "ticker_enabled";
    private static final String KEY_CACHE = "cached_articles_v2";
    private static final String[] TOPIC_NAMES = {
            "ایران و جهان", "اقتصاد و بازار", "فناوری و هوش مصنوعی", "خودرو"
    };

    private static final Source[] SOURCES = {
            new Source("bbc", "بی‌بی‌سی فارسی", "بی‌بی‌سی", "https://feeds.bbci.co.uk/persian/rss.xml", 0),
            new Source("dw", "دویچه‌وله فارسی", "دویچه‌وله", "https://rss.dw.com/syndication/feeds/dwworld.per.xml", 0),
            new Source("isna", "ایسنا", "ایسنا", "https://www.isna.ir/rss", 1),
            new Source("khabar", "خبرآنلاین", "خبرآنلاین", "https://www.khabaronline.ir/rss", 1),
            new Source("zoomit", "زومیت", "زومیت", "https://www.zoomit.ir/feed/", 2),
            new Source("khodrobank", "خودروبانک", "خودروبانک", "https://www.khodrobank.com/RSS/RSS.ashx", 3)
    };

    private static final class Source {
        final String id, label, shortName, feed;
        final int topic;
        Source(String id, String label, String shortName, String feed, int topic) {
            this.id = id; this.label = label; this.shortName = shortName;
            this.feed = feed; this.topic = topic;
        }
    }

    private static final class Article {
        final String source, title, link;
        final int topic;
        Article(String source, String title, String link, int topic) {
            this.source = source; this.title = title; this.link = link; this.topic = topic;
        }
    }

    private final MainActivity activity;
    private final SharedPreferences prefs;
    private final Handler main = new Handler(Looper.getMainLooper());
    private final ExecutorService io = Executors.newSingleThreadExecutor();
    private final LinearLayout panel;
    private final TextView title;
    private final TextView counter;
    private final ArrayList<Article> articles = new ArrayList<>();
    private boolean active, destroyed, fetching, userPaused;
    private int cursor, generation;
    private final Runnable refresh = new Runnable() {
        @Override public void run() {
            if (!active || destroyed || !enabled()) return;
            loadFeeds();
            main.postDelayed(this, REFRESH_MS);
        }
    };
    private final Runnable advance = new Runnable() {
        @Override public void run() {
            if (!active || destroyed) return;
            if (enabled() && !panel.hasFocus() && !userPaused && articles.size() > 1) {
                cursor = (cursor + 1) % articles.size();
                render();
            }
            main.postDelayed(this, ADVANCE_MS);
        }
    };

    NewsTicker(MainActivity activity, FrameLayout root) {
        this.activity = activity;
        prefs = activity.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        panel = new LinearLayout(activity);
        panel.setOrientation(LinearLayout.HORIZONTAL);
        panel.setGravity(Gravity.CENTER_VERTICAL);
        panel.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        panel.setPadding(dp(8), dp(2), dp(8), dp(2));
        panel.setFocusable(true);
        panel.setClickable(true);
        panel.setContentDescription("تلکس خبری؛ چپ خبر بعدی، راست قبلی، OK متن خبر");
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xC71A2436);
        bg.setCornerRadius(dp(14));
        bg.setStroke(dp(1), 0x52FFFFFF);
        panel.setBackground(bg);

        TextView badge = new TextView(activity);
        badge.setText("تلکس");
        badge.setTextSize(14);
        badge.setGravity(Gravity.CENTER);
        badge.setTextColor(Color.WHITE);
        GradientDrawable red = new GradientDrawable();
        red.setColor(0xFFC93642);
        red.setCornerRadius(dp(8));
        badge.setBackground(red);
        panel.addView(badge, new LinearLayout.LayoutParams(dp(62), dp(30)));

        title = new TextView(activity);
        title.setTextColor(0xFFF0F5FF);
        title.setTextSize(17);
        title.setSingleLine(true);
        title.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        title.setMarqueeRepeatLimit(-1);
        title.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        title.setTextDirection(View.TEXT_DIRECTION_RTL);
        title.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        title.setPadding(dp(12), 0, dp(8), 0);
        panel.addView(title, new LinearLayout.LayoutParams(0, dp(40), 1));

        counter = new TextView(activity);
        counter.setTextSize(12);
        counter.setTextColor(0xFFB5C6E0);
        counter.setGravity(Gravity.CENTER);
        panel.addView(counter, new LinearLayout.LayoutParams(dp(68), dp(34)));

        int screen = activity.getResources().getDisplayMetrics().widthPixels;
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                Math.round(screen * .84f), dp(47), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        lp.bottomMargin = dp(236);
        root.addView(panel, lp);
        panel.setOnFocusChangeListener((v, focused) -> {
            userPaused = focused;
            panel.setAlpha(focused ? 1f : .93f);
            render();
        });
        panel.setOnClickListener(v -> openArticle());
        panel.setOnKeyListener((v, key, event) -> {
            if (event.getAction() != KeyEvent.ACTION_DOWN) return false;
            switch (key) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    step(1); return true;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    step(-1); return true;
                case KeyEvent.KEYCODE_DPAD_UP:
                    View top = activity.findViewById(R.id.btnNewsTicker);
                    if (top != null) top.requestFocus();
                    return true;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                case KeyEvent.KEYCODE_BACK:
                    activity.restoreDockFocusForNews();
                    return true;
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    userPaused = !userPaused;
                    render();
                    return true;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_ENTER:
                    openArticle(); return true;
                default: return false;
            }
        });
        readCache();
        render();
        applyEnabled();
    }

    private int dp(float n) {
        return Math.round(n * activity.getResources().getDisplayMetrics().density);
    }

    boolean focusTicker() {
        if (!enabled() || panel.getVisibility() != View.VISIBLE) return false;
        return panel.requestFocus();
    }

    void onResume() {
        if (destroyed) return;
        active = true;
        applyEnabled();
        if (!enabled()) return;
        main.removeCallbacks(refresh);
        main.removeCallbacks(advance);
        main.post(refresh);
        main.postDelayed(advance, ADVANCE_MS);
        title.setSelected(!panel.hasFocus());
    }

    void onPause() {
        active = false;
        generation++;
        main.removeCallbacks(refresh);
        main.removeCallbacks(advance);
        title.setSelected(false);
    }

    void destroy() {
        onPause();
        destroyed = true;
        io.shutdownNow();
    }

    private boolean enabled() { return prefs.getBoolean(KEY_ENABLED, true); }

    private Set<String> selectedSources() {
        if (prefs.contains(KEY_SOURCES)) {
            return new HashSet<>(prefs.getStringSet(KEY_SOURCES, new HashSet<String>()));
        }
        HashSet<String> all = new HashSet<>();
        for (Source s : SOURCES) all.add(s.id);
        return all;
    }

    private Set<String> selectedTopics() {
        if (prefs.contains(KEY_TOPICS)) {
            return new HashSet<>(prefs.getStringSet(KEY_TOPICS, new HashSet<String>()));
        }
        HashSet<String> all = new HashSet<>();
        for (int i = 0; i < TOPIC_NAMES.length; i++) all.add(String.valueOf(i));
        return all;
    }

    private void applyEnabled() {
        panel.setVisibility(enabled() ? View.VISIBLE : View.GONE);
        if (!enabled()) {
            main.removeCallbacks(refresh);
            main.removeCallbacks(advance);
        }
    }

    // Kept as a compatibility alias with the existing top-bar OnClick callback.
    void showSourcesDialog() { showSettingsDialog(); }

    private void showSettingsDialog() {
        String[] entries = {
                enabled() ? "خاموش‌کردن تلکس" : "روشن‌کردن تلکس",
                "انتخاب منابع خبری", "انتخاب موضوعات", "تازه‌سازی فوری"
        };
        new AlertDialog.Builder(activity)
                .setTitle("تنظیمات تلکس خبری")
                .setItems(entries, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            prefs.edit().putBoolean(KEY_ENABLED, !enabled()).apply();
                            generation++;
                            applyEnabled();
                            if (active && enabled()) restartFetch();
                            break;
                        case 1: sourceDialog(); break;
                        case 2: topicDialog(); break;
                        case 3: restartFetch(); break;
                        default: break;
                    }
                })
                .setNegativeButton("بستن", null)
                .show();
    }

    private void sourceDialog() {
        String[] labels = new String[SOURCES.length];
        boolean[] checks = new boolean[SOURCES.length];
        Set<String> selected = selectedSources();
        for (int i = 0; i < SOURCES.length; i++) {
            labels[i] = SOURCES[i].label + " — " + TOPIC_NAMES[SOURCES[i].topic];
            checks[i] = selected.contains(SOURCES[i].id);
        }
        new AlertDialog.Builder(activity)
                .setTitle("منابع خبری")
                .setMultiChoiceItems(labels, checks, (dialog, which, value) -> checks[which] = value)
                .setPositiveButton("ذخیره", (dialog, which) -> {
                    HashSet<String> ids = new HashSet<>();
                    for (int i = 0; i < SOURCES.length; i++) if (checks[i]) ids.add(SOURCES[i].id);
                    prefs.edit().putStringSet(KEY_SOURCES, ids).apply();
                    reloadFilteredCache();
                    restartFetch();
                })
                .setNegativeButton("انصراف", null)
                .show();
    }

    private void topicDialog() {
        boolean[] checks = new boolean[TOPIC_NAMES.length];
        Set<String> selected = selectedTopics();
        for (int i = 0; i < checks.length; i++) checks[i] = selected.contains(String.valueOf(i));
        new AlertDialog.Builder(activity)
                .setTitle("موضوعات تلکس")
                .setMultiChoiceItems(TOPIC_NAMES, checks, (dialog, which, value) -> checks[which] = value)
                .setPositiveButton("ذخیره", (dialog, which) -> {
                    HashSet<String> topics = new HashSet<>();
                    for (int i = 0; i < checks.length; i++) if (checks[i]) topics.add(String.valueOf(i));
                    prefs.edit().putStringSet(KEY_TOPICS, topics).apply();
                    reloadFilteredCache();
                    restartFetch();
                })
                .setNegativeButton("انصراف", null)
                .show();
    }

    private void restartFetch() {
        generation++;
        main.removeCallbacks(refresh);
        if (active && enabled()) main.post(refresh);
    }

    private void step(int offset) {
        if (articles.isEmpty()) return;
        cursor = (cursor + offset + articles.size()) % articles.size();
        render();
    }

    private void render() {
        if (articles.isEmpty()) {
            title.setText(selectedTopics().isEmpty() || selectedSources().isEmpty()
                    ? "از تنظیمات تلکس، منبع و موضوع انتخاب کنید"
                    : "در حال دریافت خبرهای فارسی…");
            counter.setText("");
        } else {
            Article a = articles.get(Math.min(cursor, articles.size() - 1));
            title.setText(a.source + " | " + a.title);
            counter.setText((cursor + 1) + " / " + articles.size());
        }
        title.setSelected(active && !panel.hasFocus() && !userPaused);
    }

    private void openArticle() {
        if (articles.isEmpty()) return;
        String link = articles.get(Math.min(cursor, articles.size() - 1)).link;
        if (TextUtils.isEmpty(link)) {
            Toast.makeText(activity, "لینک این خبر در RSS موجود نیست", Toast.LENGTH_SHORT).show();
            return;
        }
        Uri uri = Uri.parse(link);
        if (!"https".equalsIgnoreCase(uri.getScheme()) || TextUtils.isEmpty(uri.getHost())) {
            Toast.makeText(activity, "لینک خبر معتبر نیست", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent browser = new Intent(Intent.ACTION_VIEW, uri);
        browser.addCategory(Intent.CATEGORY_BROWSABLE);
        browser.setPackage("com.internet.tvbrowser");
        try {
            activity.startActivity(browser);
        } catch (RuntimeException unavailable) {
            browser.setPackage(null);
            try { activity.startActivity(browser); }
            catch (RuntimeException none) {
                Toast.makeText(activity, "برنامه‌ای برای بازکردن لینک پیدا نشد", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void readCache() {
        articles.clear();
        String raw = prefs.getString(KEY_CACHE, "[]");
        try {
            JSONArray arr = new JSONArray(raw);
            Set<String> sources = selectedSources(), topics = selectedTopics();
            for (int i = 0; i < arr.length() && articles.size() < MAX_TOTAL; i++) {
                JSONObject item = arr.optJSONObject(i);
                if (item == null) continue;
                int topic = item.optInt("topic", -1);
                String id = item.optString("id", "");
                if (!sources.contains(id) || !topics.contains(String.valueOf(topic))) continue;
                String name = item.optString("source", "");
                String title = item.optString("title", "");
                String link = item.optString("link", "");
                if (!title.isEmpty()) articles.add(new Article(name, title, link, topic));
            }
        } catch (Exception ignored) { /* Invalid old cache is disposable. */ }
        cursor = 0;
    }

    private void reloadFilteredCache() {
        generation++;
        readCache();
        render();
    }

    private void saveCache(List<Article> batch, List<String> sourceIds) {
        try {
            JSONArray a = new JSONArray();
            for (int i = 0; i < batch.size(); i++) {
                Article article = batch.get(i);
                JSONObject obj = new JSONObject();
                obj.put("id", sourceIds.get(i));
                obj.put("source", article.source);
                obj.put("title", article.title);
                obj.put("link", article.link);
                obj.put("topic", article.topic);
                a.put(obj);
            }
            prefs.edit().putString(KEY_CACHE, a.toString()).apply();
        } catch (Exception ignored) { /* Cache must not crash Home. */ }
    }

    private void loadFeeds() {
        if (fetching) return;
        final Set<String> sources = selectedSources();
        final Set<String> topics = selectedTopics();
        if (sources.isEmpty() || topics.isEmpty()) {
            articles.clear();
            render();
            return;
        }
        fetching = true;
        final int request = generation;
        io.execute(() -> {
            try {
                ArrayList<ArrayList<Article>> batches = new ArrayList<>();
                ArrayList<ArrayList<String>> idBatches = new ArrayList<>();
                for (Source source : SOURCES) {
                    if (!sources.contains(source.id) || !topics.contains(String.valueOf(source.topic))) continue;
                    if (Thread.currentThread().isInterrupted()) break;
                    ArrayList<Article> one = readFeed(source);
                    ArrayList<String> ids = new ArrayList<>();
                    for (int k = 0; k < one.size(); k++) ids.add(source.id);
                    batches.add(one);
                    idBatches.add(ids);
                }
                ArrayList<Article> merged = new ArrayList<>();
                ArrayList<String> mergedIds = new ArrayList<>();
                Set<String> seen = new HashSet<>();
                for (int row = 0; row < MAX_PER_FEED && merged.size() < MAX_TOTAL; row++) {
                    for (int b = 0; b < batches.size() && merged.size() < MAX_TOTAL; b++) {
                        if (row >= batches.get(b).size()) continue;
                        Article a = batches.get(b).get(row);
                        if (!seen.add(a.title)) continue;
                        merged.add(a);
                        mergedIds.add(idBatches.get(b).get(row));
                    }
                }
                main.post(() -> {
                    fetching = false;
                    if (destroyed) return;
                    if (request != generation) {
                        if (active && enabled()) main.post(this::loadFeeds);
                        return;
                    }
                    if (!merged.isEmpty()) {
                        articles.clear();
                        articles.addAll(merged);
                        cursor = 0;
                        saveCache(merged, mergedIds);
                        if (active && enabled()) render();
                    } else if (articles.isEmpty() && active && enabled()) {
                        title.setText("دسترسی به منابع RSS برقرار نشد؛ اتصال اینترنت را بررسی کنید");
                        counter.setText("");
                    }
                });
            } catch (Exception ignored) {
                main.post(() -> {
                    fetching = false;
                    if (destroyed) return;
                    if (request != generation && active && enabled()) main.post(this::loadFeeds);
                    else if (active) render();
                });
            }
        });
    }

    private ArrayList<Article> readFeed(Source src) {
        ArrayList<Article> out = new ArrayList<>();
        HttpURLConnection conn = null;
        try {
            URL url = new URL(src.feed);
            if (!"https".equalsIgnoreCase(url.getProtocol())) return out;
            conn = (HttpURLConnection) url.openConnection();
            conn.setInstanceFollowRedirects(false);
            conn.setConnectTimeout(5500);
            conn.setReadTimeout(6500);
            conn.setRequestProperty("User-Agent", "CleanYourTV-RSS/2.0 (Android TV)");
            conn.setRequestProperty("Accept", "application/rss+xml, application/atom+xml, application/xml, text/xml");
            int code = conn.getResponseCode();
            if (code < 200 || code >= 300 || !"https".equalsIgnoreCase(conn.getURL().getProtocol())) return out;
            byte[] data;
            try (InputStream input = conn.getInputStream(); ByteArrayOutputStream buf = new ByteArrayOutputStream()) {
                byte[] part = new byte[4096];
                int n;
                while ((n = input.read(part)) != -1) {
                    if (buf.size() + n > MAX_BYTES) return out;
                    buf.write(part, 0, n);
                }
                data = buf.toByteArray();
            }
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_DOCDECL, false);
            parser.setInput(new ByteArrayInputStream(data), null);
            boolean inItem = false;
            String title = null, link = null;
            int type;
            while ((type = parser.nextToken()) != XmlPullParser.END_DOCUMENT && out.size() < MAX_PER_FEED) {
                if (type == XmlPullParser.DOCDECL) return new ArrayList<>();
                if (type == XmlPullParser.START_TAG) {
                    String tag = parser.getName().toLowerCase(Locale.ROOT);
                    if (tag.equals("item") || tag.equals("entry")) {
                        inItem = true;
                        title = null;
                        link = null;
                    } else if (inItem && tag.equals("title") && title == null) {
                        title = parser.nextText();
                    } else if (inItem && tag.equals("link") && link == null) {
                        String href = parser.getAttributeValue(null, "href");
                        String rel = parser.getAttributeValue(null, "rel");
                        if (href != null) {
                            if (rel == null || rel.equalsIgnoreCase("alternate")) link = href;
                        } else {
                            link = parser.nextText();
                        }
                    }
                } else if (type == XmlPullParser.END_TAG) {
                    String tag = parser.getName().toLowerCase(Locale.ROOT);
                    if (inItem && (tag.equals("item") || tag.equals("entry"))) {
                        if (title != null) {
                            String clean = Html.fromHtml(title, Html.FROM_HTML_MODE_LEGACY).toString()
                                    .replaceAll("[\\p{Cntrl}\\p{Cf}]+", " ").replaceAll("\\s+", " ").trim();
                            if (clean.length() > 160) clean = clean.substring(0, 160) + "…";
                            if (clean.length() > 8 && belongs(src.topic, clean)) {
                                out.add(new Article(src.shortName, clean, normalizeLink(link), src.topic));
                            }
                        }
                        inItem = false;
                    }
                }
            }
        } catch (Exception ignored) {
            // Feed errors are isolated from the Android main thread.
        } finally {
            if (conn != null) conn.disconnect();
        }
        return out;
    }

    private String normalizeLink(String raw) {
        if (TextUtils.isEmpty(raw)) return "";
        try {
            Uri uri = Uri.parse(raw.trim());
            return "https".equalsIgnoreCase(uri.getScheme()) && !TextUtils.isEmpty(uri.getHost())
                    ? uri.toString() : "";
        } catch (Exception ignored) { return ""; }
    }

    private boolean belongs(int topic, String title) {
        if (topic != 1) return true;
        String[] tokens = {"اقتصاد", "بازار", "بورس", "ارز", "دلار", "طلا", "سکه", "بانک", "تورم",
                "مسکن", "مالیات", "سرمایه", "تجارت", "گمرک", "نفت", "گاز", "قیمت",
                "حقوق", "صادرات", "واردات", "کسب", "مالی", "تولید", "معیشت"};
        for (String s : tokens) if (title.contains(s)) return true;
        return false;
    }
}
