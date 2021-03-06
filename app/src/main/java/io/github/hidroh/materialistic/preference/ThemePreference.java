/*
 * Copyright (c) 2015 Ha Duy Trung
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.hidroh.materialistic.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.v4.util.ArrayMap;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import io.github.hidroh.materialistic.R;

public class ThemePreference extends Preference {

    private static final String LIGHT = "light";
    private static final String DARK = "dark";
    private static final String BLACK = "black";
    private static final String SEPIA = "sepia";
    private static final String GREEN = "green";
    private static final String SOLARIZED = "solarized";
    private static final String SOLARIZED_DARK = "solarized_dark";
    private static final ArrayMap<Integer, String> BUTTONS = new ArrayMap<>();
    private static final ArrayMap<String, ThemeSpec> VALUES = new ArrayMap<>();
    static {
        BUTTONS.put(R.id.theme_light, LIGHT);
        BUTTONS.put(R.id.theme_dark, DARK);
        BUTTONS.put(R.id.theme_black, BLACK);
        BUTTONS.put(R.id.theme_sepia, SEPIA);
        BUTTONS.put(R.id.theme_green, GREEN);
        BUTTONS.put(R.id.theme_solarized, SOLARIZED);
        BUTTONS.put(R.id.theme_solarized_dark, SOLARIZED_DARK);

        VALUES.put(LIGHT, new LightSpec(R.string.theme_light));
        VALUES.put(DARK, new DarkSpec(R.string.theme_dark));
        VALUES.put(BLACK, new DarkSpec(R.string.theme_black, R.style.Black));
        VALUES.put(SEPIA, new LightSpec(R.string.theme_sepia, R.style.Sepia));
        VALUES.put(GREEN, new LightSpec(R.string.theme_green, R.style.Green));
        VALUES.put(SOLARIZED, new LightSpec(R.string.theme_solarized, R.style.Solarized));
        VALUES.put(SOLARIZED_DARK, new DarkSpec(R.string.theme_solarized_dark,
                R.style.Solarized_Dark));
    }

    public static ThemeSpec getTheme(String value, boolean isTranslucent) {
        ThemeSpec themeSpec = VALUES.get(VALUES.containsKey(value) ? value : LIGHT);
        return isTranslucent ? themeSpec.getTranslucent() : themeSpec;
    }

    public ThemePreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThemePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayoutResource(R.layout.preference_theme);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return LIGHT;
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        super.onSetInitialValue(restorePersistedValue, defaultValue);
        String value = restorePersistedValue ? getPersistedString(null): (String) defaultValue;
        if (TextUtils.isEmpty(value)) {
            value = LIGHT;
        }
        setSummary(VALUES.get(value).summary);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        holder.itemView.setClickable(false);
        for (int i = 0; i < BUTTONS.size(); i++) {
            final int buttonId = BUTTONS.keyAt(i);
            final String value = BUTTONS.valueAt(i);
            View button = holder.findViewById(buttonId);
            button.setClickable(true);
            button.setOnClickListener(v -> {
                setSummary(VALUES.get(value).summary);
                persistString(value);
            });
        }
    }

    public static class ThemeSpec {
        final @StringRes int summary;
        public final @StyleRes int theme;
        public final @StyleRes int themeOverrides;
        ThemeSpec translucent;

        private ThemeSpec(@StringRes int summary, @StyleRes int theme, @StyleRes int themeOverrides) {
            this.summary = summary;
            this.theme = theme;
            this.themeOverrides = themeOverrides;
        }

        ThemeSpec getTranslucent() {
            return this;
        }
    }

    static class LightSpec extends ThemeSpec {

        LightSpec(@StringRes int summary) {
            this(summary, -1);
        }

        LightSpec(@StringRes int summary, @StyleRes int themeOverrides) {
            super(summary, R.style.AppTheme, themeOverrides);
        }

        @Override
        ThemeSpec getTranslucent() {
            if (translucent == null) {
                translucent = new ThemeSpec(summary, R.style.AppTheme_Translucent, themeOverrides);
            }
            return translucent;
        }
    }

    static class DarkSpec extends ThemeSpec {

        DarkSpec(@StringRes int summary) {
            this(summary, -1);
        }

        DarkSpec(@StringRes int summary, @StyleRes int themeOverrides) {
            super(summary, R.style.AppTheme_Dark, themeOverrides);
        }

        @Override
        ThemeSpec getTranslucent() {
            if (translucent == null) {
                translucent = new ThemeSpec(summary, R.style.AppTheme_Dark_Translucent, themeOverrides);
            }
            return translucent;
        }
    }
}
