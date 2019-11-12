package org.elasticsearch.action.dpp.get;

import java.io.IOException;
import java.util.List;
import org.elasticsearch.action.FailedNodeException;
import org.elasticsearch.action.support.nodes.BaseNodesResponse;
import org.elasticsearch.cluster.ClusterName;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.xcontent.StatusToXContentObject;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.rest.RestStatus;

public class DynamicPathGetResponse extends BaseNodesResponse<DynamicPathGetNodeResponse> implements
    StatusToXContentObject {

  private List<String> lines;

  public DynamicPathGetResponse() {}

  public DynamicPathGetResponse(final ClusterName clusterName, List<DynamicPathGetNodeResponse> nodes, List<FailedNodeException> failures, List<String> lines) {
    super(clusterName, nodes, failures);

    this.lines = lines;
  }

  @Override
  public List<DynamicPathGetNodeResponse> readNodesFrom(final StreamInput in) throws IOException {
    return in.readList(DynamicPathGetNodeResponse::readNodeResponse);
  }

  @Override
  public void writeNodesTo(final StreamOutput out, List<DynamicPathGetNodeResponse> nodes) throws IOException {
    out.writeStreamableList(nodes);
  }

  @Override
  public XContentBuilder toXContent(XContentBuilder builder, Params params) {
    try {
      builder.array("lines", lines.toArray());
    } catch (IOException e) {
      e.printStackTrace();
    }

    return builder;
  }

  @Override
  public String toString() {
    return Strings.toString(this, true, true);
  }

  @Override
  public RestStatus status() {
    RestStatus result = RestStatus.OK;

    for (DynamicPathGetNodeResponse nodeResponse : getNodes()) {
      if (nodeResponse.getStatus() != RestStatus.OK) {
        result = nodeResponse.getStatus();
        break;
      }
    }

    return result;
  }
}

