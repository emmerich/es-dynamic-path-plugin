package org.elasticsearch.action;

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

public class DynamicPathUpdateResponse extends BaseNodesResponse<DynamicPathUpdateNodeResponse> implements
    StatusToXContentObject {

  public DynamicPathUpdateResponse() {}

  public DynamicPathUpdateResponse(final ClusterName clusterName, List<DynamicPathUpdateNodeResponse> nodes, List<FailedNodeException> failures) {
    super(clusterName, nodes, failures);
  }

  @Override
  public List<DynamicPathUpdateNodeResponse> readNodesFrom(final StreamInput in) throws IOException {
    return in.readList(DynamicPathUpdateNodeResponse::readNodeResponse);
  }

  @Override
  public void writeNodesTo(final StreamOutput out, List<DynamicPathUpdateNodeResponse> nodes) throws IOException {
    out.writeStreamableList(nodes);
  }

  @Override
  public XContentBuilder toXContent(XContentBuilder builder, Params params) {
    return builder;
  }

  @Override
  public String toString() {
    return Strings.toString(this, true, true);
  }

  @Override
  public RestStatus status() {
    RestStatus result = RestStatus.OK;

    for (DynamicPathUpdateNodeResponse nodeResponse : getNodes()) {
      if (nodeResponse.getStatus() != RestStatus.OK) {
        result = nodeResponse.getStatus();
        break;
      }
    }

    return result;
  }
}

