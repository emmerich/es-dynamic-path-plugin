package org.elasticsearch.action;

import org.elasticsearch.action.support.nodes.BaseNodesRequest;

public class DynamicPathUpdateRequest extends BaseNodesRequest<DynamicPathUpdateRequest> {

  private String path;
  private String line;
  private Mode mode;

  public DynamicPathUpdateRequest() {

  }

  public DynamicPathUpdateRequest(String path, String line, Mode mode) {
    this.path = path;
    this.line = line;
    this.mode = mode;
  }

  public String getPath() {
    return path;
  }

  public String getLine() {
    return line;
  }

  public Mode getMode() {
    return mode;
  }

  public enum Mode {
    APPEND, REMOVE, NOOP
  }
}
