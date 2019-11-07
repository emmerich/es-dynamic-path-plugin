package org.elasticsearch.rest;

import java.io.IOException;
import org.elasticsearch.action.DynamicPathUpdateAction;
import org.elasticsearch.action.DynamicPathUpdateRequest;
import org.elasticsearch.action.DynamicPathUpdateRequest.Mode;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.RestRequest.Method;
import org.elasticsearch.rest.action.RestActions.NodesResponseRestListener;

public class DynamicPathUpdateRestHandler extends BaseRestHandler {

  public DynamicPathUpdateRestHandler(final Settings settings, final RestController controller) {
    super(settings);

    controller.registerHandler(Method.POST, "/_dpp/update", this);
  }

  @Override
  public boolean supportsContentStream() {
    return true;
  }

  @Override
  public String getName() {
    return "Dynamic Path Update Action";
  }

  @Override
  protected RestChannelConsumer prepareRequest(RestRequest request, NodeClient client) throws IOException {
    String path = request.param("path");
    String line = request.param("line");
    String modeKey = request.param("mode");
    Mode mode = getMode(modeKey);

    DynamicPathUpdateRequest actionRequest = new DynamicPathUpdateRequest(path, line, mode);
    return channel -> client.executeLocally(DynamicPathUpdateAction.INSTANCE, actionRequest, new NodesResponseRestListener(channel));
  }

  private Mode getMode(String key) {
    switch (key) {
      case "append":
        return Mode.APPEND;
      case "remove":
        return Mode.REMOVE;
      default:
        return Mode.NOOP;
    }
  }
}
