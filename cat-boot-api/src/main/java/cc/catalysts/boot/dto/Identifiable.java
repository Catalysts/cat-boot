package cc.catalysts.boot.dto;

import java.io.Serializable;

/**
 * Common interface for an identifiable object
 *
 * @param <ID> the type of the identifier, e.g. {@link Long}
 * @author Klaus Lehner (klaus.lehner@extern.brz.gv.at)
 */
public interface Identifiable<ID extends Serializable> {
    /**
     * @return the unique ID of this object
     */
    ID getId();

    /**
     * @param id the unique ID of this object
     */
    void setId(ID id);
}
