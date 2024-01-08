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
package com.algolia.connector.bridge.solr.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rakesh Kumar
 */
public class AlgoliaRequest {

    private final String indexName;

    private final List<AlgoliaRecord> records;

    public AlgoliaRequest(String indexName) {
        this.indexName = indexName;
        this.records = new ArrayList<>();
    }

    public String getIndexName() {
        return indexName;
    }

    public List<AlgoliaRecord> getAlgoliaRecords() {
        return records;
    }

    public AlgoliaRequest addAlgoliaRecord(AlgoliaRecord algoliaRecord) {
        this.records.add(algoliaRecord);
        return this;
    }
}
