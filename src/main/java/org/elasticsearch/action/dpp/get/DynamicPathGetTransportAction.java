package org.elasticsearch.action.dpp.get;

import java.io.IOException;
import java.nio.file.FileSystemException;
import java.util.ArrayList;
import java.util.List;
import org.elasticsearch.action.FailedNodeException;
import org.elasticsearch.action.support.ActionFilters;
import org.elasticsearch.action.support.nodes.BaseNodeRequest;
import org.elasticsearch.action.support.nodes.TransportNodesAction;
import org.elasticsearch.cluster.metadata.IndexNameExpressionResolver;
import org.elasticsearch.cluster.service.ClusterService;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.service.DynamicPathService;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.threadpool.ThreadPool.Names;
import org.elasticsearch.transport.TransportService;

public class DynamicPathGetTransportAction extends
    TransportNodesAction<DynamicPathGetRequest, DynamicPathGetResponse, DynamicPathGetTransportAction.NodeRequest, DynamicPathGetNodeResponse> {

  private DynamicPathService dynamicPathService;

  @Inject
  public DynamicPathGetTransportAction(final Settings settings,
      final ThreadPool threadPool,
      final ClusterService clusterService,
      final TransportService transportService,
      final ActionFilters actionFilters,
      final IndexNameExpressionResolver indexNameExpressionResolver,
      final DynamicPathService dynamicPathService) {
    super(settings, DynamicPathGetAction.NAME, threadPool, clusterService, transportService,
        actionFilters, indexNameExpressionResolver, DynamicPathGetRequest::new,
        DynamicPathGetTransportAction.NodeRequest::new,
        Names.MANAGEMENT,
        DynamicPathGetNodeResponse.class);

    this.dynamicPathService = dynamicPathService;
  }

  @Override
  protected DynamicPathGetResponse newResponse(DynamicPathGetRequest request,
      List<DynamicPathGetNodeResponse> responses,
      List<FailedNodeException> failures) {

    if (responses.size() > 0) {
      DynamicPathGetNodeResponse firstResponse = responses.get(0);
      return new DynamicPathGetResponse(clusterService.getClusterName(), responses, failures, firstResponse.getLines());
    }

    return new DynamicPathGetResponse(clusterService.getClusterName(), responses, failures, new ArrayList<>());
  }

  @Override
  protected NodeRequest newNodeRequest(String nodeId, DynamicPathGetRequest request) {
    return new NodeRequest(nodeId, request);
  }

  @Override
  protected DynamicPathGetNodeResponse newNodeResponse() {
    return new DynamicPathGetNodeResponse(clusterService.localNode(), null, new ArrayList<>());
  }

  @Override
  protected DynamicPathGetNodeResponse nodeOperation(NodeRequest nodeRequest) {
    DynamicPathGetRequest request = nodeRequest.request;

    try {
      List<String> lines = dynamicPathService.getLines(request.getPath());
      return new DynamicPathGetNodeResponse(clusterService.localNode(), RestStatus.OK, lines);
    } catch (FileSystemException e) {
      e.printStackTrace();
      return new DynamicPathGetNodeResponse(clusterService.localNode(), RestStatus.BAD_REQUEST, new ArrayList<>());
    }
  }

  public static class NodeRequest extends BaseNodeRequest {

    DynamicPathGetRequest request;

    public NodeRequest() {
    }

    public NodeRequest(final String nodeId, final DynamicPathGetRequest request) {
      super(nodeId);
      this.request = request;
    }

    @Override
    public void readFrom(final StreamInput in) throws IOException {
      super.readFrom(in);
      request = new DynamicPathGetRequest();
      request.readFrom(in);
    }

    @Override
    public void writeTo(final StreamOutput out) throws IOException {
      super.writeTo(out);
      request.writeTo(out);
    }
  }

}
