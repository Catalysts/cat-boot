package cc.catalysts.boot.structurizr.utils;

import com.structurizr.model.Element;
import com.structurizr.view.ContainerView;

/**
 * @author Klaus Lehner, Catalysts GmbH
 */
public class StructurizrUtils {

    /**
     * <p>Adds all {@link com.structurizr.model.Container}s of the given {@link ContainerView} as well as all external influencers, that is all
     * persons and all other software systems with incoming or outgoing dependencies.</p>
     * <p>Additionally, all relationships of external dependencies are omitted to keep the diagram clean</p>
     *
     * @param containerView
     */
    public static void addAllContainersAndInfluencers(ContainerView containerView) {

        // first add all containers of the underlying software system
        containerView.addAllContainers();

        // then add all software systems with incoming or outgoing dependencies
        containerView.getModel().getSoftwareSystems()
                .stream()
                .filter(softwareSystem -> softwareSystem.hasEfferentRelationshipWith(containerView.getSoftwareSystem()) || containerView.getSoftwareSystem().hasEfferentRelationshipWith(softwareSystem))
                .forEach(containerView::add);

        // then add all people with incoming or outgoing dependencies
        containerView.getModel().getPeople()
                .stream()
                .filter(person -> person.hasEfferentRelationshipWith(containerView.getSoftwareSystem()) || containerView.getSoftwareSystem().hasEfferentRelationshipWith(person))
                .forEach(containerView::add);

        // then remove all relationships of external elements to keep the container view clean
        containerView.getRelationships()
                .stream()
                .map(view -> view.getRelationship())
                .filter(relationship -> !isPartOf(relationship.getDestination(), containerView.getSoftwareSystem()) && !isPartOf(relationship.getSource(), containerView.getSoftwareSystem()))
                .forEach(containerView::remove);
    }

    private static boolean isPartOf(Element element, Element other) {
        if (element.getId().equals(other.getId())) {
            return true;
        } else if (element.getParent() != null) {
            return isPartOf(element.getParent(), other);
        }
        return false;
    }
}
