package transfarmer.soulboundarmory.util;

public class MathUtil {
    public static double signum(final double first, final double... others) {
        double sign = Math.signum(first);

        for (final double number : others) {
            sign *= Math.signum(number);
        }

        return sign;
    }
}
