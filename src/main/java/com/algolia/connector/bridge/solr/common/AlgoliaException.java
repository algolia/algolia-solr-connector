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
package com.algolia.connector.bridge.solr.common;

/**
 * @author Rakesh Kumar
 */
public class AlgoliaException extends RuntimeException {

    private static final long serialVersionUID = -3377434176129076540L;

    public AlgoliaException(final Throwable cause) {
        super(cause);
    }
}
