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
package com.algolia.connector.bridge.solr.util;

import com.algolia.search.DefaultSearchClient;
import com.algolia.search.SearchClient;
import com.algolia.search.SearchConfig;
import com.algolia.search.SearchIndex;
import org.apache.solr.core.CloseHook;
import org.apache.solr.core.SolrCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

/**
 * @author Rakesh Kumar
 */
public class AlgoliaClientManager extends CloseHook {

    static AlgoliaClientManager instance;

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String USER_AGENT_HEADER = "User-Agent";

    private SearchClient searchClient;

    public void initSearchClient(String applicationId, String writeKey) {
        if (this.searchClient == null) {
            LOGGER.info("Initializing Algolia SearchClient for applicationId({})", applicationId);
            this.searchClient = DefaultSearchClient.create(new SearchConfig.Builder(applicationId, writeKey)
                    .addExtraHeaders(USER_AGENT_HEADER, "algolia-solr-0.0.1")
                    .build());
        }
    }

    public SearchClient getSearchClient() {
        return searchClient;
    }

    public <T> SearchIndex<T> getSearchIndex(String indexName, Class<T> type) {
        return this.searchClient.initIndex(indexName, type);
    }

    public static AlgoliaClientManager getInstance() {
        return instance;
    }

    //--------------------------------- CloseHook Methods ---------------------------------

    @Override
    public void preClose(SolrCore core) {
        if (this.searchClient != null) {
            LOGGER.info("Closing Algolia SearchClient for applicationId({})", this.searchClient.getConfig()
                    .getApplicationID());
            try {
                this.searchClient.close();
            } catch (IOException ex) {
                LOGGER.error("Exception while closing Algolia SearchClient!", ex);
            }
        }
    }

	@Override
	public void postClose(SolrCore core) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'postClose'");
	}
}
