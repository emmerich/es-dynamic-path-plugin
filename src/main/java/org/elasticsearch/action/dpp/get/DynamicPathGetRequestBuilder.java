package org.elasticsearch.action.dpp.get;

import org.elasticsearch.action.support.nodes.NodesOperationRequestBuilder;
import org.elasticsearch.client.ElasticsearchClient;

public class DynamicPathGetRequestBuilder extends
    NodesOperationRequestBuilder<DynamicPathGetRequest, DynamicPathGetResponse, DynamicPathGetRequestBuilder> {

  DynamicPathGetRequestBuilder(final ElasticsearchClient client, final DynamicPathGetAction action) {
    super(client, action, new DynamicPathGetRequest());
  }
}
