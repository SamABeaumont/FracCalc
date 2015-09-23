import java.util.*;

/**
 * Calculates fractions that the user inputs to the console.
 * @author Sam Beaumont
 * @version 1.0
 * @since 2014-10-20
 */

public class FracCalc {
	public static boolean debug = false;
	
	public static void main (String[] args) {
		System.out.println("Type an expression,");
		System.out.println("the word HELP for a explanation,");
		System.out.println("or the word QUIT to quit:");
		
		Scanner console = new Scanner(System.in);
		String input = console.nextLine();
		while (!input.equalsIgnoreCase("quit")) {
			if (input.equalsIgnoreCase("help")) {
				help();
			} else if (isValid(input, true)) {
				processInput(input);
			} else { // invalid input
				System.out.println("Error: Invalid input. Please try again.");
			}
			System.out.println("New expression:");
			input = console.nextLine();
		}
	}
	
	/**
	 * Uses a regular expression to determine whether the string that the user inputs is a valid expression.
	 * @param s The string that the user inputs.
	 * @return {@code true} if the expression is valid, {@code false} otherwise.
	 */
	public static boolean isValid (String s, boolean b) { // This works!
		if (s.indexOf(" ") == -1 || !b) { // just a number
			if (s.matches("-?\\d+_\\d+/[123456789]\\d*")) { // mixed number
				return true;
			} else if (s.matches("-?\\d+/[123456789]\\d*")) { // fraction
				return true;
			} else { // whole number or invalid
				return s.matches("-?\\d+");
			}
		} else { // not a single number
			String frac1 = "";
			String frac2 = "";
			String type = s.charAt(s.indexOf(" ") + 1) + "";
			frac1 = s.substring(0, s.indexOf(type + " ") - 1);
			frac2 = s.substring(s.indexOf(type + " ") + 2, s.length());
			return isValid(frac1, false) && isValid(frac2, false);
		}
	}
	
	/**
	 * Prints the value of the expression that the user inputs.
	 * @param s The expression to be evaluated
	 * @return 0 - Used to quickly exit the method if the input is invalid.
	 */
	public static int processInput (String s) {
		String frac1 = "";
		String frac2 = "";
		char type;
		Scanner validator = new Scanner(s);
		if (s.indexOf("+") != -1) { // addition
			frac1 = s.substring(0, s.indexOf(" + "));
			frac2 = s.substring(s.indexOf(" + ") + 3, s.length());
			type = '+';
		} else if (s.indexOf(" - ") > 0) { // So as not to include the negative sign.
			frac1 = s.substring(0, s.indexOf(" - "));
			frac2 = s.substring(s.indexOf(" - ") + 3, s.length());
			type = '-';
		} else if (s.indexOf("*") != -1) { // multiplication
			frac1 = s.substring(0, s.indexOf(" * "));
			frac2 = s.substring(s.indexOf(" * ") + 3, s.length());
			type = '*';
		} else if (s.indexOf(" / ") != -1) { // division
			frac1 = s.substring(0, s.indexOf(" / "));
			frac2 = s.substring(s.indexOf(" / ") + 3, s.length());
			type = '/';
		}  else { // no operator, just a number
			System.out.println(processFrac(s));
			type = '0';
			return 0; // To get out of the method.
		}
		log("frac1 is " + frac1 + "\n" +
			"frac2 is " + frac2 + "\n" +
			"type is " + type);
		log("frac1 is " + processFrac(frac1));
		log("frac2 is " + processFrac(frac2));
		
		System.out.println(solve(processFrac(frac1), processFrac(frac2), type));
		return 0;
	}
	
	/**
	 * Converts any number into an improper fraction so that the solve method processes it correctly.
	 * <p>
	 * Something in this method causes it to handle negatives wrong.
	 * @param frac The fraction to be processed.
	 * @return frac as an improper fraction.
	 */
	public static String processFrac (String frac) {
		int num, den;
		if (frac.indexOf("/") == -1) { // frac is a whole number
			return frac + "/1"; // Necessary because the solve method has a bug in it.
		} else {
			den = setDen(frac);
			log("Denominator is: " + den);
		}
		num = setNum(frac);
		if (frac.indexOf("_") != -1) { // frac is a mixed number
			num += Integer.parseInt(frac.substring(0, frac.indexOf("_"))) * den;
		}
		log("Numerator is: " + num);
		return num + "/" + den;
	}
	
	/**
	 * Takes a fraction as a {@code String} and returns the numerator as an {@code int}.
	 * @param s The fraction, formatted as a {@code String}, that is to be evaluated.
	 * @return The numerator of the fraction {@code s}.
	 */
	public static int setNum (String s) {
		int n;
		if (s.indexOf("_") != -1) { // mixed number
			n = Integer.parseInt(s.substring(s.indexOf("_") + 1, s.indexOf("/")));
		} else if (s.indexOf("/") == -1) { // Whole number
			n = Integer.parseInt(s);
		} else { // not a mixed number
			n = Integer.parseInt(s.substring(0, s.indexOf("/")));
		}
		return n;
	}

