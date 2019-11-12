package org.elasticsearch.action.dpp.get;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.elasticsearch.action.support.nodes.BaseNodeResponse;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.rest.RestStatus;

public class DynamicPathGetNodeResponse extends BaseNodeResponse {

  private RestStatus status;
  private List<String> lines;

  public DynamicPathGetNodeResponse() {
  }

  public DynamicPathGetNodeResponse(final DiscoveryNode node, RestStatus status, List<String> lines) {
    super(node);
    this.status = status;
    this.lines = lines;
  }

  public static DynamicPathGetNodeResponse readNodeResponse(StreamInput in) throws IOException {
    DynamicPathGetNodeResponse nodeResponse = new DynamicPathGetNodeResponse();
    nodeResponse.readFrom(in);
    return nodeResponse;
  }

  public RestStatus getStatus() {
    return status;
  }

  public List<String> getLines() {
    return lines;
  }

  @Override
  public void writeTo(StreamOutput out) throws IOException {
    super.writeTo(out);
    out.writeInt(status.getStatus());
    out.writeStringArray((String[]) lines.toArray());
  }

  @Override
  public void readFrom(StreamInput in) throws IOException {
    super.readFrom(in);
    status = RestStatus.fromCode(in.readInt());
    lines = Arrays.asList(in.readStringArray());
  }

  @Override
  public String toString() {
    return "DynamicPathGetNodeResponse [status=" + status + "] [line count=" + lines.size() + "]";
  }
}
