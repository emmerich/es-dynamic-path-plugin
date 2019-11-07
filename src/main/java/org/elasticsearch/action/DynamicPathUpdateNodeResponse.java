package org.elasticsearch.action;

import java.io.IOException;
import org.elasticsearch.action.support.nodes.BaseNodeResponse;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.rest.RestStatus;

public class DynamicPathUpdateNodeResponse extends BaseNodeResponse {

  private RestStatus status;

  public DynamicPathUpdateNodeResponse() {
  }

  public DynamicPathUpdateNodeResponse(final DiscoveryNode node, RestStatus status) {
    super(node);
    this.status = status;
  }

  public static DynamicPathUpdateNodeResponse readNodeResponse(StreamInput in) throws IOException {
    DynamicPathUpdateNodeResponse nodeResponse = new DynamicPathUpdateNodeResponse();
    nodeResponse.readFrom(in);
    return nodeResponse;
  }

  public RestStatus getStatus() {
    return status;
  }

  @Override
  public void writeTo(StreamOutput out) throws IOException {
    super.writeTo(out);
    out.writeInt(status.getStatus());
  }

  @Override
  public void readFrom(StreamInput in) throws IOException {
    super.readFrom(in);
    status = RestStatus.fromCode(in.readInt());
  }

  @Override
  public String toString() {
    return "DynamicPathUpdateNodeResponse [status=" + status + "]";
  }
}
