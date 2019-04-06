package search.config;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;


public class ElasticClient {

    public final static String HOST = "127.0.0.1";

    public final static int PORT = 9300;

    private static TransportClient client;

    public ElasticClient() throws Exception{
        setClient();
    }

    private void setClient() throws Exception{
        if (client==null) {
            client = new PreBuiltTransportClient(Settings.builder().put("cluster.name", "elasticsearch").build())
                    .addTransportAddresses(new InetSocketTransportAddress(
                            InetAddress.getByName(HOST), PORT));
        }
    }

    public TransportClient getClient(){
        return client;
    }
}
