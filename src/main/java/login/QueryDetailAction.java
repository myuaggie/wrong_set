package login;

import java.security.PrivilegedAction;

public class QueryDetailAction implements PrivilegedAction{

    private String category;

    public QueryDetailAction(String category) {this.category=category;}

    public Object run(){
        CategoryPermission p=new CategoryPermission(category, "read");
        SecurityManager manager = System.getSecurityManager();
        if (manager != null)
            manager.checkPermission(p);
        System.out.println("read succussfully");
        return true;
    }

}
