package transfarmer.soulboundarmory.util;

public class IO {
    public static void println(int... items) {
        for (int item : items) {
            System.out.printf("%d ", item);
        }

        System.out.println();
    }

    public static void println(float... array) {
        for (float element : array) {
            System.out.printf("%f ", element);
        }

        System.out.println();
    }

    public static void println(float[]... args) {
        for (float[] array : args) {
            println(array);
            System.out.println();
        }
    }

    public static void println(float[][]... args) {
        for (float[][] array : args) {
            println(array);
            System.out.println();
        }
    }

    public static void println(double... items) {
        for (double item : items) {
            System.out.printf("%f ", item);
        }

        System.out.println();
    }

    public static void println(String... items) {
        for (String item : items) {
            System.out.printf("%s ", item);
        }

        System.out.println();
    }

    /*
    @SafeVarargs
    public static <T> void println(T... item) {
        System.out.println(item);
    }

     */
}
