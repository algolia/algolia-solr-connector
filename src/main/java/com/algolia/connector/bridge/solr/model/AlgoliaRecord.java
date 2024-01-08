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

import java.util.HashMap;

import static com.algolia.connector.bridge.solr.common.Constants.ATTRIBUTE_OBJECT_ID;

/**
 * A record in Algolia index.
 *
 * @author Rakesh Kumar
 */
@SuppressWarnings("java:S2160")
public class AlgoliaRecord extends HashMap<String, Object> {

    private static final long serialVersionUID = -8318911812141566801L;

    /**
     * Note: This field is provided as a workaround for mandatory requirement by Algolia search client.
     */
    private final String objectID;

    public AlgoliaRecord(final String objectID) {
        this.objectID = objectID;
        this.addAttribute(ATTRIBUTE_OBJECT_ID, objectID);
    }

    public String getObjectID() {
        return this.objectID;
    }

    public void addAttribute(final String key, final Object value) {
        this.put(key, value);
    }
}
