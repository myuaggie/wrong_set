package login;

import java.security.PrivilegedAction;

public class SimpleAction implements PrivilegedAction {
    public SimpleAction(String propertyName) { this.propertyName = propertyName; }
    public Object run() {
        System.out.println(System.getProperty(propertyName));
        return System.getProperty(propertyName); }
    private String propertyName;
}