	/**
	 * Takes a fraction as a {@code String} and returns the denominator as an {@code int}.
	 * @param s The fraction, formatted as a {@code String}, that is to be evaluated.
	 * @return The denominator of the fraction {@code s}.
	 */
	public static int setDen (String s) {
		return Integer.parseInt(s.substring(s.indexOf("/") + 1, s.length()));
	}
	
	/**
	 * Evaluates the values of the fractions, in an expression, that are passed from the method {@code processInput}.
	 * @param frac1 One of the fractions that is processed.
	 * @param frac2 The other fraction that is processed.
	 * @param type The type of operation ({@code +}, {@code -}, {@code *}, {@code /}) that is used.
	 * @return The value of the expression that the user originally entered.
	 */
	public static String solve (String frac1, String frac2, char type) {
		int num = 0;
		int den = 0;
		String s = frac1;
		frac1 = compat(frac1, frac2);
		frac2 = compat(frac2, s);
		log("Compatible fraction 1: " + frac1);
		log("Compatible fraction 2: " + frac2);
		if (type == '+') {
			num = setNum(frac1) + setNum(frac2);
			den = setDen(frac1); // The denominator of frac2 should be the same.
		} else if (type == '-') {
			num = setNum(frac1) - setNum(frac2);
			den = setDen(frac1); // The denominator of frac1 should be the same.
		} else if (type == '*') {
			num = setNum(frac1) * setNum(frac2);
			den = setDen(frac1) * setDen(frac2);
		} else if (type == '/') {
			num = setNum(frac1) * setDen(frac2);
			den = setDen(frac1) * setNum(frac2);
		}
		log("Raw expression: " + frac1 + " " + type + " " + frac2);
		
		/* If necessary, modify the fraction
		 * so that we don't get something like
		 * 5_0/1 or 30/10
		 */
		int div = gcd(num, den); // greatest common divisor
		if (div != 1) { // reduce the fraction
			num /= div;
			den /= div;
		}
		String finalFrac = ""; 
		if (Math.abs(num) > Math.abs(den)) { // improper fraction
			int n = num / den;
			num -= n * den;
			if (num == den) { // e.g, 5_3/3
				finalFrac = "" + (n + 1);
			} else if (num == 0 && den == 1) { // e.g, 6_0/1
				finalFrac = "" + n;
			} else {
				if (("" + num).startsWith("-")) {
					finalFrac = n + "_" + -num + "/" + den;
				} else {
					finalFrac = n + "_" + num + "/" + den;
				}
			}
		} else if (num == den) {
			finalFrac = "1";
		} else { // normal fraction
			finalFrac = num + "/" + den;
		}
		return finalFrac;
	}
	
	/**
	 * Multiplies the numerators and denominators of both fractions so that they are compatible.
	 * @param frac1 One of the fractions that is processed.
	 * @param frac2 The other fraction that is processed.
	 * @return frac1 in a form that is compatible with frac2.
	 */
	public static String compat (String frac1, String frac2) {
		int n1 = setNum(frac1);
		int d1 = setDen(frac1);
		int d2 = setDen(frac2);
		n1 *= d2;
		d1 *= d2;
		return n1 + "/" + d1;
	}
	
	/**
	 * Calculates the greatest common denominators of two numbers. Is used to test if a fraction can be reduced.
	 * @param a The numerator of the fraction that is processed.
	 * @param b The denominator of the fraction that is processed.
	 * @return The greatest common denominator of the two numbers.
	 */
	public static int gcd (int a, int b) {
		a = Math.abs(a);
		b = Math.abs(b);
		boolean modB = true;
		while (a != 0 && b != 0) {
			if (modB) {
				b %= a;
				modB = false;
			} else {
				a %= b;
				modB = true;
			}
		}
		return Math.max(a, b);
	}
	
	/**
	 * Prints instructions. This method is called from the main method if the user inputs {@code help}, case-insensitive.
	 */
	public static void help () {
		System.out.println();
		System.out.println("This program calculates basic mathematical expressions");
		System.out.println("that are typed into the console.");
		System.out.println("The expressions must be mathematically valid.");
		System.out.println("(I.e, no text, symbols, etc.)");
		System.out.println("Only one operator is allowed.");
		System.out.println("This program does not handle parentheses.");
		System.out.println();
		System.out.println("With this program, it is easier");
		System.out.println("to use fractions in expressions");
		System.out.println("Because of a special feature:");
		System.out.println("numbers are typed using");
		System.out.println("the following syntax:");
		System.out.println();
		System.out.println("<number>_<numerator>/<denominator>");
		System.out.println();
		System.out.println("Variations on this are allowed:");
		System.out.println();
		System.out.println("<numerator>/<denominator>");
		System.out.println();
		System.out.println("<number>");
		System.out.println();
		System.out.println("Supposing that the above sytax");
		System.out.println("comprises a <number>, the syntax of");
		System.out.println("the overall expression is:");
		System.out.println();
		System.out.println("<number> <operator> <number>");
		System.out.println();
		System.out.println("It is, however, permitted to");
		System.out.println("simply type a single number.");
		System.out.println();
	}

	/**
	 * If the class constant {@code DEBUG} is set to {@code true}, print a message to the console.
	 * @param s The string to be printed.
	 */
	public static void log (String s) {
		if (debug) System.out.println(s + "\n");
	}
}