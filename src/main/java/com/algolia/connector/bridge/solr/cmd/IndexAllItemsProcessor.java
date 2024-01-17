package com.algolia.connector.bridge.solr.cmd;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.MapSolrParams;

public class IndexAllItemsProcessor {
    

    public static void main( String[] args ) throws Exception {


        try {
            Properties prop = new Properties();
            prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("mvn.properties"));                        
            String solrUrl = prop.getProperty("solr.url");    
            System.out.println(solrUrl);

            SolrClient solrClient = new Http2SolrClient.Builder(solrUrl).build();
            final Map<String, String> queryParamMap = new HashMap<String, String>();
            queryParamMap.put("q", "*:*");
            queryParamMap.put("sort", "id asc");
            MapSolrParams queryParams = new MapSolrParams(queryParamMap);

            final QueryResponse response = solrClient.query("algolia", queryParams);
            final SolrDocumentList documents = response.getResults();

            System.out.println("Found " + documents.getNumFound() + " documents");
            for(SolrDocument document : documents) {
                final String id = (String) document.getFirstValue("id");
                final String name = (String) document.getFirstValue("title");
                System.out.println("id: " + id + "; title: " + name);
            }

        } catch(Exception e) {
            System.out.println("Some error occurred");
            System.out.println(e);
        }
    }
}
