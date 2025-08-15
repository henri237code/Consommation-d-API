package data;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class OwnedStore {
    private static final String PREF = "owned_profiles";
    private static final String KEY  = "ids";

    public static Set<Integer> get(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        Set<String> raw = sp.getStringSet(KEY, new HashSet<>());
        Set<Integer> out = new HashSet<>();
        for (String s : raw) try { out.add(Integer.parseInt(s)); } catch (Exception ignore) {}
        return out;
    }

    public static void add(Context ctx, int id) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        Set<String> raw = new HashSet<>(sp.getStringSet(KEY, new HashSet<>()));
        raw.add(String.valueOf(id));
        sp.edit().putStringSet(KEY, raw).apply();
    }

    public static void remove(Context ctx, int id) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        Set<String> raw = new HashSet<>(sp.getStringSet(KEY, new HashSet<>()));
        raw.remove(String.valueOf(id));
        sp.edit().putStringSet(KEY, raw).apply();
    }
}
