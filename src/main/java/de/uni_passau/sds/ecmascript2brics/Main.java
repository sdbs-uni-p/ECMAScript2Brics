package de.uni_passau.sds.ecmascript2brics;

import org.apache.commons.text.StringEscapeUtils;

import java.util.Scanner;

public class Main {

    public static void main(String[] argv) {
        Scanner sc = new Scanner(System.in);
        boolean quit = false;

        while (!quit) {
            System.out.println("Please enter an ECMAScript expression or \"\\q\" to quit the program:");
            String input = sc.nextLine();
            if (input.equals("\\q")) {
                quit = true;
                System.out.println("Program closed.");
                continue;
            }
            try {
                String bricsRegex = EcmaToBricsExpressionConverter.convert(input);
                System.out.println("The Brics expression is: " + StringEscapeUtils.escapeJava(bricsRegex));
            } catch (ECMAScriptLookaroundException e) {
                System.err.println("Lookahead/lookbehind is not supported.");
            } catch (ECMAScriptNotSupportedException e) {
                System.err.println("This expression is not supported.");
            } catch (ECMAScriptSyntaxException e) {
                System.err.println("This expression is not a valid ECMAScript expression.");
            } catch (RuntimeException e) {
                System.err.println("Unexpected error.");
            }
        }
    }

}
