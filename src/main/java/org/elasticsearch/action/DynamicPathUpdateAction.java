package org.elasticsearch.action;

import org.elasticsearch.client.ElasticsearchClient;

public class DynamicPathUpdateAction extends Action<DynamicPathUpdateRequest, DynamicPathUpdateResponse, DynamicPathUpdateRequestBuilder> {

  public static final DynamicPathUpdateAction INSTANCE = new DynamicPathUpdateAction();
  public static final String NAME = "cluster:admin/dpp/update";

  private DynamicPathUpdateAction() {
    super(NAME);
  }

  @Override
  public DynamicPathUpdateRequestBuilder newRequestBuilder(ElasticsearchClient client) {
    return new DynamicPathUpdateRequestBuilder(client, this);
  }

  @Override
  public DynamicPathUpdateResponse newResponse() {
    return new DynamicPathUpdateResponse();
  }
}
