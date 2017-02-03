package cc.catalysts.boot.structurizr.service;

import cc.catalysts.boot.structurizr.ViewProvider;
import cc.catalysts.boot.structurizr.config.StructurizrConfigurationProperties;
import com.structurizr.Workspace;
import com.structurizr.api.StructurizrClient;
import com.structurizr.api.StructurizrClientException;
import com.structurizr.model.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author Klaus Lehner, Catalysts GmbH
 */
@Service
@Order(Ordered.HIGHEST_PRECEDENCE)
public class StructurizrService implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(StructurizrService.class);

    private final StructurizrClient structurizrClient;
    private final Workspace workspace;
    private final StructurizrConfigurationProperties config;

    @Autowired
    public StructurizrService(StructurizrClient structurizrClient, Workspace workspace, StructurizrConfigurationProperties config) {
        this.structurizrClient = structurizrClient;
        this.workspace = workspace;
        this.config = config;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (config.isAddImplicitRelationships()) {
            final Set<Relationship> relationships = workspace.getModel().addImplicitRelationships();
            LOG.info("Added {} implicit relationships.", relationships.size());
        }
        event.getApplicationContext()
                .getBeansOfType(ViewProvider.class)
                .values()
                .forEach(vp -> vp.createViews(workspace.getViews()));


        if (config.isPerformMerge()) {
            try {
                structurizrClient.putWorkspace(config.getWorkspaceId(), workspace);
            } catch (StructurizrClientException e) {
                LOG.error("Could not put workspace.", e);
                throw new RuntimeException(e);
            }
        }
    }
}
