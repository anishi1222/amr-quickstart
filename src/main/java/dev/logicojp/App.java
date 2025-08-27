package dev.logicojp;


import java.util.logging.Logger;

public class App
{
    public static void main(String... args) {

        Logger logger = Logger.getLogger(App.class.getName());
        for(AMR_Constant.MIType type : AMR_Constant.MIType.values()) {
            System.out.printf(
                    """
                    ******************************************
                    execution started with %s
                    ******************************************
                    """, type);
            try {
                JedisAppToken jedisApp = new JedisAppToken();
                jedisApp.go(type);
            } catch (Exception e) {
                logger.severe(e.getLocalizedMessage());
            }
            System.out.println(
                    """
                    ******************************************
                    JedisApp executed
                    ******************************************
                    """);

            try {
                JedisAppMI jedisAppMI = new JedisAppMI();
                jedisAppMI.go(type);
            } catch (Exception e) {
                logger.severe(e.getLocalizedMessage());
            }
            System.out.println(
                    """
                    ******************************************
                    JedisAppMI executed
                    ******************************************
                    """);

            try {
                LettuceAppToken lettuceApp = new LettuceAppToken();
                lettuceApp.go(type);
            } catch (Exception e) {
                logger.severe(e.getLocalizedMessage());
            }
            System.out.println(
                    """
                    ******************************************
                    LettuceApp executed
                    ******************************************
                    """);

            try {
                LettuceAppMI lettuceAppMI = new LettuceAppMI();
                lettuceAppMI.go(type);
            } catch (Exception e) {
                logger.severe(e.getLocalizedMessage());
            }
            System.out.println(
                    """
                    ******************************************
                    LettuceAppMI executed
                    ******************************************
                    """);
        }
        System.exit(0);
    }
}