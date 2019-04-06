package neo4j;

import net.sf.json.JSONArray;
import org.neo4j.driver.v1.*;

import java.util.ArrayList;
import java.util.List;

import static org.neo4j.driver.v1.Values.parameters;

public class PersonSocial {
    public void createUser(int id, String name) throws Exception{
        Driver driver=GraphDatabase.driver( "bolt://localhost:7687");
        Transaction tx = driver.session().beginTransaction();
        tx.run("create(:User{id:{ID},name:{NAME}})",
                parameters("ID",id,"NAME",name));
        tx.success();
        tx.close();
        driver.close();
    }

    public JSONArray getUser(int id) throws Exception{
        Driver driver=GraphDatabase.driver( "bolt://localhost:7687");
        Transaction tx = driver.session().beginTransaction();
        StatementResult result = tx.run("MATCH(a:User) WHERE a.id = {ID} RETURN a.id AS id,a.name AS name",
                parameters("ID",id));
        ArrayList<JSONArray> qJ= new ArrayList<JSONArray>();
        while(result.hasNext()){
            ArrayList<String> arrayList = new ArrayList<String>();
            Record record = result.next();
            arrayList.add(String.valueOf(record.get("id").asInt()));
            arrayList.add(record.get("name").asString());
            qJ.add(JSONArray.fromObject(arrayList));
            System.out.println(String.format("Id:%d Name:%s",record.get("id").asInt(),record.get("name").asString()));
        }
        tx.close();
        driver.close();
        return JSONArray.fromObject(qJ.toArray());
    }

    public void createFriendship(int id, String name, int id2, String name2) throws Exception{
        Driver driver=GraphDatabase.driver( "bolt://localhost:7687");
        Transaction tx = driver.session().beginTransaction();
        StatementResult r1=tx.run("MATCH(a:User) WHERE a.id = {ID} RETURN a.id",parameters("ID",id));
        if (!r1.hasNext()){
            createUser(id,name);
        }
        StatementResult r2=tx.run("MATCH(a:User) WHERE a.id = {ID} RETURN a.id",parameters("ID",id2));
        if (!r2.hasNext()){
            createUser(id2,name2);
        }
        tx.run("MATCH(a:User),(b:User) WHERE a.id={ID} AND b.id={ID2} CREATE (a)-[:Friendship]->(b)",
                parameters("ID",id,"ID2",id2));
        tx.success();
        tx.close();
        driver.close();
    }

    public JSONArray getFriendsList(int id){
        List<Boolean> res=new ArrayList<Boolean>();
        Driver driver=GraphDatabase.driver( "bolt://localhost:7687");
        Transaction tx = driver.session().beginTransaction();
        StatementResult result=tx.run("MATCH (b:User)-[:Friendship]->(a) WHERE b.id={ID} RETURN a.id AS id,a.name AS name",
                parameters("ID",id));
        ArrayList<JSONArray> qJ= new ArrayList<JSONArray>();
        while(result.hasNext()){
            ArrayList<String> arrayList = new ArrayList<String>();
            Record record = result.next();
            arrayList.add(String.valueOf(record.get("id").asInt()));
            arrayList.add(record.get("name").asString());
            qJ.add(JSONArray.fromObject(arrayList));
            System.out.println(String.format("Id:%d Name:%s",record.get("id").asInt(),record.get("name").asString()));
        }
        tx.close();
        driver.close();
        return JSONArray.fromObject(qJ.toArray());
    }

    public Boolean isFriend(int id, int id2){
        List<Boolean> res=new ArrayList<Boolean>();
        Driver driver=GraphDatabase.driver( "bolt://localhost:7687");
        Transaction tx = driver.session().beginTransaction();
        StatementResult result=tx.run("MATCH (b:User)-[:Friendship]->(a) WHERE b.id={ID} AND a.id={ID2} RETURN a.id AS id",
                parameters("ID",id,"ID2",id2));
        return result.hasNext();
    }
}
