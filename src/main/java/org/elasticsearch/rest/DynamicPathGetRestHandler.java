package org.elasticsearch.rest;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import org.elasticsearch.action.dpp.get.DynamicPathGetAction;
import org.elasticsearch.action.dpp.get.DynamicPathGetRequest;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.rest.RestRequest.Method;
import org.elasticsearch.rest.action.RestActions.NodesResponseRestListener;

public class DynamicPathGetRestHandler extends BaseRestHandler {

  public DynamicPathGetRestHandler(final Settings settings, final RestController controller) {
    super(settings);

    controller.registerHandler(Method.POST, "/_dpp/get", this);
  }

  @Override
  public boolean supportsContentStream() {
    return true;
  }

  @Override
  public String getName() {
    return "Dynamic Path Get Action";
  }

  @Override
  protected RestChannelConsumer prepareRequest(RestRequest request, NodeClient client) throws IOException {
    String path = null;

    if (request.hasContent()) {
      try (XContentParser parser = request.contentParser()) {
        XContentParser.Token token = parser.nextToken();
        if (token == null) {
          throw new IllegalArgumentException("/_dpp/get must be called with a path.");
        }
        String currentFieldName = null;
        while ((token = parser.nextToken()) != null) {
          if (token == XContentParser.Token.FIELD_NAME) {
            currentFieldName = parser.currentName();
          } else if (token.isValue()) {
            if ("path".equals(currentFieldName)) {
              path = URLDecoder.decode(parser.text(), StandardCharsets.UTF_8.name());
            }
          }
        }
      }
    }

    DynamicPathGetRequest actionRequest = new DynamicPathGetRequest(path);
    return channel -> client.executeLocally(DynamicPathGetAction.INSTANCE, actionRequest, new NodesResponseRestListener(channel));
  }
}
