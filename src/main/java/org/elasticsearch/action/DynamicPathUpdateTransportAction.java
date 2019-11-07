package org.elasticsearch.action;

import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;
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

public class DynamicPathUpdateTransportAction extends
    TransportNodesAction<DynamicPathUpdateRequest, DynamicPathUpdateResponse, DynamicPathUpdateTransportAction.NodeRequest, DynamicPathUpdateNodeResponse> {

  private DynamicPathService dynamicPathService;

  @Inject
  public DynamicPathUpdateTransportAction(final Settings settings,
      final ThreadPool threadPool,
      final ClusterService clusterService,
      final TransportService transportService,
      final ActionFilters actionFilters,
      final IndexNameExpressionResolver indexNameExpressionResolver,
      final DynamicPathService dynamicPathService) {
    super(settings, DynamicPathUpdateAction.NAME, threadPool, clusterService, transportService,
        actionFilters, indexNameExpressionResolver, DynamicPathUpdateRequest::new,
        DynamicPathUpdateTransportAction.NodeRequest::new,
        Names.MANAGEMENT,
        DynamicPathUpdateNodeResponse.class);

    this.dynamicPathService = dynamicPathService;
  }

  @Override
  protected DynamicPathUpdateResponse newResponse(DynamicPathUpdateRequest request,
      List<DynamicPathUpdateNodeResponse> responses,
      List<FailedNodeException> failures) {
    return new DynamicPathUpdateResponse(clusterService.getClusterName(), responses, failures);
  }

  @Override
  protected NodeRequest newNodeRequest(String nodeId, DynamicPathUpdateRequest request) {
    return new NodeRequest(nodeId, request);
  }

  @Override
  protected DynamicPathUpdateNodeResponse newNodeResponse() {
    return new DynamicPathUpdateNodeResponse(clusterService.localNode(), null);
  }

  @Override
  protected DynamicPathUpdateNodeResponse nodeOperation(NodeRequest nodeRequest) {
    DynamicPathUpdateRequest request = nodeRequest.request;

    try {
      switch (request.getMode()) {
        case APPEND:
          dynamicPathService.appendLine(request.getPath(), request.getLine());
          break;
        case REMOVE:
          dynamicPathService.removeLine(request.getPath(), request.getLine());
          break;
        case NOOP:
          return new DynamicPathUpdateNodeResponse(clusterService.localNode(), RestStatus.NOT_MODIFIED);
      }

      return new DynamicPathUpdateNodeResponse(clusterService.localNode(), RestStatus.OK);
    } catch (IOException e) {
      e.printStackTrace();
      return new DynamicPathUpdateNodeResponse(clusterService.localNode(), RestStatus.BAD_REQUEST);
    }
  }

  public static class NodeRequest extends BaseNodeRequest {

    DynamicPathUpdateRequest request;

    public NodeRequest() {
    }

    public NodeRequest(final String nodeId, final DynamicPathUpdateRequest request) {
      super(nodeId);
      this.request = request;
    }

    @Override
    public void readFrom(final StreamInput in) throws IOException {
      super.readFrom(in);
      request = new DynamicPathUpdateRequest();
      request.readFrom(in);
    }

    @Override
    public void writeTo(final StreamOutput out) throws IOException {
      super.writeTo(out);
      request.writeTo(out);
    }
  }

}
