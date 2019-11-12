package org.elasticsearch.plugin.dpp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.dpp.get.DynamicPathGetAction;
import org.elasticsearch.action.dpp.get.DynamicPathGetTransportAction;
import org.elasticsearch.action.dpp.update.DynamicPathUpdateAction;
import org.elasticsearch.action.dpp.update.DynamicPathUpdateTransportAction;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.metadata.IndexNameExpressionResolver;
import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.elasticsearch.cluster.service.ClusterService;
import org.elasticsearch.common.io.stream.NamedWriteableRegistry;
import org.elasticsearch.common.settings.ClusterSettings;
import org.elasticsearch.common.settings.IndexScopedSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.SettingsFilter;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.env.Environment;
import org.elasticsearch.env.NodeEnvironment;
import org.elasticsearch.plugins.ActionPlugin;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.DiscoveryPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.rest.DynamicPathGetRestHandler;
import org.elasticsearch.rest.DynamicPathUpdateRestHandler;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestHandler;
import org.elasticsearch.script.ScriptService;
import org.elasticsearch.service.DynamicPathService;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.watcher.ResourceWatcherService;

public class DynamicPathPlugin extends Plugin implements AnalysisPlugin, ActionPlugin, DiscoveryPlugin {

  private final static Logger LOGGER = LogManager.getLogger(DynamicPathPlugin.class);

  public DynamicPathPlugin() {
    try {
      final Properties pluginProperties = new Properties();
      pluginProperties.load(this.getClass().getClassLoader().getResourceAsStream("plugin-descriptor.properties"));

      String version = pluginProperties.getProperty("version");

      LOGGER.info("dynamic-path-plugin version: {}", version);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public Collection<Object> createComponents(Client client, ClusterService clusterService,
      ThreadPool threadPool, ResourceWatcherService resourceWatcherService,
      ScriptService scriptService, NamedXContentRegistry xContentRegistry, Environment environment,
      NodeEnvironment nodeEnvironment, NamedWriteableRegistry namedWriteableRegistry) {
    final List<Object> components = new ArrayList<>();

    components.add(new DynamicPathService(environment));

    return components;
  }

  @Override
  public List<ActionHandler<? extends ActionRequest, ? extends ActionResponse>> getActions() {
    List<ActionHandler<? extends ActionRequest, ? extends ActionResponse>> actions = new ArrayList<>();

    actions.add(new ActionHandler<>(DynamicPathUpdateAction.INSTANCE, DynamicPathUpdateTransportAction.class));
    actions.add(new ActionHandler<>(DynamicPathGetAction.INSTANCE, DynamicPathGetTransportAction.class));

    return actions;
  }

  @Override
  public List<RestHandler> getRestHandlers(Settings settings, RestController restController,
      ClusterSettings clusterSettings, IndexScopedSettings indexScopedSettings,
      SettingsFilter settingsFilter, IndexNameExpressionResolver indexNameExpressionResolver,
      Supplier<DiscoveryNodes> nodesInCluster) {
    List<RestHandler> handlers = new ArrayList<>();

    handlers.add(new DynamicPathUpdateRestHandler(settings, restController));
    handlers.add(new DynamicPathGetRestHandler(settings, restController));

    return handlers;
  }
}

