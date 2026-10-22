package com.gathin.tvhome;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** TV remote-friendly macOS-style Dock with on-device favourite editing. */
final class FloatingDock {
    private static final String PREFS = "custom_mac_dock_v1";
    private static final String KEY = "favorite_packages";
    static final int MAX_FAVORITES = 8;
    private static final String[] DEFAULTS = {
        "com.google.android.youtube.tv", "com.internet.tvbrowser", "com.instagram.airwave",
        "tv.telewebion", "com.sabaidea.filimo.tv", "ir.filmnet.android.tv"
    };
    private final MainActivity activity;
    private final PackageManager pm;
    private final LinearLayout bar;
    private final TextView tooltip;
    private final ArrayList<ImageView> icons = new ArrayList<>();
    private final ArrayList<String> packages = new ArrayList<>();
    private final float d;
    private int lastFocusedIndex = 0;
    // Keep wave only while navigating. After idle, focused icon stays actionable.
    private static final long IDLE_COLLAPSE_MS = 3000L;
    private final Handler dockHandler = new Handler(Looper.getMainLooper());
    private Runnable idleCollapse;
    private final DecelerateInterpolator ease = new DecelerateInterpolator(1.65f);

    FloatingDock(MainActivity activity, FrameLayout root) {
        this.activity = activity;
        this.pm = activity.getPackageManager();
        this.d = activity.getResources().getDisplayMetrics().density;
        int screenWidth = activity.getResources().getDisplayMetrics().widthPixels;

        tooltip = new TextView(activity);
        tooltip.setTextColor(0xF2FFFFFF);
        tooltip.setTextSize(26f);
        tooltip.setTypeface(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD);
        tooltip.setShadowLayer(dp(2), 0f, dp(2), 0xCC000000);
        tooltip.setGravity(Gravity.CENTER);
        tooltip.setVisibility(View.INVISIBLE);
        FrameLayout.LayoutParams titleLp = new FrameLayout.LayoutParams(
                Math.round(screenWidth * .60f), dp(44), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        titleLp.bottomMargin = dp(165);
        root.addView(tooltip, titleLp);

        bar = new LinearLayout(activity);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setGravity(Gravity.CENTER);
        bar.setClipChildren(false);
        bar.setClipToPadding(false);
        bar.setPadding(dp(8), dp(10), dp(8), dp(10));
        GradientDrawable bg = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{0xBE47556D, 0xB824304A});
        bg.setCornerRadius(dp(25));
        bg.setStroke(dp(1), 0x55FFFFFF);
        bar.setBackground(bg);
        FrameLayout.LayoutParams dockLp = new FrameLayout.LayoutParams(
                Math.round(screenWidth * .60f), dp(78), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        dockLp.bottomMargin = dp(34);
        root.setClipChildren(false);
        root.setClipToPadding(false);
        root.addView(bar, dockLp);
        // Dock stays 60% wide, but animated icons can fan out into the free 20% margins.
        bar.setClipChildren(false);
        idleCollapse = () -> {
            // No focus change: only visual magnification and caption are hidden.
            tooltip.setVisibility(View.INVISIBLE);
            magnify(-1);
        };
        reloadFromPreferences();
    }

    static ArrayList<String> readPackages(Context context) {
        ArrayList<String> out = new ArrayList<>();
        String saved = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, null);
        if (saved != null) {
            try {
                JSONArray a = new JSONArray(saved);
                for (int i = 0; i < a.length() && out.size() < MAX_FAVORITES; i++) {
                    String pkg = a.optString(i, "");
                    if (!pkg.isEmpty() && !out.contains(pkg)) out.add(pkg);
                }
            } catch (JSONException ignored) { out.clear(); }
        }
        if (out.isEmpty()) {
            Collections.addAll(out, DEFAULTS);
        }
        return out;
    }

