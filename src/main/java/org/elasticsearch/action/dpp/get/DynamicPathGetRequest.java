package org.elasticsearch.action.dpp.get;

import org.elasticsearch.action.support.nodes.BaseNodesRequest;

public class DynamicPathGetRequest extends BaseNodesRequest<DynamicPathGetRequest> {

  private String path;

  public DynamicPathGetRequest() {

  }

  public DynamicPathGetRequest(String path) {
    this.path = path;
  }

  public String getPath() {
    return path;
  }
}
