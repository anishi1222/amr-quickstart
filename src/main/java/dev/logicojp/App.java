package dev.logicojp;

public class App
{
    public static void main(String... args ) {

        for(MIType type : MIType.values()) {
            System.out.printf(
            """
                *******\n\n
                execution started with %s\n\n
                *******
            """, type);
            try {
                JedisAppToken jedisApp = new JedisAppToken();
                jedisApp.go(type);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("JedisApp executed");

            try {
                JedisAppMI jedisAppMI = new JedisAppMI();
                jedisAppMI.go(type);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("JedisAppMI executed");

            try {
                LettuceAppToken lettuceApp = new LettuceAppToken();
                lettuceApp.go(type);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("LettuceApp executed");

            try {
                LettuceAppMI lettuceAppMI = new LettuceAppMI();
                lettuceAppMI.go(type);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("LettuceAppMI executed");
        }
        System.exit(0);
    }
}