/* HelloWorld.java */

public class HelloWorld {
    native String helloFromC(); /* (1) */
    static {
        System.loadLibrary("pillowtalk"); /* (2) */
        System.loadLibrary("choicenet"); /* (2) */
    }
    static public void main(String argv[]) {
        HelloWorld helloWorld = new HelloWorld();
        //helloWorld.helloFromC(); /* (3) */
        String text = helloWorld.helloFromC(); /* (3) */
        System.out.println("Got Response from Planner\n"); /* (3) */
        System.out.println(text); /* (3) */
    }
}
