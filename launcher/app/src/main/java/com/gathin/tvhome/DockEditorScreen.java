package com.gathin.tvhome;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Full-screen Dock editor; TV remote only, no phone/Mac or rebuild required. */
final class DockEditorScreen {
    private final MainActivity activity;
    private final FloatingDock dock;
    private final Dialog dialog;
    private final LinearLayout chosenArea;
    private final LinearLayout availableArea;
    private final ArrayList<String> chosen;
    private final LinkedHashMap<String, String> allApps = new LinkedHashMap<>();
    private final PackageManager pm;

    static void show(MainActivity activity, FloatingDock dock, View opener) {
        new DockEditorScreen(activity, dock, opener).open();
    }

    private DockEditorScreen(MainActivity activity, FloatingDock dock, View opener) {
        this.activity = activity;
        this.dock = dock;
        this.pm = activity.getPackageManager();
        this.chosen = FloatingDock.readPackages(activity);
        this.dialog = new Dialog(activity, android.R.style.Theme_Material_NoActionBar_Fullscreen);
        for (String category : new String[]{Intent.CATEGORY_LEANBACK_LAUNCHER, Intent.CATEGORY_LAUNCHER}) {
            Intent q = new Intent(Intent.ACTION_MAIN).addCategory(category);
            for (ResolveInfo ri : pm.queryIntentActivities(q, 0)) {
                if (ri.activityInfo == null || !ri.activityInfo.exported) continue;
                String pkg = ri.activityInfo.packageName;
                if (pkg.equals(activity.getPackageName()) || allApps.containsKey(pkg)) continue;
                allApps.put(pkg, String.valueOf(ri.loadLabel(pm)));
            }
        }
        // Allow an already configured favourite even if its launcher entry temporarily disappeared.
        for (String pkg : chosen) if (!allApps.containsKey(pkg)) allApps.put(pkg, pkg);

        LinearLayout screen = new LinearLayout(activity);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setPadding(dp(52), dp(26), dp(52), dp(22));
        GradientDrawable bg = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[]{0xFF101827, 0xFF26324B, 0xFF111827});
        screen.setBackground(bg);

        LinearLayout header = new LinearLayout(activity);
        header.setGravity(Gravity.CENTER_VERTICAL);
        TextView title = label("Edit Dock", 32, 0xFFFFFFFF);
        header.addView(title, new LinearLayout.LayoutParams(0, dp(60), 1));
        Button done = button("Done");
        done.setOnClickListener(v -> dialog.dismiss());
        header.addView(done, new LinearLayout.LayoutParams(dp(128), dp(52)));
        screen.addView(header, new LinearLayout.LayoutParams(-1, dp(65)));

        TextView hint = label("OK: Add / Remove     Move left/right: change order     Max "
                + FloatingDock.MAX_FAVORITES + " apps     Changes save instantly", 15, 0xBBCAD6EA);
        screen.addView(hint, new LinearLayout.LayoutParams(-1, dp(38)));

