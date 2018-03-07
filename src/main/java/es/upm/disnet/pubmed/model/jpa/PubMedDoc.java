/*
 * Copyright (c) 2018. Eduardo P. Garcia del Valle.
 *
 * If other author is present on the file, the copyrights are shared or of the mentioned author.
 *
 * Unless required by applicable law or agreed to in writing, software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package es.upm.disnet.pubmed.model.jpa;

/**
 * @author Eduardo P. Garcia del Valle
 */

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;

@Entity
public class PubMedDoc {

    private static final String FULL_TEXT_DELIMITER = ";;";
    private Date createDate;
    @Id
    @Column(name = "pubMedDocId", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(length = 3000)
    private String author;
    @NotNull
    private String pmID;
    private String pmcID;
    private String doi;
    @Column(length = 3000)
    private String titleText;
    @Column(length = 3000)
    private String meshTerms;
    @Column(length = 3000)
    private String keyWords;
    @Lob
    private String abstractText;
    /*
    // Many to Many

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "Disease_PubMedDoc",
            joinColumns = {@JoinColumn(name = "pubMedDocId", referencedColumnName = "pubMedDocId")},
            inverseJoinColumns = {@JoinColumn(name = "diseaseId", referencedColumnName = "diseaseId")})
    private Set<Disease> diseases = new HashSet();

    @JsonIgnore
    public Set<Disease> getDiseases() {
        return diseases;
    }

    public void setDiseases(Set<Disease> diseases) {
        this.diseases = diseases;
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public long getDiseaseCount() {
        return diseases.size();
    }
    */
    @ManyToOne
    @JoinColumn(name = "disease_id")
    private Disease disease;
    @NotNull
    private String snapshot;

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

    @JsonIgnore
    public Disease getDisease() {
        return disease;
    }

    public void setDisease(Disease disease) {
        this.disease = disease;
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public long getDiseaseId() {
        return disease.getId();
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getPmArticleURL() {
        return pmID != null ? "https://www.ncbi.nlm.nih.gov/pubmed/" + pmID : pmID;
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
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

    public void setAuthor(Collection<String> author) {
        this.author = asString(author, true);
    }

    @JsonIgnore
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

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getMeshTerms() {
        return meshTerms;
    }

    public void setMeshTerms(Collection<String> meshTerms) {
        this.meshTerms = asString(meshTerms);
    }

    public void setMeshTerms(String meshTerms) {
        this.meshTerms = meshTerms;
    }

    @JsonIgnore
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

    @JsonIgnore
    public Collection<String> getKeyWordsAsCollection() {
        return asCollection(keyWords);
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
}
