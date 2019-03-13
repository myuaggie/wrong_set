package action;

import com.mongodb.*;
import login.QueryDetailAction;
import login.SimpleCallbackHandler;
import model.Question;
import model.ULKey;
import model.UQ_Library;
import model.User;
import net.sf.json.JSONArray;
import service.AppService;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.io.PrintWriter;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class LibraryAction extends BaseAction {
    private static final long serialVersionUID = 1L;

    private AppService appService;

    private int libraryId;
    private String name;
    private String content;
    private String reference;
    private String tagOne;
    private String tagTwo;
    private boolean frequency;
    private boolean date;
    private int questionId;
    private int userId;

    public void setAppService(AppService appService){
        this.appService=appService;
    }

    public int getLibraryId() { return libraryId; }
    public void setLibraryId(int libraryId) { this.libraryId = libraryId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public String getTagOne() { return tagOne; }
    public void setTagOne(String tagOne) { this.tagOne = tagOne; }

    public String getTagTwo() { return tagTwo; }
    public void setTagTwo(String tagTwo) { this.tagTwo = tagTwo; }

    public boolean isFrequency() { return frequency; }
    public void setFrequency(boolean frequency) { this.frequency = frequency; }

    public boolean isDate() { return date; }
    public void setDate(boolean date) { this.date = date; }

    public int getQuestionId() { return questionId; }
    public void setQuestionId(int questionId) { this.questionId = questionId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String queryUserLibraries() throws Exception{
        Object o= request().getSession().getAttribute("userid");
        int owner;
        if (o==null){owner=-1;}
        else {owner=Integer.parseInt(o.toString());}

        PrintWriter out = response().getWriter();

        if (owner==-1){
            ArrayList<JSONArray> qJ= new ArrayList<JSONArray>();
            ArrayList<String> ur=new ArrayList<String>();
            ur.add("-1");
            qJ.add(JSONArray.fromObject(ur));
            out.println(JSONArray.fromObject(qJ));
        }
        else{
            List<UQ_Library> res;
            //user
                res=appService.getAllLibrariesById(owner);

            Iterator it = res.iterator();
            ArrayList<JSONArray> qJ= new ArrayList<JSONArray>();
            while (it.hasNext()) {
                UQ_Library lib = (UQ_Library) it.next();
                ArrayList<String> arrayList = new ArrayList<String>();
                arrayList.add(String.valueOf(lib.getUlKey().getLibraryId()).trim());
                arrayList.add(lib.getQuestion().getName());
                arrayList.add(lib.getTagOne()+" "+lib.getTagTwo());
                arrayList.add(String.valueOf(lib.getFrequency()));
                arrayList.add(String.valueOf(lib.getDate()));
                arrayList.add(String.valueOf(lib.getQuestion().getOwner().getId()));
                qJ.add(JSONArray.fromObject(arrayList));
            }
            JSONArray q=JSONArray.fromObject(qJ.toArray());
            out.println(q);
        }
        out.flush();
        out.close();
        return null;
    }


    public String queryManagerLibraries() throws Exception{
        PrintWriter out = response().getWriter();
        response().setContentType("text/html;charset=utf-8");
        List<UQ_Library> res=appService.getAllLibraries();
        Iterator it = res.iterator();
        ArrayList<JSONArray> qJ= new ArrayList<JSONArray>();
        while (it.hasNext()) {
            UQ_Library lib = (UQ_Library) it.next();
            ArrayList<String> arrayList = new ArrayList<String>();
            arrayList.add(String.valueOf(lib.getUlKey().getLibraryId()).trim());
            arrayList.add(lib.getQuestion().getName());
            arrayList.add(lib.getTagOne()+" "+lib.getTagTwo());
            arrayList.add(String.valueOf(lib.getFrequency()));
            arrayList.add(String.valueOf(lib.getDate()));
            arrayList.add(String.valueOf(lib.getQuestion().getOwner().getId()));
            arrayList.add(String.valueOf(lib.getQuestion().getOwner().getUsername()));
            MongoClient mongoClient = new MongoClient();
            DB database = mongoClient.getDB("wrong_set");
            DBCollection collection = database.getCollection("questions");
            DBObject qry = new BasicDBObject("question_id",lib.getQuestion().getId());
            DBObject obj=collection.findOne(qry);
            arrayList.add(String.valueOf(obj.get("content")));
            arrayList.add(String.valueOf(obj.get("reference")));
            database=null;
            mongoClient.close();
            //arrayList.add(String.valueOf(lib.getQuestion().getContent()));
            //arrayList.add(String.valueOf(lib.getQuestion().getReference()));
            arrayList.add(String.valueOf(lib.getQuestion().getId()));
            qJ.add(JSONArray.fromObject(arrayList));
        }
        JSONArray q=JSONArray.fromObject(qJ.toArray());
        out.println(q);
        out.flush();
        out.close();
        return null;
    }

    public String queryCommonLibraries() throws Exception{
        PrintWriter out = response().getWriter();
        response().setContentType("text/html;charset=utf-8");
        List<UQ_Library> res=appService.getAllLibrariesById(111111);
        Iterator it = res.iterator();
        ArrayList<JSONArray> qJ= new ArrayList<JSONArray>();
        while (it.hasNext()) {
            UQ_Library lib = (UQ_Library) it.next();
            ArrayList<String> arrayList = new ArrayList<String>();
            arrayList.add(String.valueOf(lib.getUlKey().getLibraryId()).trim());
            arrayList.add(lib.getQuestion().getName());
            arrayList.add(lib.getTagOne()+" "+lib.getTagTwo());
            arrayList.add(String.valueOf(lib.getFrequency()));
            arrayList.add(String.valueOf(lib.getDate()));
            arrayList.add(String.valueOf(lib.getQuestion().getOwner().getId()));
            arrayList.add(String.valueOf(lib.getQuestion().getOwner().getUsername()));
            MongoClient mongoClient = new MongoClient();
            DB database = mongoClient.getDB("wrong_set");
            DBCollection collection = database.getCollection("questions");
            DBObject qry = new BasicDBObject("question_id",lib.getQuestion().getId());
            DBObject obj=collection.findOne(qry);
            arrayList.add(String.valueOf(obj.get("content")));
            arrayList.add(String.valueOf(obj.get("reference")));
            database=null;
            mongoClient.close();
            //arrayList.add(String.valueOf(lib.getQuestion().getContent()));
            //arrayList.add(String.valueOf(lib.getQuestion().getReference()));
            qJ.add(JSONArray.fromObject(arrayList));
        }
        JSONArray q=JSONArray.fromObject(qJ.toArray());
        out.println(q);
        out.flush();
        out.close();
        return null;
    }

    //in:libraryId
    public String addLibraries() throws Exception{
        int owner=Integer.parseInt(request().getSession()
                .getAttribute("userid").toString());
        User u=appService.getUserById(owner);
        Question q=new Question();
        q.setName(name);
        //q.setContent(content);
        q.setOwner(u);
        int qid=appService.addQuestion(q);

        UQ_Library l=new UQ_Library();
        l.setUlKey(new ULKey(owner,libraryId));
        l.setQuestion(q);
        l.setTagOne(tagOne);
        l.setTagTwo(tagTwo);
        l.setFrequency(0);
        l.setDate(new Date());
        appService.addLibrary(l);

        MongoClient mongoClient = new MongoClient();
        DB database = mongoClient.getDB("wrong_set");
        DBCollection collection = database.getCollection("questions");
        DBObject demo = new BasicDBObject("question_id", qid)
                .append("content",content).append("reference",null);
        collection.insert(demo);
        database=null;
        mongoClient.close();
        return null;
    }

    public String addPop() throws Exception{
        UQ_Library l=new UQ_Library();
        l.setUlKey(new ULKey(111111,libraryId));
        l.setQuestion(appService.getQuestionById(questionId));
        l.setTagOne(tagOne);
        l.setTagTwo(tagTwo);
        l.setFrequency(0);
        l.setDate(new Date());
        appService.addLibrary(l);
        return null;
    }

    public String deletePop() throws Exception{
        appService.deleteLibrary(appService.getLibraryByKey(new ULKey(111111,libraryId)));
        return null;
    }

    //in:libraryId
    public String deleteLibraries() throws Exception{
        int owner=Integer.parseInt(request().getSession()
                .getAttribute("userid").toString());

        UQ_Library l=appService.getLibraryByKey(new ULKey(owner,libraryId));
        Question q=l.getQuestion();
        appService.deleteLibrary(l);
        appService.deleteQuestion(q);
        MongoClient mongoClient = new MongoClient();
        DB database = mongoClient.getDB("wrong_set");
        DBCollection collection = database.getCollection("questions");
        DBObject demo = new BasicDBObject("question_id",q.getId());
        collection.remove(demo);
        database=null;
        mongoClient.close();
        return null;
    }

    public String deleteLibrariesByUser() throws Exception{
        UQ_Library l=appService.getLibraryByKey(new ULKey(userId,libraryId));
        Question q=l.getQuestion();
        appService.deleteLibrary(l);
        appService.deleteQuestion(q);
        MongoClient mongoClient = new MongoClient();
        DB database = mongoClient.getDB("wrong_set");
        DBCollection collection = database.getCollection("questions");
        DBObject demo = new BasicDBObject("question_id",q.getId());
        collection.remove(demo);
        database=null;
        mongoClient.close();
        return null;
    }
    //in: libraryId(name/content/reference)
    public String updateQuestions() throws Exception{
        int owner=Integer.parseInt(request().getSession()
                .getAttribute("userid").toString());

        UQ_Library l=appService.getLibraryByKey(new ULKey(owner,libraryId));
        Question q=l.getQuestion();
        if (name != null){
            q.setName(name);
        }
        if (content != null){
            //q.setContent(content);
            MongoClient mongoClient = new MongoClient();
            DB database = mongoClient.getDB("wrong_set");
            DBCollection collection = database.getCollection("questions");
            DBObject qry = new BasicDBObject("question_id",q.getId());
            DBObject demo = new BasicDBObject("$set",new BasicDBObject("content",content));
            collection.update(qry,demo);
            database=null;
            mongoClient.close();
        }
        if (reference != null){
            //q.setReference(reference);
            MongoClient mongoClient = new MongoClient();
            DB database = mongoClient.getDB("wrong_set");
            DBCollection collection = database.getCollection("questions");
            DBObject qry = new BasicDBObject("question_id",q.getId());
            DBObject demo = new BasicDBObject("$set",new BasicDBObject("reference",reference));
            collection.update(qry,demo);
            database=null;
            mongoClient.close();
        }
        appService.updateQuestion(q);

        return null;
    }

    //in: libraryId(tagOne/tagTwo/frequency/data)
    public String updateLibraries() throws Exception{
        int owner=Integer.parseInt(request().getSession()
                .getAttribute("userid").toString());

        UQ_Library l=appService.getLibraryByKey(new ULKey(owner,libraryId));

        if (tagOne != null){
            l.setTagOne(tagOne);
        }
        if (tagTwo != null){
            l.setTagTwo(tagTwo);
        }
        if (date){
            l.setDate(new Date());
        }
        if (frequency){
            l.setFrequency(l.getFrequency()+1);
        }
        appService.updateLibrary(l);

        return null;
    }

    //in:libraryId
    public String queryDetails() throws Exception{
        int owner=Integer.parseInt(request().getSession()
                .getAttribute("userid").toString());
        PrintWriter out = response().getWriter();
        response().setContentType("text/html;charset=utf-8");
        UQ_Library l=appService.getLibraryByKey(new ULKey(owner,libraryId));
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add(l.getQuestion().getName());

        MongoClient mongoClient = new MongoClient();
        DB database = mongoClient.getDB("wrong_set");
        DBCollection collection = database.getCollection("questions");
        DBObject demo = new BasicDBObject("question_id",l.getQuestion().getId());
        DBObject obj=collection.findOne(demo);
        if (obj.get("content")!=null) {arrayList.add(obj.get("content").toString());}
        else{arrayList.add(null);}
        if (obj.get("reference")!=null){ arrayList.add(obj.get("reference").toString());}
        else {arrayList.add(null);}
        database=null;
        mongoClient.close();
        //arrayList.add(l.getQuestion().getContent());
        //arrayList.add(l.getQuestion().getReference());
        arrayList.add(l.getQuestion().getOwner().getUsername());
        arrayList.add(l.getDate().toString());
        arrayList.add(String.valueOf(libraryId));
        //arrayList.add(String.valueOf(owner));
        arrayList.add(String.valueOf(l.getQuestion().getOwner().getId()));
        out.println(JSONArray.fromObject(arrayList));
        return null;
    }

    public String checkDetailPremission() throws Exception{
        PrintWriter out = response().getWriter();
        Object o= request().getSession().getAttribute("userid");
        String owner,pwd;

        if (o==null){
            out.println("0");
            out.flush();
            out.close();
            return null;
        }
        else {owner=o.toString();}
        if (owner.equals("-1")){
            out.println("0");
            out.flush();
            out.close();
            return null;
        }
        //pwd=request().getSession().getAttribute("password").toString();
        else {
            String role=request().getSession().getAttribute("valid").toString();
            if (this.tagTwo.contains("external")) {
                if (role.equals("2")) {
                    out.println("1");
                } else {
                    out.println("-1");
                }
            }
            else {
                out.println("1");
            }
        }

//        try {
//            System.setProperty("java.security.auth.login.config", "classes/jaas.config");
//            System.setProperty("java.security.policy", "classes/simple.policy");
//            System.setSecurityManager(new SecurityManager());
//            LoginContext context = new LoginContext("Login1",new SimpleCallbackHandler(owner,pwd.toCharArray()));
//            context.login();
//            System.out.println("Authentication successful.");
//            Subject subject = context.getSubject();
//            Iterator principalIterator = subject.getPrincipals().iterator();
//            System.out.println("Authenticated user has the following Principals:");
//            while (principalIterator.hasNext()) {
//                Principal p = (Principal)principalIterator.next();
//                System.out.println("\t" + p.toString());
//            }
//
//            System.out.println("User has " +
//                    subject.getPublicCredentials().size() +
//                    " Public Credential(s)");
//
//            PrivilegedAction action = new QueryDetailAction(this.tagTwo);
//            Subject.doAsPrivileged(subject, action, null);
//            context.logout();
//            out.println("1");
//        }
//        catch (LoginException e) {
//            out.println("-1");
//            out.flush();
//            out.close();
//            return null;
//        }

        out.flush();
        out.close();
        return null;
    }

}
