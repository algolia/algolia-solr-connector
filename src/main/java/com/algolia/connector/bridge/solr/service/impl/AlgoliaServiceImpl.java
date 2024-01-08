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
package com.algolia.connector.bridge.solr.service.impl;

import com.algolia.connector.bridge.solr.common.AlgoliaException;
import com.algolia.connector.bridge.solr.model.AlgoliaRecord;
import com.algolia.connector.bridge.solr.model.AlgoliaRequest;
import com.algolia.connector.bridge.solr.service.AlgoliaService;
import com.algolia.connector.bridge.solr.util.AlgoliaClientManager;
import com.algolia.search.Defaults;
import com.algolia.search.SearchIndex;
import com.algolia.search.exceptions.AlgoliaApiException;
import com.algolia.search.models.indexing.BatchIndexingResponse;
import com.algolia.search.models.indexing.BatchResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.security.AccessController;

/**
 * @author Rakesh Kumar
 */
public class AlgoliaServiceImpl implements AlgoliaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public AlgoliaServiceImpl(String applicationId, String writeKey) {
        AlgoliaClientManager.getInstance().initSearchClient(applicationId, writeKey);
    }

    @Override
    public String createRecord(AlgoliaRequest request) {
        SearchIndex<AlgoliaRecord> index = AlgoliaClientManager.getInstance()
                .getSearchIndex(request.getIndexName(), AlgoliaRecord.class);
            BatchIndexingResponse response = index.saveObject(request.getAlgoliaRecords().get(0));
        if (response != null && !response.getResponses().isEmpty()) {
            BatchResponse batchResponse = response.getResponses().get(0);

            return batchResponse.getObjectIDs().get(0);
        }
        return null;
    }

    @Override
    public CompletableFuture<List<String>> createRecordsAsync(AlgoliaRequest request) {
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Algolia create record payload: \n {}", Defaults.getObjectMapper()
                        .writerWithDefaultPrettyPrinter()
                        .writeValueAsString(request.getAlgoliaRecords()));
            }
            SearchIndex<AlgoliaRecord> index = AlgoliaClientManager.getInstance()
                    .getSearchIndex(request.getIndexName(), AlgoliaRecord.class);
            return index.saveObjectsAsync(request.getAlgoliaRecords())
                    .thenApplyAsync(response -> response.getResponses().isEmpty()
                            ? Collections.emptyList()
                            : response.getResponses().get(0).getObjectIDs());
        } catch (AlgoliaApiException | JsonProcessingException ex) {
            throw new AlgoliaException(ex);
        }
    }

    @Override
    public CompletableFuture<List<String>> deleteRecordsAsync(AlgoliaRequest request) {
        try {
            SearchIndex<AlgoliaRecord> index = AlgoliaClientManager.getInstance()
                    .getSearchIndex(request.getIndexName(), AlgoliaRecord.class);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Algolia delete record payload: \n {}", Defaults.getObjectMapper()
                        .writerWithDefaultPrettyPrinter()
                        .writeValueAsString(request.getAlgoliaRecords()));
            }
            List<String> recordIds = request.getAlgoliaRecords()
                    .stream()
                    .map(AlgoliaRecord::getObjectID)
                    .collect(Collectors.toList());
            return index.deleteObjectsAsync(recordIds)
                    .thenApplyAsync(response -> response.getResponses().isEmpty()
                            ? Collections.emptyList()
                            : response.getResponses().get(0).getObjectIDs());
        } catch (AlgoliaApiException | JsonProcessingException ex) {
            throw new AlgoliaException(ex);
        }
    }
}
