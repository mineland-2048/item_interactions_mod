package dev.mineland.item_interactions_mod;

import org.apache.commons.lang3.math.NumberUtils;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.Vector;

public class MiscUtils {

    //    I dont know if java has any counting function for strings, so i made this. Aeugh
    public static int count(String s, String match) {
        int length = match.length();
        int result = 0;
        for (int i = 0; i <= s.length() - length; i++) {
            if (s.substring(i, i+length).equals(match)) result++;
        }
        return result;
    }


    public static String preNumberCharacter(int number, String characters) {
        if (number < 0) {
            return new StringBuilder().append(number).insert(1, characters).toString();
        }
        return characters + number;
    }

    public static String preNumberCharacter(double number, String characters) {
        if (number < 0) {
            return new StringBuilder().append(number).insert(1, characters).toString();
        }
        return characters + number;
    }

    public static String preNumberCharacter(float number, String characters) {
        if (number < 0) {
            return new StringBuilder().append(number).insert(1, characters).toString();
        }
        return characters + number;
    }


    public static double randomRange(double min, double max) {
        double range = max - min;
        return (Math.random() * range) + min;
    }

    public static double randomVariance(double n, double variance) {
            return n + randomRange(-variance, variance);
    }

    public static int[] colorVariance(int[] color, int[] variance) {
        int[] result = new int[4];
        for (int i = 0; i < color.length; i++) {
            result[i] = (int) Math.clamp(MiscUtils.randomVariance(color[i], variance[i]), 0, 255);
        }
        return result;

    }

    public static int colorLerp(float t, int a, int b) {

        int[] s = int2Array(a), e = int2Array(b);



        int A = lerp(t, s[0], e[0]);
        int R = lerp(t, s[1], e[1]);
        int G = lerp(t, s[2], e[2]);
        int B = lerp(t, s[3], e[3]);

        return array2Int(new int[]{A,R,G,B});
    }

    public static int[] int2Array(int i) {
        return new int[]{
                (i >> 24) & 0xFF,
                (i >> 16) & 0xff,
                (i >> 8) & 0xFF,
                (i) & 0xFF
        };
    }

    public static int array2Int(int[] i) {
        return  (i[0] << 24) +
                (i[1] << 16) +
                (i[2] << 8)  +
                (i[3]);

    }

    public static int lerp(float t, int a, int b) {
            return (int) (a + ((b-a) * t));
    }
    public static float lerp(float t, float a, float b) {
            return (a + ((b-a) * t));
    }
    public static double lerp(double t, double a, double b) {
            return (a + ((b-a) * t));
    }

    public static double lerpRotation(double t, double a, double b) {
        var shortest_angle=((((b - a) % Math.PI) + (Math.PI * 1.5)) % Math.PI) - (Math.PI/2);
        return a + shortest_angle * t;
//        double d = ((b - a + (Math.PI * 1.5)) % Math.PI) - (Math.PI*0.5);
//        return a + d * t;
    }

    public static Vector3f lerpVector3f(float t, Vector3f a, Vector3f b) {
        return new Vector3f(
                MiscUtils.lerp(t, a.x(), b.x()),
                MiscUtils.lerp(t, a.y(), b.y()),
                MiscUtils.lerp(t, a.z(), b.z())
            );
    }

    public static double lerpRotationDegrees(double t, double a, double b) {
        double d = ((b - a + 540) % 360) - 180;
        return a + d * t;
    }

    public static int[] applyBrightness(int[] colorArray, double brightness) {
        double clampedBrightness = Math.clamp(brightness, 0, 1);
        int r = (int) (colorArray[1] * clampedBrightness);
        int g = (int) (colorArray[2] * clampedBrightness);
        int b = (int) (colorArray[3] * clampedBrightness);

        return new int[]{colorArray[0], r, g, b};

    }
    public static int applyBrightness(int color, double brightness) {
        return array2Int(applyBrightness(int2Array(color), brightness));
    }

    public static boolean isNumber(String s) {
        return NumberUtils.isParsable(s) || NumberUtils.isCreatable(s);
    }

    public static boolean isBoolean(String s) {
        return s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false");
    }

    public static boolean isVector(String s) {
        if (s.isEmpty()) return false;
        if (!s.startsWith("(") || !s.endsWith(")")) return false;


        s = s.replace("(", "").replace(")", "").trim().replace("  ", " ");
        String[] rawValues = s.split(" ");

        for (String rawValue : rawValues) {
            if (!isNumber(rawValue)) return false;
        }

        return true;
    }

    private static float[] parseVectorGetArray(String s) {
        if (s.isEmpty()) throw(new NumberFormatException("String is empty"));
        if (!s.startsWith("(") || !s.endsWith(")")) throw new NumberFormatException("String is not enclosed in (braces)");


        s = s.replace("(", "").replace(")", "").trim().replace("  ", " ");
        String[] rawValues = s.split(" ");

        float[] numValues = new float[s.length()];
        int i = 0;
        for (String rawValue : rawValues) {
            if (isNumber(rawValue)) {
                numValues[i] = Float.parseFloat(rawValue);
                i++;
            } else throw new NumberFormatException("Value '" + rawValues[i] + "' is not a valid number");
        }

        return numValues;
    }
    public static Vector3f parseVector3f(String s) {
        float[] numValues = parseVectorGetArray(s);
        return new Vector3f(numValues);
    }

    public static String numberMaxDigits(double num, int digits) {
        return num < 0 ? String.valueOf(num).substring(0, digits+1) : String.valueOf(num).substring(0, digits);
    }

    public static String numberMaxDecimal(double num, int digits) {
        String numString = String.valueOf(num);
        if (numString.contains(".")) {
            numString = numString.substring(0, Math.clamp(numString.indexOf(".") + digits, 0, numString.length()-1) );
        }
        if (numString.endsWith(".")) return numString.substring(0, numString.length()-1);

        return numString;
    }
    public static String numberMaxDecimal(int num, int digits) {
        String numString = String.valueOf(num);
        if (numString.contains(".")) {
            return numString.substring(0, Math.clamp(numString.indexOf(".") + digits, 0, numString.length()-1) );
        }
        return numString;
    }


    public static Vector2f pointAtFrom(Vector2f angleVector, Vector2f posVector) {

        float x = posVector.x();
        float y = posVector.y();

        x += (float) Math.cos(angleVector.x()) * angleVector.y();
        y += (float) Math.sin(angleVector.x()) * angleVector.y();
        return new Vector2f(x, y);
    }

    public static Vector3f pointAtFrom(Vector3f angleVector, Vector3f posVector) {

        float x = posVector.x();
        float y = posVector.y();

        x += (float) Math.cos(angleVector.x()) * angleVector.y();
        y += (float) Math.sin(angleVector.x()) * angleVector.y();
        return new Vector3f(x, y, posVector.z);
    }



}
