package ai.ll.tflitemnist;

public class Username {
    private static String username="UserName";
    public static void changeName(String name)
    {
        username=name;
    }
    public static String getName()
    {
        // Return the instance
        return username;
    }
}
