package com.algolia.connector.hybris;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import java.util.Date;

public class SolrDocumentTest {

    private static final String SOLR_BASE_URL = "http://localhost:8983/solr/algolia";

    //@Disabled
    @Test
    public void createDocumentTest() throws Exception {
        try (SolrClient solrClient = new Http2SolrClient.Builder(SOLR_BASE_URL).build()) {
            SolrInputDocument document = new SolrInputDocument();
            document.addField("id", UUID.randomUUID().toString());
            document.addField("msg", "Solr says hi!!");
            document.addField("dateAdded", new Date().toString());
            solrClient.add(document);
            solrClient.commit(true, true);
        }
    }

    @Disabled
    @Test
    public void deleteDocumentTest() throws Exception {
        try (SolrClient solrClient = new Http2SolrClient.Builder(SOLR_BASE_URL).build()) {
            List<String> ids = new ArrayList<>();
            ids.add("e58e9a8f-9886-473e-8bc4-45708b9aff93");
            ids.add("0e152967-f264-43c5-a49e-96cfc8c0c293");
            ids.add("a3e7048d-b5c1-4a17-9e1a-e4d4f0f61a5c");
            ids.add("343660be-2f26-4069-a778-c987db16a4ec");
            ids.add("0c3edef6-4c06-44ab-aee5-d5b73f11c7df");
            ids.add("778f4526-7736-4614-a44a-eb57606c329c");
            ids.add("838782f7-b008-456f-970c-11ab516ceed0");
            ids.add("f5cae7a0-65a7-4f73-a9c5-c3ec50abddb0");
            solrClient.deleteById(ids);
            solrClient.commit(true, true);
        }
    }

    @Disabled
    @Test
    public void deleteAllDocumentsByQueryTest() throws Exception {
        try (SolrClient solrClient = new Http2SolrClient.Builder(SOLR_BASE_URL).build()) {
            solrClient.deleteByQuery("*:*");
            solrClient.commit(true, true);
        }
    }
}