        ScrollView scroll = new ScrollView(activity);
        scroll.setFillViewport(false);
        LinearLayout sections = new LinearLayout(activity);
        sections.setOrientation(LinearLayout.VERTICAL);
        chosenArea = new LinearLayout(activity);
        chosenArea.setOrientation(LinearLayout.VERTICAL);
        availableArea = new LinearLayout(activity);
        availableArea.setOrientation(LinearLayout.VERTICAL);
        sections.addView(chosenArea, new LinearLayout.LayoutParams(-1, -2));
        sections.addView(availableArea, new LinearLayout.LayoutParams(-1, -2));
        scroll.addView(sections);
        screen.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        dialog.setContentView(screen);
        dialog.setOnDismissListener(ignored -> dock.restoreFocus());
    }

    private void open() {
        draw(0);
        dialog.show();
        Window w = dialog.getWindow();
        if (w != null) w.setLayout(-1, -1);
    }

    private void persist() {
        FloatingDock.savePackages(activity, chosen);
        dock.reloadFromPreferences();
    }

    private void draw(int desiredPosition) {
        chosenArea.removeAllViews();
        availableArea.removeAllViews();
        TextView selectedHeading = label("FAVORITES   " + chosen.size() + " / "
                + FloatingDock.MAX_FAVORITES, 21, Color.WHITE);
        selectedHeading.setPadding(0, dp(16), 0, dp(12));
        chosenArea.addView(selectedHeading);
        for (int i = 0; i < chosen.size(); i++) {
            final int index = i;
            String pkg = chosen.get(i);
            LinearLayout row = new LinearLayout(activity);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(dp(8), dp(2), dp(8), dp(2));
            ImageView icon = new ImageView(activity);
            try { icon.setImageDrawable(pm.getApplicationIcon(pkg)); }
            catch (PackageManager.NameNotFoundException e) { icon.setImageResource(R.drawable.ic_apps); }
            row.addView(icon, new LinearLayout.LayoutParams(dp(38), dp(38)));
            TextView name = label((i + 1) + ".  " + allApps.get(pkg), 18, 0xFFF0F4FC);
            name.setPadding(dp(16), 0, 0, 0);
            row.addView(name, new LinearLayout.LayoutParams(0, dp(49), 1f));
            Button left = button("◀ Left");
            left.setEnabled(i > 0);
            left.setOnClickListener(v -> swap(index, index - 1));
            row.addView(left, new LinearLayout.LayoutParams(dp(120), dp(46)));
            Button right = button("Right ▶");
            right.setEnabled(i + 1 < chosen.size());
            right.setOnClickListener(v -> swap(index, index + 1));
            row.addView(right, new LinearLayout.LayoutParams(dp(120), dp(46)));
            Button remove = button("Remove");
            remove.setEnabled(chosen.size() > 1);
            remove.setOnClickListener(v -> {
                if (chosen.size() <= 1) return;
                chosen.remove(index);
                persist();
                draw(Math.max(0, index - 1));
            });
            row.addView(remove, new LinearLayout.LayoutParams(dp(130), dp(46)));
            chosenArea.addView(row, new LinearLayout.LayoutParams(-1, dp(55)));
        }
        TextView availableHeading = label("ADD APPS", 21, Color.WHITE);
        availableHeading.setPadding(0, dp(24), 0, dp(12));
        availableArea.addView(availableHeading);
        ArrayList<Map.Entry<String, String>> apps = new ArrayList<>(allApps.entrySet());
        Collections.sort(apps, (a, b) -> a.getValue().compareToIgnoreCase(b.getValue()));
        for (Map.Entry<String, String> entry : apps) {
            String pkg = entry.getKey();
            if (chosen.contains(pkg)) continue;
            Button add = button("+  " + entry.getValue());
            add.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
            add.setOnClickListener(v -> {
                if (chosen.size() >= FloatingDock.MAX_FAVORITES) {
                    Toast.makeText(activity, "Remove an app first (max "
                            + FloatingDock.MAX_FAVORITES + ")", Toast.LENGTH_SHORT).show();
                    return;
                }
                chosen.add(pkg);
                persist();
                draw(chosen.size() - 1);
            });
            availableArea.addView(add, new LinearLayout.LayoutParams(-1, dp(50)));
        }
        chosenArea.post(() -> {
            int rowPos = Math.min(Math.max(0, desiredPosition), chosen.size() - 1) + 1;
            if (chosenArea.getChildCount() > rowPos) {
                View row = chosenArea.getChildAt(rowPos);
                if (row instanceof LinearLayout && ((LinearLayout) row).getChildCount() >= 5) {
                    LinearLayout buttons = (LinearLayout) row;
                    View right = buttons.getChildAt(3);
                    View left = buttons.getChildAt(2);
                    View remove = buttons.getChildAt(4);
                    if (right.isEnabled()) right.requestFocus();
                    else if (left.isEnabled()) left.requestFocus();
                    else if (remove.isEnabled()) remove.requestFocus();
                }
            }
        });
    }

    private void swap(int a, int b) {
        if (a < 0 || b < 0 || a >= chosen.size() || b >= chosen.size()) return;
        Collections.swap(chosen, a, b);
        persist();
        draw(b);
    }

    private TextView label(String str, int size, int color) {
        TextView t = new TextView(activity);
        t.setText(str);
        t.setTextColor(color);
        t.setTextSize(size);
        t.setGravity(Gravity.CENTER_VERTICAL);
        return t;
    }

    private Button button(String name) {
        Button b = new Button(activity);
        b.setText(name);
        b.setAllCaps(false);
        b.setTextSize(15);
        b.setTextColor(Color.WHITE);
        b.setFocusable(true);
        styleButton(b, false);
        b.setOnFocusChangeListener((v, focused) -> styleButton(b, focused));
        return b;
    }

    private void styleButton(Button b, boolean focused) {
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(focused ? 0xFF5475A9 : 0xFF35425B);
        bg.setStroke(dp(1), focused ? 0xFFDAE8FF : 0x335C7EA1);
        bg.setCornerRadius(dp(12));
        b.setBackground(bg);
    }

    private int dp(float n) {
        return Math.round(activity.getResources().getDisplayMetrics().density * n);
    }
}
