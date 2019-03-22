package es.upm.disnet.pubmed.model;
import es.upm.disnet.pubmed.constants.Constants;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

/**
 * Created by gerardo on 08/03/2018.
 *
 * @author Gerardo Lagunes G. ${EMAIL}
 * @version ${<VERSION>}
 * @project pubmed_text_extraction_rest
 * @className Request
 * @see
 */
public class Request {

    @Valid
    @NotNull(message = Constants.ERR_NO_PARAMETER)
    @Size(min = 10, max = 10, message = Constants.ERR_EMPTY_PARAMETER)
    private String snapshot;
    private boolean json;
    private Integer numOfArticles;

    public Request() {
    }

    public Request(String snapshot, boolean json) {
        this.snapshot = snapshot;
        this.json = json;
    }

    public Request(String snapshot, boolean json, Integer numOfArticles) {
        this.snapshot = snapshot;
        this.json = json;
        this.numOfArticles = numOfArticles;
    }

    public String getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(String snapshot) {
        this.snapshot = snapshot;
    }

    public boolean isJson() {
        return json;
    }

    public void setJson(boolean json) {
        this.json = json;
    }

    public Integer getNumOfArticles() {
        return numOfArticles;
    }

    public void setNumOfArticles(Integer numOfArticles) {
        this.numOfArticles = numOfArticles;
    }
}
