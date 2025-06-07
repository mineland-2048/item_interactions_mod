package dev.mineland.item_interactions_mod;

import net.minecraft.Util;
import net.minecraft.util.ColorRGBA;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;

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

    public static int[] applyBrightness(int[] colorArray, double brightness) {
        double clampedBrightness = Math.clamp(brightness, 0, 1);
        int r = (int) (colorArray[1] * clampedBrightness);
        int g = (int) (colorArray[2] * clampedBrightness);
        int b = (int) (colorArray[3] * clampedBrightness);

        return new int[]{colorArray[0], r, g, b};
    }

    public static boolean isNumber(String s) {
        return NumberUtils.isParsable(s);
    }

    public static boolean isBoolean(String s) {
        return s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false");
    }

}
