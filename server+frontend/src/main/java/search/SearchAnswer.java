package search;

import model.Record;
import net.sf.json.JSONArray;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import search.config.ElasticClient;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchAnswer {

    private ElasticClient elasticClient;

    public void setElasticClient(ElasticClient elasticClient) throws Exception{
        this.elasticClient = elasticClient;
       // CreateIndex();
    }

    public void CreateIndex() throws Exception{
//        IndicesExistsRequest ier=new IndicesExistsRequest();
//        ier.indices()
//        elasticClient.getClient().admin()
//                .indices().exists(IndicesExistsRequest())
        CreateIndexRequestBuilder cib=elasticClient.getClient().admin()
                .indices().prepareCreate("wrongset");
        XContentBuilder mapping = XContentFactory.jsonBuilder()
                .startObject()
                .startObject("properties") //设置之定义字段
                .startObject("recordId")
                .field("type","integer") //设置数据类型
                .endObject()
                .startObject("libraryId")
                .field("type","integer")
                .endObject()
                .startObject("data")
                .field("type","date")  //设置Date类型
                .field("format","yyyy-MM-dd") //设置Date的格式
                .endObject()
                .startObject("useId")
                .field("type","integer")
                .endObject()
                .startObject("content")
                .field("type","text")
                .endObject()
                .endObject()
                .endObject();
        cib.addMapping("answer", mapping);
        cib.execute().actionGet();
    }

    public void addAnswer(Record record, String answer) throws Exception {
        System.out.println("Add index");
        System.out.println("recordId:"+record.getUrKey().getRecordId()+",content:"+answer);
        IndexResponse response = elasticClient.getClient().prepareIndex("wrongset1", "answer")
                .setSource(XContentFactory.jsonBuilder()
                        .startObject().field("recordId", new Integer(record.getUrKey().getRecordId()))
                        .field("libraryId", new Integer(record.getUrKey().getLibraryId()))
                        .field("userId", new Integer(record.getUrKey().getUserId()))
                        .field("date", record.getDate())
                        .field("content", answer)
                        .endObject()).execute().get();
        System.out.println(response.getId());

    }

    public JSONArray queryAnswer(int userId, int libraryId, String text) throws Exception {
        System.out.println("query userId:"+userId+",libraryId:"+libraryId+",text:"+text);
        QueryBuilder builder = QueryBuilders.matchPhraseQuery("userId", userId);
        QueryBuilder builder1 = QueryBuilders.matchPhraseQuery("libraryId", libraryId);
        QueryBuilder builder2 = QueryBuilders.fuzzyQuery("content", text);
        QueryBuilder b = QueryBuilders.boolQuery().must(builder).must(builder1).must(builder2);
        SearchResponse response = elasticClient.getClient().prepareSearch("wrongset1")
                .setQuery(b).get();
        SearchHits hits = response.getHits();
        ArrayList<JSONArray> qJ= new ArrayList<JSONArray>();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
            //将获取的值转换成map的形式
            Map<String, Object> map = hit.getSourceAsMap();
            ArrayList<String> arrayList = new ArrayList<String>();

            for (String key : map.keySet()) {
                if (key.equals("recordId")||key.equals("date")||key.equals("content"))
                    arrayList.add(String.valueOf(map.get(key)));
                System.out.println(key + " key对应的值为：" + map.get(key));
            }
            qJ.add(JSONArray.fromObject(arrayList));
        }
        return JSONArray.fromObject(qJ.toArray());
    }
}
    //    @Autowired
//    private RecordRepository recordRepository;
//    @Autowired
//    private TransportClient client;
//
//    private ElasticsearchTemplate elasticsearchTemplate;
//
//    SearchAnswer(){
//      //  createIndex();
//    }
//
//    public void setRecordRepository(RecordRepository recordRepository) {
//        this.recordRepository = recordRepository;
//    }
//
//    public void setClient(TransportClient client) {
//        this.client = client;
//    }
//
//    public void setElasticsearchTemplate(ElasticsearchTemplate elasticsearchTemplate) {
//        this.elasticsearchTemplate = elasticsearchTemplate;
//    }
//
//    public void createIndex(){
//        System.out.println(elasticsearchTemplate);
//        if (!elasticsearchTemplate.indexExists(Answer.class)) {
//            elasticsearchTemplate.createIndex(Answer.class);
//            elasticsearchTemplate.putMapping(Answer.class);
//        }
//    }
//
//    public void save(Record record) {
//        Answer ans=new Answer();
//        ans.setRecordId(record.getUrKey().getRecordId());
//        ans.setLibraryId(record.getUrKey().getLibraryId());
//        ans.setUserId(record.getUrKey().getUserId());
//        ans.setDate(record.getDate());
//        ans.setContent(record.getAnswer().toString());
//        recordRepository.save(ans);
//    }
//
//    public List<Answer> query(int userId, int libraryId, String text){
//        System.out.println("Query---userId:"+userId+",libraryId:"+libraryId+",text:"+text);
//        return recordRepository.findByUserIdAndLibraryIdAndContentContaining(userId,libraryId,text);
//    }
//
//    public List<Answer> queryALL(int userId){
//        System.out.println("QueryAll---userId:"+userId);
//        System.out.println(recordRepository.findAllByUserId(userId));
//        return recordRepository.findAllByUserId(userId);
//    }

