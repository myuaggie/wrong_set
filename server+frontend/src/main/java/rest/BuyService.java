package rest;

import com.mongodb.*;
import message.BuyLogProducer;
import model.Question;
import model.ULKey;
import model.UQ_Library;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import questionset.QuestionSet;
import service.AppService;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import java.util.Date;
import java.util.Iterator;

@RestController
@RequestMapping("/buy")
public class BuyService {
    @Autowired
    private AppService appService;
    @Autowired
    private BuyLogProducer buyLogProducer;

    public void setAppService(AppService appService){
        this.appService=appService;
    }

    public void setBuyLogProducer(BuyLogProducer buyLogProducer) {this.buyLogProducer=buyLogProducer;}

    @RequestMapping(value="/{libraryId}/{id}/{userId}/{role}",method=RequestMethod.GET)
    public void buy(@PathVariable int libraryId, @PathVariable int id,
                    @PathVariable int userId, @PathVariable int role) throws Exception{
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

        if (role==2){
            userId=111111;
        }
        User u=appService.getUserById(userId);

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
            l.setUlKey(new ULKey(userId,lid));
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
        buyLogProducer.produce(userId,id);
    }
}
