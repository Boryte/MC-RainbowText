package dev.bytecore.trollreborn.utilities;

import net.md_5.bungee.api.ChatColor;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RainbowText {
    private final String text;
    private final List<String> colorCycle;
    private final String formatCode;
    private int offset;

    private RainbowText(String text, List<String> cycle, String format, int offset) {
        this.text       = Objects.requireNonNull(text, "text");
        this.colorCycle = List.copyOf(cycle);
        this.formatCode = Objects.requireNonNull(format, "format");
        this.offset     = Math.floorMod(offset, colorCycle.size());
    }

    /** Convenience: rainbowize with defaults. */
    public static RainbowText of(String text) {
        return builder(text).build();
    }

    /** Start a custom rainbow build. */
    public static Builder builder(String text) {
        return new Builder(text);
    }

    /** Rotate the cycle by +1 (or any amount). */
    public RainbowText shift(int by) {
        offset = Math.floorMod(offset + by, colorCycle.size());
        return this;
    }

    /** Produce the final colored string. */
    @Override
    public String toString() {
        var sb = new StringBuilder(text.length() * 8);
        int n = colorCycle.size();

        for (int i = 0, len = text.length(); i < len; i++) {
            char ch = text.charAt(i);
            if (Character.isWhitespace(ch)) {
                sb.append(ch);
            } else {
                // pick a color from the cycle
                String rawColor = colorCycle.get((i + offset) % n);
                sb.append(colorize(rawColor))
                        .append(formatCode)
                        .append(ch);
            }
        }
        return sb.toString();
    }

    /** Interpret "#RRGGBB" with Bungee ChatColor.of, or legacy codes. */
    private String colorize(String code) {
        if (code.startsWith("#")) {
            return ChatColor.of(code).toString();
        }
        // support both '&' and '§' as prefix for legacy
        if (code.startsWith("&") || code.startsWith("§")) {
            return ChatColor.translateAlternateColorCodes('&', code.replace('§','&'));
        }
        // otherwise treat it as already a ChatColor string
        return code;
    }

    /** Builder to customize cycle, brightness, saturation, etc. */
    public static class Builder {
        private final String text;
        private int  steps      = 180;
        private float saturation= 1.0f;
        private float brightness= 1.0f;
        private String format   = "";
        private List<String> customCycle = null;

        private Builder(String text) {
            this.text = text;
        }

        /** Override the entire color cycle manually. */
        public Builder cycle(List<String> hexOrLegacyCodes) {
            this.customCycle = new ArrayList<>(hexOrLegacyCodes);
            return this;
        }

        /** How many distinct hues to generate around the circle. Default 180. */
        public Builder steps(int steps) {
            this.steps = Math.max(3, steps);
            return this;
        }

        /** Saturation [0.0–1.0], default 1.0. */
        public Builder saturation(float sat) {
            this.saturation = Math.min(1f, Math.max(0f, sat));
            return this;
        }

        /** Brightness [0.0–1.0], default 1.0. */
        public Builder brightness(float b) {
            this.brightness = Math.min(1f, Math.max(0f, b));
            return this;
        }

        /** Extra format code after each color, e.g. ChatColor.BOLD.toString() */
        public Builder format(String legacyOrChatCode) {
            this.format = legacyOrChatCode == null ? "" : legacyOrChatCode;
            return this;
        }

        public RainbowText build() {
            var cycle = (customCycle != null)
                    ? List.copyOf(customCycle)
                    : generateRainbow(steps, saturation, brightness);
            return new RainbowText(text, cycle, format, 0);
        }

        /** HSB → hexcycle generator. */
        private static List<String> generateRainbow(int steps, float sat, float bri) {
            return IntStream.range(0, steps)
                    .mapToObj(i -> {
                        float hue = i / (float) steps;
                        Color c = Color.getHSBColor(hue, sat, bri);
                        return String.format("#%02x%02x%02x",
                                c.getRed(), c.getGreen(), c.getBlue());
                    })
                    .collect(Collectors.toUnmodifiableList());
        }
    }
}
