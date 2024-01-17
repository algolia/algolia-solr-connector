package com.algolia.connector.bridge.solr.plugin;

import java.lang.invoke.MethodHandles;

import org.apache.solr.common.util.NamedList;
import org.apache.solr.handler.RequestHandlerBase;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlgoliaIndexRequestProcessor extends RequestHandlerBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private String applicationId;
    private String writeKey;
    private String indexName;

    @Override
  public void init(NamedList args) {
    this.applicationId = (String) args.get("applicationId");
    this.writeKey = (String) args.get("writeKey");
    this.indexName = (String) args.get("indexName");
    LOGGER.info("appId: " + this.applicationId + ", index: " + this.indexName);
    super.init(args);
}

	@Override
	public void handleRequestBody(SolrQueryRequest req, SolrQueryResponse rsp) throws Exception {
		LOGGER.info("handleRequestBody(" + this.writeKey + ")");
        
        // TODO Auto-generated method stub
		// throw new UnsupportedOperationException("Unimplemented method 'handleRequestBody'");
        rsp.add("response", "ok");
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
        return "Some description";
	}

	// @Override
	// public Name getPermissionName(AuthorizationContext request) {
	// 	// TODO Auto-generated method stub
	// 	throw new UnsupportedOperationException("Unimplemented method 'getPermissionName'");
	// }
    
}
