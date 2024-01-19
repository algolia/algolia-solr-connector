/*
################################################################################
#                                                                              #
#    Copyright 2023, Algolia (https://www.algolia.com)                         #
#                                                                              #
#    All rights reserved.                                                      #
#    Algolia PROPRIETARY/CONFIDENTIAL.                                         #
#                                                                              #
################################################################################
*/
package com.algolia.connector.bridge.solr.plugin;

import com.algolia.connector.bridge.solr.model.AlgoliaRecord;
import com.algolia.connector.bridge.solr.model.AlgoliaRequest;
import com.algolia.connector.bridge.solr.service.AlgoliaService;
import com.algolia.connector.bridge.solr.service.impl.AlgoliaServiceImpl;
import com.algolia.search.Defaults;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.apache.solr.update.AddUpdateCommand;
import org.apache.solr.update.DeleteUpdateCommand;
import org.apache.solr.update.processor.UpdateRequestProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author Rakesh Kumar
 */
public class AlgoliaUpdateRequestProcessor extends UpdateRequestProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final String SOLR_DOCUMENT_ID_NAME = "id";

    private final String indexName;

    private final AlgoliaService algoliaService;

    public AlgoliaUpdateRequestProcessor(String applicationId,
                                         String writeKey, String indexName, UpdateRequestProcessor next) {
        super(next);
        this.indexName = indexName;
        this.algoliaService = new AlgoliaServiceImpl(applicationId, writeKey);
    }

    @Override
    public void processAdd(AddUpdateCommand cmd) throws IOException {
        SolrInputDocument document = cmd.getSolrInputDocument();
        Object id = document.getFieldValue(SOLR_DOCUMENT_ID_NAME);
        if (id == null) {
            LOGGER.warn("id is missing from payload, not proceeding ahead with creation of AlgoliaRequest!!");
        } else {
            LOGGER.info("Handling add request for id: {}", id);
            AlgoliaRequest request = this.getAlgoliaRequest(document, id);
            try {
                /*this.algoliaService.createRecordsAsync(request)
                        .whenCompleteAsync((objectIds, throwable) -> {
                            if (objectIds != null && !objectIds.isEmpty()) {
                                LOGGER.info("Total record(s) added/updated: {}", objectIds.size());
                            } else if (throwable != null) {
                                LOGGER.error(throwable.getMessage(), throwable);
                            }
                        });*/
                String resp = this.algoliaService.createRecord(request);
                LOGGER.info("Algolia response: {}", resp);
                String value = Defaults.getObjectMapper()
                        .writeValueAsString(new AlgoliaRecord(UUID.randomUUID().toString()));
                LOGGER.info("ObjectMapper test value: {}", value);
            } catch (Exception ex) {
                LOGGER.warn("An error occurred {}", ex);
            }
        }
        super.processAdd(cmd);
    }

    private AlgoliaRequest getAlgoliaRequest(SolrInputDocument document, Object id) {
        AlgoliaRecord algoliaRecord = new AlgoliaRecord(id.toString());
        for (String fieldName : document.getFieldNames()) {
            SolrInputField field = document.getField(fieldName);
            Object value = field.getValue();
            if (fieldName.equals(SOLR_DOCUMENT_ID_NAME)) {
                continue;
            }
            LOGGER.info("Input field name({}):value({})", fieldName, value);
            algoliaRecord.put(fieldName, value);
        }
        return new AlgoliaRequest(this.indexName).addAlgoliaRecord(algoliaRecord);
    }

    @Override
    public void processDelete(DeleteUpdateCommand cmd) throws IOException {
        String id = cmd.getId();
        LOGGER.info("Handling delete request for id: {}", id);
        AlgoliaRequest request = new AlgoliaRequest(this.indexName).addAlgoliaRecord(new AlgoliaRecord(id));
        try {
            CompletableFuture<List<String>> cf = this.algoliaService.deleteRecordsAsync(request);
            cf.whenCompleteAsync((objectIds, throwable) -> {
                if (objectIds != null && !objectIds.isEmpty()) {
                    LOGGER.info("Total record(s) deleted: {}", objectIds.size());
                } else if (throwable != null) {
                    LOGGER.error(throwable.getMessage(), throwable);
                }
            }).exceptionally(throwable -> {
                LOGGER.error(throwable.getMessage(), throwable);
                return null;
            });
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        super.processDelete(cmd);
    }
}
