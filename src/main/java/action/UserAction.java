package action;

import encode.MD5Util;
import model.User;
import net.sf.json.JSONArray;
import service.AppService;

import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UserAction extends BaseAction {
    private static final long serialVersionUID = 1L;

    private AppService appService;
    private int id;
    private String username;
    private String password;
    private String email;
    private String phone;

    public void setAppService(AppService appService){
        this.appService=appService;
    }

    public void setId(int id){ this.id=id; }
    public int getId(){ return id; }

    public void setUsername(String username){ this.username=username; }
    public String getUsername(){ return username; }

    public void setPassword(String password){ this.password=password; }
    public String getPassword(){ return password; }

    public void setEmail(String email){ this.email=email; }
    public String getEmail(){ return email; }

    public void setPhone(String phone){ this.phone=phone; }
    public String getPhone(){ return phone; }

    public String register() throws Exception {
        PrintWriter out = response().getWriter();
        if (appService.getUserByPhone(phone)==null){
            User user=new User(username,MD5Util.md5Encode(password),email,phone);
            int id=appService.addUser(user);
            ArrayList<String> ur=new ArrayList<String>();
            ur.add(String.valueOf(id));
            out.println(JSONArray.fromObject(ur));
        }
        else{
            ArrayList<String> ur=new ArrayList<String>();
            ur.add("0");
            out.println(JSONArray.fromObject(ur));
        }
        out.flush();
        out.close();
        return null;
    }

    public String login() throws Exception{
        PrintWriter out = response().getWriter();
        User user=appService.getUserById(id);
        if (user!=null){
            if (user.getValid()==0){
                ArrayList<String> ur=new ArrayList<String>();
                ur.add("-2");
                out.println(JSONArray.fromObject(ur));
            }
            else if (MD5Util.md5Encode(password).equals(user.getPassword())){
                HttpSession session=request().getSession();
                session.setAttribute("userid",user.getId());
                session.setAttribute("username",user.getUsername());
                session.setAttribute("phone",user.getPhone());
                session.setAttribute("email", user.getEmail());
                session.setAttribute("valid", user.getValid());
                ArrayList<String> ur=new ArrayList<String>();
                ur.add(String.valueOf(id));
                ur.add(user.getUsername());
                ur.add(user.getPhone());
                ur.add(user.getEmail());
                ur.add(String.valueOf(user.getValid()));
                out.println(JSONArray.fromObject(ur));
            }
            else{
                ArrayList<String> ur=new ArrayList<String>();
                ur.add("0");
                out.println(JSONArray.fromObject(ur));
            }
        }
        else{
            ArrayList<String> ur=new ArrayList<String>();
            ur.add("-1");
            out.println(JSONArray.fromObject(ur));
        }
        out.flush();
        out.close();
        return null;
    }

    public String logout() throws Exception{
        request().getSession().setAttribute("userid",-1);
        return null;
    }

    public String getUserState() throws Exception{
        PrintWriter out = response().getWriter();
        HttpSession session=request().getSession();
        Object o=session.getAttribute("userid");
        if (o==null || session.getAttribute("userid").toString().equals("-1")){
            ArrayList<String> ur=new ArrayList<String>();
            ur.add("-1");
            out.println(JSONArray.fromObject(ur));
        }
        else {
            ArrayList<String> ur=new ArrayList<String>();
            ur.add(session.getAttribute("userid").toString());
            ur.add(session.getAttribute("username").toString());
            ur.add(session.getAttribute("phone").toString());
            ur.add(session.getAttribute("email").toString());
            ur.add(session.getAttribute("valid").toString());
            out.println(JSONArray.fromObject(ur));
        }
        out.flush();
        out.close();
        return null;
    }

    public String querySessionUser() throws Exception{
        PrintWriter out = response().getWriter();
        HttpSession session=request().getSession();
        ArrayList<String> ur=new ArrayList<String>();
        ur.add(session.getAttribute("userid").toString());
        ur.add(session.getAttribute("username").toString());
        ur.add(session.getAttribute("phone").toString());
        ur.add(session.getAttribute("email").toString());
        ur.add(session.getAttribute("valid").toString());
        out.println(JSONArray.fromObject(ur));
        out.flush();
        out.close();
        return null;
    }

    public String queryAllUsers() throws Exception{
        List<User> res=appService.getAllUsers();
        Iterator it = res.iterator();
        PrintWriter out = response().getWriter();
        ArrayList<JSONArray> qJ= new ArrayList<JSONArray>();
        while (it.hasNext()) {

            User u=(User)it.next();
            if (u.getValid()==2) continue;
            ArrayList<String> arrayList = new ArrayList<String>();
            arrayList.add(String.valueOf(u.getId()));
            arrayList.add(u.getUsername());
            arrayList.add(u.getEmail());
            arrayList.add(u.getPhone());
            arrayList.add(String.valueOf(u.getValid()));
            qJ.add(JSONArray.fromObject(arrayList));
        }
        JSONArray q=JSONArray.fromObject(qJ.toArray());
        out.println(q);
        out.flush();
        out.close();
        return null;
    }

    public String deleteUser() throws Exception{
       User u=appService.getUserById(id);
       appService.deleteUser(u);
       return null;
    }

    public String invalidUser() throws Exception{
        User u=appService.getUserById(id);
        u.setValid(0);
        appService.updateUser(u);
        return null;
    }

    public String validUser() throws Exception{
        User u=appService.getUserById(id);
        u.setValid(1);
        appService.updateUser(u);
        return null;
    }
}
