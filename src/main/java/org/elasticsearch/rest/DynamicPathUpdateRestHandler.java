package org.elasticsearch.rest;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import org.elasticsearch.action.dpp.update.DynamicPathUpdateAction;
import org.elasticsearch.action.dpp.update.DynamicPathUpdateRequest;
import org.elasticsearch.action.dpp.update.DynamicPathUpdateRequest.Mode;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentParser;
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
    String path = null;
    String line = null;
    Mode mode = null;

    if (request.hasContent()) {
      try (XContentParser parser = request.contentParser()) {
        XContentParser.Token token = parser.nextToken();
        if (token == null) {
          throw new IllegalArgumentException("/_dpp/update must be called with a path, line and mode.");
        }
        String currentFieldName = null;
        while ((token = parser.nextToken()) != null) {
          if (token == XContentParser.Token.FIELD_NAME) {
            currentFieldName = parser.currentName();
          } else if (token.isValue()) {
            if ("path".equals(currentFieldName)) {
              path = URLDecoder.decode(parser.text(), StandardCharsets.UTF_8.name());
            } else if ("line".equals(currentFieldName)) {
              line = parser.text();
            } else if ("mode".equals(currentFieldName)) {
              mode = getMode(parser.text());
            }
          }
        }
      }
    }

    DynamicPathUpdateRequest actionRequest = new DynamicPathUpdateRequest(path, line, mode);
    return channel -> client.executeLocally(DynamicPathUpdateAction.INSTANCE, actionRequest, new NodesResponseRestListener(channel));
  }

  private Mode getMode(String key) {
    switch (key) {
      case "append":
        return Mode.APPEND;
      case "remove":
        return Mode.REMOVE;
      case "noop":
        return Mode.NOOP;
      case "create":
        return Mode.CREATE;
      default:
          return null;
    }
  }
}
