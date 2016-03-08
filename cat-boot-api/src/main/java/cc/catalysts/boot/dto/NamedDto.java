package cc.catalysts.boot.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Basic DTO class with just an id and name.
 *
 * @author Klaus Lehner (klaus.lehner@catalysts.cc)
 */
public class NamedDto<ID extends Serializable> implements Identifiable<ID> {
    private ID id;
    private String name;

    @JsonCreator
    public NamedDto(@JsonProperty("id") ID id, @JsonProperty("name") String name) {
        setId(id);

        this.name = name;
    }

    /**
     * Necessary for JAXB unmarshalling.
     */
    NamedDto() {
    }

    @Override
    public final ID getId() {
        return id;
    }

    @Override
    public final void setId(ID id) {
        this.id = id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "\"" + this.getName() + "\" id=\"" + this.getId() + "\"";
    }
}
