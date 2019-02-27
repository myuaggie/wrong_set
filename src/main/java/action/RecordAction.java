package action;

import com.mongodb.*;
import model.Record;
import model.URKey;
import net.sf.json.JSONArray;
import service.AppService;

import java.io.PrintWriter;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class RecordAction extends BaseAction {
    private static final long serialVersionUID = 1L;

    private AppService appService;

    private int libraryId;
    private int recordId;
    private String answer;

    public void setAppService(AppService appService){
        this.appService=appService;
    }

    public int getLibraryId() { return libraryId; }
    public void setLibraryId(int libraryId) { this.libraryId = libraryId; }

    public int getRecordId() { return recordId; }
    public void setRecordId(int recordId) { this.recordId = recordId; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    //in:libraryId
    public String queryRecords() throws Exception{
        int owner=Integer.parseInt(request().getSession()
                .getAttribute("userid").toString());
        PrintWriter out = response().getWriter();
        List<Record> res=appService.getAllRecords(owner,libraryId);
        Iterator it = res.iterator();
        ArrayList<JSONArray> qJ= new ArrayList<JSONArray>();
        while (it.hasNext()) {
            Record rec = (Record) it.next();
            ArrayList<String> arrayList = new ArrayList<String>();
            arrayList.add(String.valueOf(rec.getUrKey().getRecordId()));
            arrayList.add(String.valueOf(rec.getDate()));
            qJ.add(JSONArray.fromObject(arrayList));
        }
        JSONArray q=JSONArray.fromObject(qJ.toArray());
        out.println(q);
        out.flush();
        out.close();
        return null;
    }

    //in:libraryId,recordId,answer
   public String addRecord() throws Exception{
       int owner=Integer.parseInt(request().getSession()
               .getAttribute("userid").toString());
       Record rec=new Record();
       rec.setUrKey(new URKey(owner,libraryId,recordId));
       //rec.setAnswer(appService.convertBlob(answer));
       rec.setDate(new Date());
       appService.addRecord(rec);
       MongoClient mongoClient = new MongoClient();
       DB database = mongoClient.getDB("wrong_set");
       DBCollection collection = database.getCollection("records");
       DBObject demo = new BasicDBObject("id",String.valueOf(owner)
               +"+"+String.valueOf(libraryId)+"+"+String.valueOf(recordId)).append("answer",answer);
       collection.insert(demo);
       database=null;
       mongoClient.close();
       return null;
   }

   //in:libraryId,recordId
   public String queryRecordDetails() throws Exception{
       int owner=Integer.parseInt(request().getSession()
               .getAttribute("userid").toString());
       PrintWriter out = response().getWriter();
       response().setContentType("text/html;charset=utf-8");
       Record r=appService.getRecordByKey(new URKey(owner,libraryId,recordId));
       //Blob blob=r.getAnswer();
       //String answer=new String(blob.getBytes((long)1, (int)blob.length()));
       MongoClient mongoClient = new MongoClient();
       DB database = mongoClient.getDB("wrong_set");
       DBCollection collection = database.getCollection("records");
       DBObject demo = new BasicDBObject("id",String.valueOf(owner)
               +"+"+String.valueOf(libraryId)+"+"+String.valueOf(recordId));
       DBObject obj=collection.findOne(demo);
       if (obj.get("answer")!=null){out.println(obj.get("answer"));}
       database=null;
       mongoClient.close();
       out.flush();
       out.close();
       return null;
   }
}
