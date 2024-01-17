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

import com.algolia.connector.bridge.solr.util.AlgoliaClientManager;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.SolrCore;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.update.processor.UpdateRequestProcessor;
import org.apache.solr.update.processor.UpdateRequestProcessorFactory;
import org.apache.solr.util.plugin.SolrCoreAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

/**
 * @author Rakesh Kumar
 */
public class AlgoliaUpdateRequestProcessorFactory extends UpdateRequestProcessorFactory implements SolrCoreAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private String applicationId;

    private String writeKey;

    private String indexName;

    @Override
    @SuppressWarnings({"rawtypes"})
    public void init(NamedList args) {
        this.applicationId = (String) args.get("applicationId");
        this.writeKey = (String) args.get("writeKey");
        this.indexName = (String) args.get("indexName");
        super.init(args);
    }

    @Override
    public UpdateRequestProcessor getInstance(SolrQueryRequest req, SolrQueryResponse rsp, UpdateRequestProcessor next) {
        LOGGER.info("Creating AlgoliaUpdateRequestProcessor instance with next URP({})", next);
        return new AlgoliaUpdateRequestProcessor(this.applicationId, this.writeKey, this.indexName, next);
    }

    @Override
    public void inform(SolrCore core) {
        core.addCloseHook(AlgoliaClientManager.getInstance());
    }
}
