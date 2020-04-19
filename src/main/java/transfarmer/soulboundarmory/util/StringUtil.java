package transfarmer.soulboundarmory.util;

public class StringUtil {
    public static String macroCaseToCamelCase(final String string) {
        final StringBuilder builder = new StringBuilder();
        final char[] chars = string.toLowerCase().toCharArray();

        for (int i = 0; i < chars.length; i++) {
            char character = chars[i];

            if (character == '_') {
                character = (char) (chars[++i] - 32);
            }

            builder.append(character);
        }

        return builder.toString();
    }
}