    static void savePackages(Context context, List<String> chosen) {
        JSONArray a = new JSONArray();
        for (String pkg : chosen) a.put(pkg);
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY, a.toString()).apply();
    }

    void reloadFromPreferences() {
        if (idleCollapse != null) dockHandler.removeCallbacks(idleCollapse);
        packages.clear();
        packages.addAll(readPackages(activity));
        lastFocusedIndex = Math.min(lastFocusedIndex, packages.size());
        tooltip.setVisibility(View.INVISIBLE);
        bar.removeAllViews();
        icons.clear();
        for (int index = 0; index < packages.size(); index++) {
            final String pkg = packages.get(index);
            ImageView icon = makeIcon();
            try { icon.setImageDrawable(pm.getApplicationIcon(pkg)); }
            catch (PackageManager.NameNotFoundException e) { icon.setImageResource(R.drawable.ic_apps); }
            final String name = packageLabel(pkg);
            addIcon(icon, name, () -> openApp(pkg));
        }
        ImageView launchpad = makeIcon();
        launchpad.setImageResource(R.drawable.ic_apps);
        addIcon(launchpad, "Launchpad", () -> LaunchpadScreen.show(
                activity, launchpad, () -> DockEditorScreen.show(activity, this, launchpad)));
        // Long OK on any Dock icon opens the same editor.
        for (ImageView icon : icons) {
            icon.setOnLongClickListener(v -> {
                DockEditorScreen.show(activity, this, v);
                return true;
            });
        }
    }

    private String packageLabel(String pkg) {
        try { return String.valueOf(pm.getApplicationLabel(pm.getApplicationInfo(pkg, 0))); }
        catch (PackageManager.NameNotFoundException e) { return pkg; }
    }

    private ImageView makeIcon() {
        ImageView icon = new ImageView(activity);
        icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        icon.setPadding(dp(2), dp(2), dp(2), dp(2));
        icon.setFocusable(true);
        icon.setClickable(true);
        icon.setId(View.generateViewId());
        return icon;
    }

    // Reserve the whole magnification wave inside a 60%-wide bar,
    // including when the user adds up to 8 favorites from the TV editor.
    private int compactIconWidth() {
        int count = Math.max(1, packages.size() + 1); // Launchpad included
        int barWidth = Math.round(activity.getResources().getDisplayMetrics().widthPixels * 0.60f);
        int room = barWidth - dp(16) - count * dp(2) - dp(12);
        return Math.min(dp(49), Math.max(dp(26), Math.round(room / (count + 3.0f))));
    }

    private void wakeMagnification(int index, String label) {
        dockHandler.removeCallbacks(idleCollapse);
        tooltip.setText(label);
        tooltip.setVisibility(View.VISIBLE);
        magnify(index);
        dockHandler.postDelayed(idleCollapse, IDLE_COLLAPSE_MS);
    }

    private void addIcon(ImageView icon, String label, Runnable action) {
        int index = icons.size();
        icon.setContentDescription(label);
        // Fixed compact slots, rather than weight=1 stretching icons across all 60%.
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(compactIconWidth(), dp(56));
        lp.leftMargin = dp(1);
        lp.rightMargin = dp(1);
        bar.addView(icon, lp);
        icons.add(icon);
        icon.setOnClickListener(v -> action.run());
        icon.setOnFocusChangeListener((v, focused) -> {
            if (focused) {
                lastFocusedIndex = index;
                wakeMagnification(index, label);
            } else if (!anyFocused()) {
                dockHandler.removeCallbacks(idleCollapse);
                tooltip.setVisibility(View.INVISIBLE);
                magnify(-1);
            }
        });
        icon.setOnKeyListener((v, key, event) -> {
            if (event.getAction() != KeyEvent.ACTION_DOWN) return false;
            if (key == KeyEvent.KEYCODE_DPAD_LEFT) {
                int next = Math.max(0, index - 1);
                if (next == index) wakeMagnification(index, label);
                icons.get(next).requestFocus();
                return true;
            }
            if (key == KeyEvent.KEYCODE_DPAD_RIGHT) {
                int next = Math.min(icons.size() - 1, index + 1);
                if (next == index) wakeMagnification(index, label);
                icons.get(next).requestFocus();
                return true;
            }
            if (key == KeyEvent.KEYCODE_DPAD_DOWN) return true;
            if (key == KeyEvent.KEYCODE_DPAD_UP) {
                if (activity.focusNewsTicker()) return true;
                View top = activity.findViewById(R.id.btnClockCustom);
                if (top != null && top.getVisibility() == View.VISIBLE) top.requestFocus();
                return true;
            }
            return false;
        });
    }

    private void openApp(String pkg) {
        Intent intent = pm.getLeanbackLaunchIntentForPackage(pkg);
        if (intent == null) intent = pm.getLaunchIntentForPackage(pkg);
        if (intent == null) {
            Toast.makeText(activity, "App unavailable: " + packageLabel(pkg), Toast.LENGTH_SHORT).show();
            return;
        }
        try { activity.startActivity(intent); }
        catch (RuntimeException e) {
            Toast.makeText(activity, "Can't open " + packageLabel(pkg), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean anyFocused() {
        for (ImageView v : icons) if (v.hasFocus()) return true;
        return false;
    }

    /**
     * Dock magnification wave. Each scaled icon gets extra horizontal room from its
     * neighbours; shift centres by half of their excess widths to avoid overlapping.
     * No child reordering: z-elevation alone chooses which icon paints on top.
     */
    private void magnify(int selected) {
        if (icons.isEmpty()) return;
        // Use actual compact icon slots; old formula assumed weight-based stretched slots.
        float slotWidth = icons.get(0).getWidth();
        if (slotWidth <= 0) slotWidth = compactIconWidth();
        float[] extra = new float[icons.size()];
        float[] scales = new float[icons.size()];
        float[] lifts = new float[icons.size()];
        float total = 0f;
        for (int i = 0; i < icons.size(); i++) {
            int distance = selected < 0 ? 99 : Math.abs(i - selected);
            float scale = distance == 0 ? 2.00f : distance == 1 ? 1.55f
                    : distance == 2 ? 1.24f : distance == 3 ? 1.07f : 1f;
            scales[i] = scale;
            lifts[i] = distance == 0 ? -dp(26) : distance == 1 ? -dp(15)
                    : distance == 2 ? -dp(7) : distance == 3 ? -dp(2) : 0f;
            extra[i] = slotWidth * (scale - 1f);
            total += extra[i];
        }
        float running = -total / 2f;
        for (int i = 0; i < icons.size(); i++) {
            ImageView v = icons.get(i);
            float translation = running + extra[i] / 2f;
            running += extra[i];
            int distance = selected < 0 ? 99 : Math.abs(i - selected);
            v.animate().cancel();
            v.setTranslationZ(distance == 0 ? dp(24) : distance == 1 ? dp(12)
                    : distance == 2 ? dp(5) : 0f);
            v.animate().scaleX(scales[i]).scaleY(scales[i])
                    .translationX(translation).translationY(lifts[i])
                    .setDuration(250).setInterpolator(ease).start();
        }
    }

    void restoreFocus() {
        View glass = activity.findViewById(R.id.glass);
        View hero = activity.findViewById(R.id.hero);
        if (glass != null) glass.setVisibility(View.INVISIBLE);
        if (hero != null) hero.setVisibility(View.INVISIBLE);
        if (!icons.isEmpty()) icons.get(Math.min(lastFocusedIndex, icons.size() - 1)).requestFocus();
    }

    private int dp(float n) { return Math.round(d * n); }
}
