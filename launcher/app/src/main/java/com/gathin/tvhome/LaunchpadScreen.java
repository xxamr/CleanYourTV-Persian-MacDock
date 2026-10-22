package com.gathin.tvhome;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.GradientDrawable;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

/** Full-screen, remote-controlled app grid independent of the original app-library popup. */
final class LaunchpadScreen {
    private LaunchpadScreen() { }

    private static int dp(Activity a, float val) {
        return Math.round(a.getResources().getDisplayMetrics().density * val);
    }

    private static final class App {
        final String pkg;
        final String name;
        App(String pkg, String name) { this.pkg = pkg; this.name = name; }
    }

    static void show(MainActivity activity, View opener, Runnable onEditDock) {
        PackageManager pm = activity.getPackageManager();
        LinkedHashMap<String, App> byPackage = new LinkedHashMap<>();
        for (String category : new String[]{Intent.CATEGORY_LEANBACK_LAUNCHER, Intent.CATEGORY_LAUNCHER}) {
            Intent scan = new Intent(Intent.ACTION_MAIN).addCategory(category);
            List<ResolveInfo> results = pm.queryIntentActivities(scan, 0);
            for (ResolveInfo ri : results) {
                if (ri.activityInfo == null || !ri.activityInfo.exported) continue;
                String pkg = ri.activityInfo.packageName;
                if (pkg.equals(activity.getPackageName()) || byPackage.containsKey(pkg)) continue;
                byPackage.put(pkg, new App(pkg, String.valueOf(ri.loadLabel(pm))));
            }
        }
        ArrayList<App> apps = new ArrayList<>(byPackage.values());
        Collections.sort(apps, (a, b) -> a.name.compareToIgnoreCase(b.name));

        Dialog dialog = new Dialog(activity, android.R.style.Theme_Material_NoActionBar_Fullscreen);
        LinearLayout content = new LinearLayout(activity);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(activity, 48), dp(activity, 28), dp(activity, 48), dp(activity, 12));
        GradientDrawable bg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{0xFF101827, 0xFF273052, 0xFF121A2B});
        content.setBackground(bg);

        TextView title = new TextView(activity);
        title.setText("Launchpad");
        title.setTextColor(Color.WHITE);
        title.setTextSize(34);
        title.setPadding(dp(activity, 12), 0, 0, dp(activity, 12));
        LinearLayout heading = new LinearLayout(activity);
        heading.setGravity(Gravity.CENTER_VERTICAL);
        heading.addView(title, new LinearLayout.LayoutParams(0, dp(activity, 68), 1));
        TextView editDock = new TextView(activity);
        editDock.setText("Edit Dock");
        editDock.setTextColor(0xFFFFFFFF);
        editDock.setTextSize(19);
        editDock.setGravity(Gravity.CENTER);
        editDock.setFocusable(true);
        editDock.setClickable(true);
        editDock.setPadding(dp(activity, 16), dp(activity, 8), dp(activity, 16), dp(activity, 8));
        GradientDrawable editBg = new GradientDrawable();
        editBg.setCornerRadius(dp(activity, 16));
        editBg.setColor(0xAA354967);
        editBg.setStroke(dp(activity, 1), 0x77FFFFFF);
        editDock.setBackground(editBg);
        editDock.setOnFocusChangeListener((v, focused) -> {
            editDock.animate().scaleX(focused ? 1.08f : 1f)
                    .scaleY(focused ? 1.08f : 1f).setDuration(140).start();
        });
        editDock.setOnClickListener(v -> {
            dialog.setOnDismissListener(null);
            dialog.dismiss();
            onEditDock.run();
        });
        heading.addView(editDock, new LinearLayout.LayoutParams(dp(activity, 170), dp(activity, 50)));
        content.addView(heading, new LinearLayout.LayoutParams(-1, dp(activity, 68)));

        RecyclerView grid = new RecyclerView(activity);
        grid.setLayoutManager(new GridLayoutManager(activity, 6));
        grid.setClipChildren(false);
        grid.setClipToPadding(false);
        grid.setOverScrollMode(View.OVER_SCROLL_NEVER);
        grid.setPadding(dp(activity, 8), dp(activity, 8), dp(activity, 8), dp(activity, 24));
        grid.setItemAnimator(null);
        grid.setAdapter(new AppsAdapter(activity, pm, apps, dialog));
        content.addView(grid, new LinearLayout.LayoutParams(-1, 0, 1));

        dialog.setContentView(content);
        dialog.setOnDismissListener(ignored -> opener.requestFocus());
        dialog.show();
        Window w = dialog.getWindow();
        if (w != null) w.setLayout(-1, -1);
        grid.post(() -> {
            RecyclerView.ViewHolder first = grid.findViewHolderForAdapterPosition(0);
            if (first != null) first.itemView.requestFocus();
            else grid.requestFocus();
        });
    }

    private static final class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.Holder> {
        final Activity activity;
        final PackageManager pm;
        final List<App> apps;
        final Dialog dialog;

        AppsAdapter(Activity activity, PackageManager pm, List<App> apps, Dialog dialog) {
            this.activity = activity;
            this.pm = pm;
            this.apps = apps;
            this.dialog = dialog;
        }

        static final class Holder extends RecyclerView.ViewHolder {
            final ImageView icon;
            final TextView name;
            Holder(@NonNull View item, ImageView icon, TextView name) {
                super(item);
                this.icon = icon;
                this.name = name;
            }
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LinearLayout item = new LinearLayout(activity);
            item.setOrientation(LinearLayout.VERTICAL);
            item.setGravity(Gravity.CENTER);
            item.setFocusable(true);
            item.setClickable(true);
            item.setClipChildren(false);
            item.setPadding(dp(activity, 5), dp(activity, 9), dp(activity, 5), dp(activity, 5));
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(-1, dp(activity, 118));
            lp.setMargins(dp(activity, 7), dp(activity, 6), dp(activity, 7), dp(activity, 6));
            item.setLayoutParams(lp);

            ImageView icon = new ImageView(activity);
            icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
            item.addView(icon, new LinearLayout.LayoutParams(dp(activity, 66), dp(activity, 66)));
            TextView name = new TextView(activity);
            name.setTextColor(0xE6FFFFFF);
            name.setTextSize(13);
            name.setSingleLine(true);
            name.setEllipsize(android.text.TextUtils.TruncateAt.END);
            name.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams nameLp = new LinearLayout.LayoutParams(-1, dp(activity, 28));
            nameLp.topMargin = dp(activity, 4);
            item.addView(name, nameLp);

            GradientDrawable idle = new GradientDrawable();
            idle.setColor(0x002E4063);
            idle.setCornerRadius(dp(activity, 18));
            GradientDrawable focused = new GradientDrawable();
            focused.setColor(0xA0455875);
            focused.setCornerRadius(dp(activity, 18));
            focused.setStroke(dp(activity, 1), 0x88FFFFFF);
            item.setBackground(idle);
            item.setOnFocusChangeListener((v, hasFocus) -> {
                v.setBackground(hasFocus ? focused : idle);
                v.animate().cancel();
                v.animate().scaleX(hasFocus ? 1.10f : 1f)
                        .scaleY(hasFocus ? 1.10f : 1f).setDuration(130).start();
                if (hasFocus) v.bringToFront();
            });
            item.setOnClickListener(v -> {
                int pos = ((RecyclerView) parent).getChildAdapterPosition(v);
                if (pos < 0 || pos >= apps.size()) return;
                App app = apps.get(pos);
                Intent intent = pm.getLeanbackLaunchIntentForPackage(app.pkg);
                if (intent == null) intent = pm.getLaunchIntentForPackage(app.pkg);
                if (intent == null) {
                    Toast.makeText(activity, "Cannot open " + app.name, Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    dialog.dismiss();
                    activity.startActivity(intent);
                } catch (RuntimeException e) {
                    Toast.makeText(activity, "Cannot open " + app.name, Toast.LENGTH_SHORT).show();
                }
            });
            return new Holder(item, icon, name);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            App app = apps.get(position);
            holder.name.setText(app.name);
            try { holder.icon.setImageDrawable(pm.getApplicationIcon(app.pkg)); }
            catch (PackageManager.NameNotFoundException e) {
                holder.icon.setImageResource(R.drawable.ic_apps);
            }
        }

        @Override public int getItemCount() { return apps.size(); }
    }
}
