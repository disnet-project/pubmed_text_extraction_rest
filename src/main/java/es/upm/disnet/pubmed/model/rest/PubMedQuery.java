/*
 * Copyright (c) 2018. Eduardo P. Garcia del Valle.
 *
 * If other author is present on the file, the copyrights are shared or of the mentioned author.
 *
 * Unless required by applicable law or agreed to in writing, software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package es.upm.disnet.pubmed.model.rest;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author https://github.com/jl987-Jie
 * <p>
 * See https://github.com/wcmc-its/ReCiter-PubMed-Retrieval-Tool
 */
public class PubMedQuery {

    private static SimpleDateFormat dt = new SimpleDateFormat("yyyy/MM/dd");
    @JsonProperty("author")
    private String author;
    @JsonProperty("start")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date start;
    @JsonProperty("end")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date end;
    @JsonProperty("strategy-rest")
    private String strategyQuery;
    @JsonProperty("doi")
    private String doi;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getStrategyQuery() {
        return strategyQuery;
    }

    public void setStrategyQuery(String strategyQuery) {
        this.strategyQuery = strategyQuery;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    @Override
    public String toString() {
//        return author + "[au]" + " AND " + dt.format(start) + ":" + dt.format(end) + "[DP]";
        List<String> parts = new ArrayList<String>();
        if (author != null) {
            parts.add(author + " [au]");
        }
        if (start != null && end != null) {
            parts.add(dt.format(start) + ":" + dt.format(end) + "[DP]");
        }
        if (strategyQuery != null && !strategyQuery.isEmpty()) {
            parts.add(strategyQuery);
        }
        if (doi != null) {
            parts.add(doi);
        }

        return StringUtils.join(parts, " AND ");
    }
}
