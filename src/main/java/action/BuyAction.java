package action;

import com.mongodb.*;
import model.Question;
import model.ULKey;
import model.UQ_Library;
import model.User;
import net.sf.json.JSONArray;
import questionset.Library;
import questionset.QuestionSet;
import service.AppService;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import java.io.PrintWriter;
import java.util.*;


public class BuyAction extends BaseAction{
    private static final long serialVersionUID = 1L;

    private AppService appService;

    private int libraryId;
    private int k;
    private int id;


    public void setAppService(AppService appService){
        this.appService=appService;
    }

    public int getLibraryId() {
        return libraryId;
    }

    public void setLibraryId(int libraryId) {
        this.libraryId = libraryId;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTopKSaleLibraries() throws Exception {
        Context namingContext = new InitialContext();

        System.out.print("RMI registry bindings: ");
        NamingEnumeration<NameClassPair> e = namingContext.list("rmi://localhost/");
        while (e.hasMore())
            System.out.println(e.next().getName());
        HashSet<String> lib_set= new HashSet<String>();
        Object o=request().getSession()
                .getAttribute("userid");
        if (o!=null) {
            int owner = Integer.parseInt(o.toString());
            if (owner != -1) {
                List<UQ_Library> list_lib = appService.getAllLibrariesById(owner);
                Iterator it = list_lib.iterator();
                while (it.hasNext()) {
                    UQ_Library temp_lib = (UQ_Library) it.next();
                    lib_set.add(temp_lib.getTagOne());
                }
            }
        }
        String url = "rmi://localhost:1099/questionset";
        QuestionSet questionSet = (QuestionSet) namingContext.lookup(url);

        ArrayList<Library> llib= questionSet.getTopKSaleLibraries(k);
        Iterator it=llib.iterator();
        PrintWriter out = response().getWriter();
        ArrayList<JSONArray> qJ= new ArrayList<JSONArray>();
        while (it.hasNext()){
            questionset.Library l=(Library)it.next();
            if (lib_set.contains(l.getName())) continue;
            ArrayList<String> arrayList = new ArrayList<String>();
            arrayList.add(String.valueOf(l.getId()));
            arrayList.add(String.valueOf(l.getName()));
            arrayList.add(String.valueOf(l.getPrice()));
            arrayList.add(String.valueOf(l.getCategory()));
            arrayList.add(String.valueOf(l.getCount()));
            arrayList.add(String.valueOf(l.getSales()));
            qJ.add(JSONArray.fromObject(arrayList));
        }
        JSONArray q=JSONArray.fromObject(qJ.toArray());
        out.println(q);
        out.flush();
        out.close();
        return null;
    }

    public String buyLibrary() throws Exception{
        Context namingContext = new InitialContext();

        System.out.print("RMI registry bindings: ");
        NamingEnumeration<NameClassPair> e = namingContext.list("rmi://localhost/");
        while (e.hasMore())
            System.out.println(e.next().getName());

        String url = "rmi://localhost:1099/questionset";
        QuestionSet questionSet = (QuestionSet) namingContext.lookup(url);

        questionset.Library l0 = questionSet.buyLibrary(id);
        System.out.println(id);
        System.out.println("match"+l0.getId());
        Iterator it=l0.getQuestionsIterator().iterator();

        int owner=Integer.parseInt(request().getSession()
                .getAttribute("userid").toString());
        User u=appService.getUserById(owner);

        int lid=libraryId;
        MongoClient mongoClient = new MongoClient();
        DB database = mongoClient.getDB("wrong_set");
        while (it.hasNext()){
            questionset.Question q0=(questionset.Question)it.next();
            Question q=new Question();
            q.setName(q0.getName());
            //q.setContent(content);
            q.setOwner(u);
            int qid=appService.addQuestion(q);
            UQ_Library l=new UQ_Library();
            l.setUlKey(new ULKey(owner,lid));
            lid+=1;
            l.setQuestion(q);
            l.setTagOne(String.valueOf(l0.getName()));
            l.setTagTwo("external_"+l0.getCategory());
            l.setFrequency(0);
            l.setDate(new Date());
            appService.addLibrary(l);

            DBCollection collection = database.getCollection("questions");
            DBObject demo = new BasicDBObject("question_id", qid)
                    .append("content",q0.getContent()).append("reference",null);
            collection.insert(demo);
        }
        database=null;
        mongoClient.close();
        return null;
    }
}
