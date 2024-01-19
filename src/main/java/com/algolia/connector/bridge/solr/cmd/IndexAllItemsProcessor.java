package com.algolia.connector.bridge.solr.cmd;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

import com.algolia.connector.bridge.solr.model.AlgoliaRecord;
import com.algolia.connector.bridge.solr.model.AlgoliaRequest;
import com.algolia.search.DefaultSearchClient;
import com.algolia.search.SearchClient;
import com.algolia.search.SearchConfig;
import com.algolia.search.SearchIndex;
import com.algolia.search.models.indexing.BatchIndexingResponse;
import com.google.gson.*;

public class IndexAllItemsProcessor {
    
    private static final String USER_AGENT_HEADER = "User-Agent";


    private static ObjectsReturn getItems(String solrUrl, int start, int rows) throws Exception {

        String urlString = String.format("%s/select?q=*:*&start=%s&rows=%s", solrUrl, start, rows);
        System.out.println("Getting items " + urlString);
            
        URLConnection request = new URL(urlString).openConnection();
        request.connect();
        InputStream stream = (InputStream)request.getContent();
        InputStreamReader inputStreamReader = new InputStreamReader(stream);

        // map to GSON objects
        JsonElement root = JsonParser.parseReader(inputStreamReader);
        JsonObject rootObject = root.getAsJsonObject();
        JsonObject responseObject = rootObject.get("response").getAsJsonObject();
        int numFound = responseObject.get("numFound").getAsInt();
        JsonArray docs = responseObject.get("docs").getAsJsonArray();
        return new ObjectsReturn(numFound, docs);
    }

    private static SearchClient getSearchClient(String appId, String writeKey) {
        SearchClient searchClient = DefaultSearchClient.create(new SearchConfig.Builder(appId, writeKey)
                    .addExtraHeaders(USER_AGENT_HEADER, "algolia-solr-0.0.1")
                    .build()); 
        return searchClient;       
    }

    private static void setItemsToAlgolia(SearchIndex<AlgoliaRecord> index, JsonArray items) {

        AlgoliaRequest request = new AlgoliaRequest("");
        for ( int i = 0;i < items.size();i++) {
            JsonObject docObject = items.get(i).getAsJsonObject();
            Set<String> keySet = docObject.keySet();

            String id = docObject.get("id").getAsString();
            AlgoliaRecord algoliaRecord = new AlgoliaRecord(id);
            for ( String name : keySet) {
                JsonElement fieldObject = docObject.get(name);
                if ( "id".equals(name) ) {
                    continue;
                }
                algoliaRecord.put(name, fieldObject.getAsString());
            }
            request.addAlgoliaRecord(algoliaRecord);
        }
        BatchIndexingResponse response = index.saveObjects(request.getAlgoliaRecords());
        System.out.println("Batch index saved, waiting for indexing to finish");
        response.waitTask();
        System.out.println("Saved " + response.getResponses().get(0).getObjectIDs().size() + " records to algolia");
    }



    public static void main( String[] args ) throws Exception {


        try {
            Properties prop = new Properties();
            prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("mvn.properties"));                        
            String solrUrl = prop.getProperty("solr.url"); 
            String appId = prop.getProperty("algolia.application.id");
            String writeKey = prop.getProperty("algolia.write.key");
            String indexName = prop.getProperty("algolia.index.name");

            SearchClient searchClient = getSearchClient(appId, writeKey);
            SearchIndex<AlgoliaRecord> index = searchClient.initIndex(indexName, AlgoliaRecord.class);
            
            int start = 0;
            int rows = 100;
            int totalPages = 0;

            do {
                ObjectsReturn objectsReturn = getItems(solrUrl, start, rows);
                int numRecords = objectsReturn.getReturnCount();
                Double pageCount = Math.floor(numRecords);
                totalPages = pageCount.intValue();
                start = start + rows;

                JsonArray items = objectsReturn.getItems();
                System.out.println("Got items " + items.size());
                setItemsToAlgolia(index, items);


            } while (totalPages >= start);

            System.out.println("All done processing records to " + indexName);


        } catch(Exception e) {
            System.out.println("Some error occurred " + e.getMessage());
            e.printStackTrace();
        }
        System.exit(0);
    }

    private static class ObjectsReturn {

        private int returnCount;
        private JsonArray items;

        public ObjectsReturn(int returnCount, JsonArray items) {
            this.returnCount = returnCount;
            this.items = items;
        }

        public int getReturnCount() {
            return this.returnCount;
        }

        public JsonArray getItems() {
            return this.items;
        }
    }
}
