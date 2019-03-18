package action;

import model.Question;
import model.UQ_Library;
import model.User;
import net.sf.json.JSONArray;
import service.AppService;

import javax.swing.text.html.HTMLDocument;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CountAction extends BaseAction{
    private static final long serialVersionUID = 1L;

    private AppService appService;

    public void setAppService(AppService appService){
        this.appService=appService;
    }

    public String queryCountAll() throws Exception{
        PrintWriter out = response().getWriter();
        out.println(appService.getAllQuestions().size());
        out.flush();
        out.close();
        return null;
    }

    public String queryCountAllByUser() throws Exception{
        List<User> res=appService.getAllUsers();
        PrintWriter out = response().getWriter();
        Iterator it = res.iterator();
        ArrayList<JSONArray> qJ= new ArrayList<JSONArray>();
        while (it.hasNext()) {
            User u=(User)it.next();
            if (u.getId()==111111) continue;
            ArrayList<String> arrayList = new ArrayList<String>();
            arrayList.add(String.valueOf(u.getId()));
            arrayList.add(String.valueOf(appService.getAllLibrariesById(u.getId()).size()));
            qJ.add(JSONArray.fromObject(arrayList));
        }
        JSONArray q=JSONArray.fromObject(qJ.toArray());
        out.println(q);
        out.flush();
        out.close();
        return null;
    }

    public String queryCountFre() throws Exception{
        PrintWriter out = response().getWriter();
        List<Question> res=appService.getAllQuestions();
        Iterator it = res.iterator();
        ArrayList<JSONArray> qJ= new ArrayList<JSONArray>();
        int count;
        while (it.hasNext()) {
            Question q=(Question)it.next();
            count=0;
            List<UQ_Library> ls=appService.getAllLibrariesByQuestion(q.getId());
            Iterator itl=ls.iterator();
            while (itl.hasNext()){
                UQ_Library l=(UQ_Library)itl.next();
                count+=l.getFrequency();
            }
            ArrayList<String> arrayList = new ArrayList<String>();
            arrayList.add(String.valueOf(q.getId()));
            arrayList.add(String.valueOf(count));
            qJ.add(JSONArray.fromObject(arrayList));
        }
        JSONArray q=JSONArray.fromObject(qJ.toArray());
        out.println(q);
        out.flush();
        out.close();
        return null;
    }

    public String queryCountTags() throws Exception{
        List<User> res=appService.getAllUsers();
        PrintWriter out = response().getWriter();
        Iterator it = res.iterator();
        ArrayList<JSONArray> qJ= new ArrayList<JSONArray>();
        while (it.hasNext()) {
            User u=(User)it.next();
            if (u.getId()==111111) continue;
            ArrayList<String> arrayList = new ArrayList<String>();
            arrayList.add(String.valueOf(u.getId()));
            List<UQ_Library> ls=appService.getAllLibrariesById(u.getId());
            Iterator itt=ls.iterator();
            String listt="";
            while (itt.hasNext()){
                UQ_Library l=(UQ_Library)itt.next();
                if (!listt.contains(l.getTagOne())){
                    listt+=l.getTagOne();
                    listt+=" ";
                }
                if (!listt.contains(l.getTagTwo())){
                    listt+=l.getTagTwo();
                    listt+=" ";
                }
            }
            arrayList.add(listt);
            qJ.add(JSONArray.fromObject(arrayList));
        }
        JSONArray q=JSONArray.fromObject(qJ.toArray());
        out.println(q);
        out.flush();
        out.close();
        return null;
    }
}
