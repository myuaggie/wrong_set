package login;

import java.security.Principal;

public class SimplePrincipal implements Principal{

  //  private String descr;
    private String value;

    public SimplePrincipal( String value) {
      //  this.descr = descr;
        this.value = value;
      //  System.out.println("get principal:"+descr+"="+value);
    }

    public String getName() {
       // return descr + "=" + value;
        return value;
    }

    public String toString() {
       // return descr + "=" + value;
        return value;
    }

    public boolean equals(Object otherObject) {
        System.out.println("principal equals");
        if (this == otherObject) {
            System.out.println(":yes");
            return true;
        }
        if (otherObject == null) {
            System.out.println(":null");
            return false;
        }
        if (getClass() != otherObject.getClass()) {
           // if (pe.getPrincipalName().equals(getName())) return true;
            System.out.println(":different class:"+otherObject.getClass().getName());
            return false;
        }
        SimplePrincipal other = (SimplePrincipal) otherObject;
        boolean res=getName().equals(other.getName());
        System.out.println(getName()+":"+other.getName());
        if (res){
            System.out.println(":yes");
        }
        else System.out.println(":no");
        return res;
    }

    public int hashCode() {
        return getName().hashCode();
    }

}
