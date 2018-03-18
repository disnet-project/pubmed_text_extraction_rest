/*
 * Copyright (c) 2018. Eduardo P. Garcia del Valle.
 *
 * If other author is present on the file, the copyrights are shared or of the mentioned author.
 *
 * Unless required by applicable law or agreed to in writing, software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package es.upm.disnet.pubmed.model.document_structure;

/**
 * @author Eduardo P. Garcia del Valle
 */

import es.upm.disnet.pubmed.constants.Constants;
import es.upm.disnet.pubmed.enums.SourceEnum;
import es.upm.disnet.pubmed.model.document_structure.code.Resource;
import org.springframework.util.StringUtils;

import java.util.*;

public class PubMedDoc {

    private static final String FULL_TEXT_DELIMITER = ";;";
    private long id;
    private String author;
    private String pmID;
    private String pmcID;
    private String doi;
    private String titleText;
    private String meshTerms;
    private String keyWords;
    private String abstractText;
    private Disease disease;
    private String snapshot;
    private Integer termCount;
    private List<Term> terms;
    private boolean hasFreeText;
    private Link link;
    private Link freeTextlink;

    public String getAbstractText() {
        return abstractText;
    }

    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    public String getPmcID() {
        return pmcID;
    }

    public void setPmcID(String pmcID) {
        this.pmcID = pmcID;
    }

    public Disease getDisease() {
        return disease;
    }

    public void setDisease(Disease disease) {
        this.disease = disease;
    }

    public long getDiseaseId() {
        return disease.getId();
    }

    public String getPmArticleURL() {
        return pmID != null ? "https://www.ncbi.nlm.nih.gov/pubmed/" + pmID : pmID;
    }

    public String getPmcArticleURL() {
        return pmcID != null ? "https://www.ncbi.nlm.nih.gov/pmc/articles/" + pmcID : pmcID;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(String snapshot) {
        this.snapshot = snapshot;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setAuthor(List<String> author) {
        this.author = asString(author, true);
    }

    public Collection<String> getAuthorAsCollection() {
        return asCollection(author, true);
    }

    public String getPmID() {
        return pmID;
    }

    public void setPmID(String pmID) {
        this.pmID = pmID;
    }

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public String getMeshTerms() {
        return meshTerms;
    }

    public void setMeshTerms(List<String> meshTerms) {
        this.meshTerms = asString(meshTerms);
        if (meshTerms!=null) {
            List<Term> terms = new ArrayList<>();
            for (String t : meshTerms) {
                Term term = new Term(t, new Resource(SourceEnum.MeSH.getClave(), SourceEnum.MeSH.getDescripcion()));
                terms.add(term);
            }
            this.terms = terms;
            this.termCount = this.terms.size();
        }
    }

    public void setMeshTerms(String meshTerms) {
        this.meshTerms = meshTerms;
    }

    public Collection<String> getMeshTermsAsCollection() {
        return asCollection(meshTerms);
    }

    public String getKeyWords() {
        return keyWords;
    }

    public void setKeyWords(Collection<String> keyWords) {
        this.keyWords = asString(keyWords);
    }

    public void setKeyWords(String keyWords) {
        this.keyWords = keyWords;
    }

    public Collection<String> getKeyWordsAsCollection() {
        return asCollection(keyWords);
    }

    public Integer getTermCount() {
        return termCount;
    }

    public void setTermCount(Integer termCount) {
        this.termCount = termCount;
    }

    public List<Term> getTerms() {
        return terms;
    }

    public void setTerms(List<Term> terms) {
        this.terms = terms;
    }

    public boolean isHasFreeText() {
        return hasFreeText;
    }

    public void setHasFreeText(boolean hasFreeText) {
        //pmcID != null ? true : false;
        this.hasFreeText = hasFreeText;
    }

    public void setHasFreeText() {
        this.hasFreeText = pmcID != null;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public void setLink() {
        Source source = new Source(Constants.SOURCE_WIKIPEDIA_CODE, Constants.SOURCE_WIKIPEDIA);
        if (pmID != null) this.link = new Link(this.getPmArticleURL(), source);
        else this.link = null;
    }

    public Link getFreeTextlink() {
        return freeTextlink;
    }

    public void setFreeTextlink(Link freeTextlink) {
        this.freeTextlink = freeTextlink;
    }

    public void setFreeTextlink() {
        Source source = new Source(Constants.SOURCE_WIKIPEDIA_CODE, Constants.SOURCE_WIKIPEDIA);
        if (pmcID != null) this.freeTextlink = new Link(this.getPmcArticleURL(), source);
        else this.freeTextlink = null;
    }

    private Collection<String> asCollection(String value) {
        return asCollection(value, false);
    }

    private String asString(Collection<String> value) {
        return asString(value, false);
    }

    private Collection<String> asCollection(String value, boolean isText) {
        return isText ?
                Arrays.asList(StringUtils.delimitedListToStringArray(value, FULL_TEXT_DELIMITER)) :
                StringUtils.commaDelimitedListToSet(value);
    }

    private String asString(Collection<String> value, boolean isText) {
        return isText ?
                StringUtils.collectionToDelimitedString(value, FULL_TEXT_DELIMITER) :
                StringUtils.collectionToCommaDelimitedString(value);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof PubMedDoc)) {
            return false;
        }

        PubMedDoc d = (PubMedDoc) o;

        return d.getPmID().equals(pmID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pmID);
    }

    @Override
    public String toString() {
        return "PubMedDoc{" +
                "id=" + id +
                ", link='" + getLink() + '\'' +
                ", url='" + getPmArticleURL() + '\'' +
                ", urlPMCenter='" + getPmcArticleURL() + '\'' +
                ", author='" + author + '\'' +
                ", pmID='" + pmID + '\'' +
                ", pmcID='" + pmcID + '\'' +
                ", doi='" + doi + '\'' +
                ", titleText='" + titleText + '\'' +
                ", meshTerms='" + meshTerms + '\'' +
                ", termCount='" + termCount + '\'' +
                ", terms='" + getTerms().toString() + '\'' +
                ", keyWords='" + keyWords + '\'' +
                ", abstractText='" + abstractText + '\'' +
                ", hasFreeText='" + hasFreeText + '\'' +
                //", disease=" + disease +
                ", snapshot='" + snapshot + '\'' +
                '}';
    }
}
