package dev.mineland.item_interactions_mod;

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
}
