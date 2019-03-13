package login;

import java.security.Permission;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CategoryPermission extends Permission {

    private String action;

    public CategoryPermission(String target, String anAction) {
        super(target);
        action = anAction;
        System.out.println(target);
        System.out.println("action:"+action);
    }

    public String getActions() { return action; }

    public boolean equals(Object other) {
        if (other == null) return false;
        if (!getClass().equals(other.getClass()))
            return false;
        CategoryPermission b = (CategoryPermission) other;
        if (!action.equals(b.action)) return false;
        if (action.equals("read"))
            return getName().equals(b.getName());
        else if (action.equals("avoid"))
            return wordSet().equals(b.wordSet());
        else return false;
    }

    public int hashCode()
    {
        return getName().hashCode() + action.hashCode();
    }

    public Set<String> wordSet()
    {
        Set<String> set = new HashSet<String>();
        set.addAll(Arrays.asList(getName().split(",")));
        return set;
    }

    public boolean implies(Permission other){
        System.out.println("imply");
        if (!(other instanceof CategoryPermission)) return false;
        CategoryPermission b = (CategoryPermission) other;
        if (action.equals("read"))
        {
            System.out.println("read premission & read action");
            return b.action.equals("read") &&
                    getName().contains(b.getName());
        }
        else if (action.equals("avoid")) {
            if (b.action.equals("avoid")) {
                System.out.println("avoid premission & avoid action");
                return b.wordSet().containsAll(wordSet());
            }
            else if (b.action.equals("read")) {
                System.out.println("avoid premission & read action");
                for (String badWord : wordSet()) {
                    if (b.getName().contains(badWord)) return false;
                }
                return true;
            }
            else return false;
        }
        else return false;

    }
}
