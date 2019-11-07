package org.elasticsearch.action;

import org.elasticsearch.action.support.nodes.NodesOperationRequestBuilder;
import org.elasticsearch.client.ElasticsearchClient;

public class DynamicPathUpdateRequestBuilder extends
    NodesOperationRequestBuilder<DynamicPathUpdateRequest, DynamicPathUpdateResponse, DynamicPathUpdateRequestBuilder> {

  DynamicPathUpdateRequestBuilder(final ElasticsearchClient client, final DynamicPathUpdateAction action) {
    super(client, action, new DynamicPathUpdateRequest());
  }
}
