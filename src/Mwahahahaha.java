public class Mwahahahaha {
	public static boolean quit = false;
	public static void main (String[] args) {
		String[] s = new String[1];
		s[0] = "foo";
		if (quit) return;
		quit = true;
		System.out.println("foo");
		main(s);
	}
}
