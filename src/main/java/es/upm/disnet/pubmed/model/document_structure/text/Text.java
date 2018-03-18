package es.upm.disnet.pubmed.model.document_structure.text;


import es.upm.disnet.pubmed.model.document_structure.Link;
import es.upm.disnet.pubmed.model.document_structure.PubMedDoc;

import java.util.List;

/**
 * Created by gerardo on 3/4/17.
 *
 * @author Gerardo Lagunes G. ${EMAIL}
 * @version ${<VERSION>}
 * @project ExtractionInformationWikipedia
 * @className Text
 * @see
 */
//Un Text siempre será un <p>, <ul><ol>
public class Text {

    private Integer id;
    private String title;//EL nombre si tiene, será un <h3>
    private int textOrder;

    private Integer urlCount;
    private List<Link> urlList;
    private String paperId;

    public Text() {
    }

    public Text(int id, int textOrder) {
        this.id = id;
        this.textOrder = textOrder;
    }

    public Text(Integer id, int textOrder, String paperId) {
        this.id = id;
        this.textOrder = textOrder;
        this.paperId = paperId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTextOrder() {
        return textOrder;
    }

    public void setTextOrder(int textOrder) {
        this.textOrder = textOrder;
    }

    public Integer getUrlCount() {
        return urlCount;
    }

    public void setUrlCount(Integer urlCount) {
        this.urlCount = urlCount;
    }

    public List<Link> getUrlList() {
        return urlList;
    }

    public void setUrlList(List<Link> urlList) {
        this.urlList = urlList;
    }

    public String getPaperId() {
        return paperId;
    }

    public void setPaperId(String paperId) {
        this.paperId = paperId;
    }

    @Override
    public String toString() {
        return "Text{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", textOrder=" + textOrder +
                ", urlCount=" + urlCount +
                ", urlList=" + urlList +
                '}';
    }
}
