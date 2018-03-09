package es.upm.disnet.pubmed.model.document_structure;

import java.util.List;
import java.util.TreeMap;

/**
 * Created by gerardo on 03/05/2017.
 *
 * @author Gerardo Lagunes G. ${EMAIL}
 * @version ${<VERSION>}
 * @project ExtractionInformationDiseasesWikipedia
 * @className LoadSource
 * @see
 */
public class Source {

    private int id;
    private String name;
    private Link url;

    private List<Doc> docList;
    private TreeMap<String, String> sectionMap;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Link getUrl() {
        return url;
    }

    public void setUrl(Link url) {
        this.url = url;
    }

    public List<Doc> getDocList() {
        return docList;
    }

    public void setDocList(List<Doc> docList) {
        this.docList = docList;
    }

    public TreeMap<String, String> getSectionMap() {
        return sectionMap;
    }

    public void setSectionMap(TreeMap<String, String> sectionMap) {
        this.sectionMap = sectionMap;
    }

}
