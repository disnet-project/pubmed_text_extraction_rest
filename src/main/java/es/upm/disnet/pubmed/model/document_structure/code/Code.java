package es.upm.disnet.pubmed.model.document_structure.code;


import com.fasterxml.jackson.annotation.JsonInclude;
import es.upm.disnet.pubmed.model.document_structure.Link;

import java.util.Objects;

/**
 * Created by gerardo on 01/06/2017.
 *
 * @author Gerardo Lagunes G. ${EMAIL}
 * @version ${<VERSION>}
 * @project ExtractionInformationDiseasesWikipedia
 * @className Code
 * @see
 */
public class Code {

    private Integer id;
    private Integer uniqueId;
    private String code;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Link link;
    private Resource resource;



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(Integer uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Code)) return false;
        Code code1 = (Code) o;
        return Objects.equals(getCode(), code1.getCode()) &&
                Objects.equals(getResource(), code1.getResource());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCode(), getResource());
    }

    @Override
    public String toString() {
        return "Code{" +
                "id=" + id +
                ", uniqueId=" + uniqueId +
                ", code='" + code + '\'' +
                ", link=" + link +
                ", resource=" + resource +
                '}';
    }
}
