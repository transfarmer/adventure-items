package transfarmer.soulboundarmory.util;

@SuppressWarnings("unchecked")
public class MathUtil {
    public static double signum(final double first, final double... others) {
        double sign = Math.signum(first);

        for (final double number : others) {
            sign *= Math.signum(number);
        }

        return sign;
    }

    public static int min(final int... values) {
        int min = values[0];

        for (final int value : values) {
            if (value < min) {
                min = value;
            }
        }

        return min;
    }

    public static double min(final double... values) {
        double min = values[0];

        for (final double value : values) {
            if (value < min) {
                min = value;
            }
        }

        return min;
    }

    public static int max(final int... values) {
        int max = values[0];

        for (final int value : values) {
            if (value > max) {
                max = value;
            }
        }

        return max;
    }

    public static double max(final double... values) {
        double max = values[0];

        for (final double value : values) {
            if (value > max) {
                max = value;
            }
        }

        return max;
    }

    public static double hypot(final double... values) {
        return Math.sqrt(sqSum(values));
    }

    public static int sum(final int... values) {
        int sum = 0;

        for (final int value : values) {
            sum += value;
        }

        return sum;
    }

    public static double sum(final double... values) {
        double sum = 0;

        for (final double value : values) {
            sum += value;
        }

        return sum;
    }

    public static double sqSum(final double... values) {
        double sum = 0;

        for (final double value : values) {
            sum += value * value;
        }

        return sum;
    }
}
