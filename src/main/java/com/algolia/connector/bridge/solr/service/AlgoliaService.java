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
package com.algolia.connector.bridge.solr.service;

import com.algolia.connector.bridge.solr.model.AlgoliaRequest;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author Rakesh Kumar
 */
public interface AlgoliaService {

    String createRecord(final AlgoliaRequest request);

    CompletableFuture<List<String>> createRecordsAsync(final AlgoliaRequest request);

    CompletableFuture<List<String>> deleteRecordsAsync(final AlgoliaRequest request);
}
