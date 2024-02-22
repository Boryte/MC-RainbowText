package net.kyodo.core.utilities;

import java.util.Arrays;
import java.util.List;

import java.util.Arrays;
import java.util.List;

public class RainbowText {
    private int place;
    private String text;
    private String fancyText;
    private List<String> rainbowArray;
    private String prefix;


    public RainbowText(final String text, final String formatCode) {
        this(text, formatCode, null);
    }

    public RainbowText(final String text, final List<String> rainbowArray) {
        this(text, null, rainbowArray);
    }

    public RainbowText(final String text, final String formatCode, final List<String> rainbowArray) {
        this.place = 0;
        this.text = (text != null) ? text : "You did not provide any text.";
        this.fancyText = "You did not provide any text";
        this.prefix = (formatCode != null) ? formatCode : "";
        this.place = 0;

        if (rainbowArray != null && !rainbowArray.isEmpty()) {
            this.rainbowArray = rainbowArray;
        } else {
            this.rainbowArray = getDefaultRainbow();
        }

        updateFancy();
    }

    private void updateFancy() {
        int spot = place;
        StringBuilder fancyTextBuilder = new StringBuilder();

        for (char l : text.toCharArray()) {
            String letter = Character.toString(l);

            if (!letter.equalsIgnoreCase(" ")) {
                fancyTextBuilder.append(rainbowArray.get(spot)).append(prefix).append(letter);
                if (spot == rainbowArray.size() - 1) {
                    spot = 0;
                } else {
                    spot++;
                }
            } else {
                fancyTextBuilder.append(letter);
            }
        }

        fancyText = fancyTextBuilder.toString();
    }

    public void moveRainbow() {
        if (rainbowArray.size() - 1 == place) {
            place = 0;
        } else {
            place++;
        }
        updateFancy();
    }

    public void moveRainbowRight() {
        if (place == 0) {
            place = rainbowArray.size() - 1;
        } else {
            place--;
        }
        updateFancy();
    }

    public String getOrigonalText() {
        return text;
    }

    public String getText() {
        return fancyText;
    }

    public void setPlace(final int place) {
        if (place >= 0 && place < rainbowArray.size()) {
            this.place = place;
            updateFancy();
        }
    }

    public List<String> getRainbow() {
        return rainbowArray;
    }

    public String getFormatPrefix() {
        return prefix;
    }

    public void setFormatPrefix(final String prefix) {
        this.prefix = prefix;
        updateFancy();
    }

    public static List<String> getDefaultRainbow() {
        return Arrays.asList("#FF0000", "#FFA500", "#FFFF00", "#00FF00", "#0000FF", "#4B0082", "#8A2BE2", "#FF00FF");
    }
}
