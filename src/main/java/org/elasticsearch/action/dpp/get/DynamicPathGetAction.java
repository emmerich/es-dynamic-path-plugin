package org.elasticsearch.action.dpp.get;

import org.elasticsearch.action.Action;
import org.elasticsearch.client.ElasticsearchClient;

public class DynamicPathGetAction extends
    Action<DynamicPathGetRequest, DynamicPathGetResponse, DynamicPathGetRequestBuilder> {

  public static final DynamicPathGetAction INSTANCE = new DynamicPathGetAction();
  public static final String NAME = "cluster:admin/dpp/get";

  private DynamicPathGetAction() {
    super(NAME);
  }

  @Override
  public DynamicPathGetRequestBuilder newRequestBuilder(ElasticsearchClient client) {
    return new DynamicPathGetRequestBuilder(client, this);
  }

  @Override
  public DynamicPathGetResponse newResponse() {
    return new DynamicPathGetResponse();
  }
}
