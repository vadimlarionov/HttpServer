package headers;

/**
 * Created by vadim on 26.08.15.
 */
public enum AllowedMethods {
    GET("GET"),
    HEAD("HEAD");

    private String name;

    AllowedMethods(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static boolean contains(String method) {
        for (AllowedMethods allowedMethod : AllowedMethods.values())
            if (allowedMethod.name.equals(method.toUpperCase()))
                return true;
        return false;
    }

    public static AllowedMethods getMethod(String method) throws IllegalArgumentException {
        return AllowedMethods.valueOf(method);
    }

}
