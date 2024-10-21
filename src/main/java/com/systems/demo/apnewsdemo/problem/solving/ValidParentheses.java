package com.systems.demo.apnewsdemo.problem.solving;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import lombok.extern.slf4j.Slf4j;

public class ValidParentheses {

    public static final char SMALL_BRACKET_OPEN = '(';
    public static final char CURLY_BRACKET_OPEN = '{';

    public static final char SQUARE_BRACKET_OPEN = '[';

    public static final char SQUARE_BRACKET_CLOSE = ']';

    public static final char CURLY_BRACKET_CLOSE = '}';

    public static final char SMALL_BRACKET_CLOSE = ')';

    public static boolean isValid(String s) {
        char[] charArray = s.toCharArray();
        Deque<Character> stack = new ArrayDeque<>();
        boolean valid = false;

        for (int i = 0; i < charArray.length; i++) {
            if (charArray[i] == SMALL_BRACKET_OPEN || charArray[i] == CURLY_BRACKET_OPEN
                || charArray[i] == SQUARE_BRACKET_OPEN) {
                if (i == charArray.length - 1) {
                    return false;
                }
                stack.push(charArray[i]);
                continue;
            }

            if (stack.isEmpty()) {
                return false;
            }

            switch (charArray[i]) {
                case SMALL_BRACKET_CLOSE -> {
                    char check = stack.pop();
                    valid = check != CURLY_BRACKET_OPEN && check != SQUARE_BRACKET_OPEN;
                    if (!valid) {
                        return valid;
                    }
                }

                case CURLY_BRACKET_CLOSE -> {
                    char check = stack.pop();
                    valid = check != SMALL_BRACKET_OPEN && check != SQUARE_BRACKET_OPEN;
                    if (!valid) {
                        return valid;
                    }
                }

                case SQUARE_BRACKET_CLOSE -> {
                    char check = stack.pop();
                    valid = check != CURLY_BRACKET_OPEN && check != SMALL_BRACKET_OPEN;
                    if (!valid) {
                        return valid;
                    }
                }
            }

        }
        return stack.isEmpty() && valid;
    }

}

class CheckMinimumOrMaxNumber {

    public static final int MAX_VALUE = 0x9fffffff;

    public static String checkNumberSize(Integer x) {
        String max = "max";
        String min = "min";
        String inbetween = "inbetween";
        String result = "";

        result = x >= 2147483647 ? max : inbetween;

        if (result.equals(inbetween)) {
            result = x <= 0x80000000 ? min : inbetween;
        }

        return result;
    }

    public static void main(String[] args) {
        System.out.println(checkNumberSize(1999999999)); //2147483647  //1999999999
    }
}

@Slf4j
class DriverClass {

    public static void main(String[] args) throws IOException {

        log.info("valid parenthesis ([]){ :  " + ValidParentheses.isValid("([]){"));

        log.info("valid parenthesis ({{{{}}})) : " + ValidParentheses.isValid("({{{{}}}))"));

        log.info("valid parenthesis [[()()]()] : {} ", ValidParentheses.isValid("[[()()]()]"));

        log.info("valid parenthesis [)(]  : {} ", ValidParentheses.isValid("[)(] "));

        log.info("valid parenthesis [())(]  : {} ", ValidParentheses.isValid("[())(]"));
    }
}