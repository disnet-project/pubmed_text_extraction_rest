/*
 * Copyright (c) 2018. Eduardo P. Garcia del Valle.
 *
 * If other author is present on the file, the copyrights are shared or of the mentioned author.
 *
 * Unless required by applicable law or agreed to in writing, software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package es.upm.disnet.pubmed.model.jpa;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Eduardo P. Garcia del Valle
 */
@Entity
public class Disease {

    private static final String FULL_TEXT_DELIMITER = ";;";
    @Id
    @Column(name = "diseaseId", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String meshMH;
    @Column(length = 1000)
    private String meSHMN;
    @Column(unique = true, nullable = false)
    private String meSHUI;
    @NotNull
    @Column(length = 500)
    private String doId;
    @NotNull
    @Column(length = 1000)
    private String doName;
    @Column(length = 1000)
    private String doIsA;
    @Column(length = 3000)
    private String doDef;
    private String icd10CM;
    private String omim;
    @Column(length = 3000)
    private String snomedct;
    @Column(length = 1000)
    private String umlsCUI;
    @NotNull
    private Date createDate;
    @NotNull
    private String snapshot;
    private boolean disnet;
    @OneToMany(mappedBy = "disease", cascade = CascadeType.ALL)
    private Set<PubMedDoc> pubMedDocs = new HashSet<>();
    @OneToMany(mappedBy = "disease", cascade = CascadeType.ALL)
    private Set<Term> terms = new HashSet<>();

    /*
    // Many to Many
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "diseases")
    private Set<PubMedDoc> pubMedDocs = new HashSet<>();
    */

    public boolean isDisnet() {
        return disnet;
    }

    public void setDisnet(boolean disnet) {
        this.disnet = disnet;
    }

    @JsonIgnore
    public Set<Term> getTerms() {
        return terms;
    }

    public void setTerms(Set<Term> terms) {
        this.terms = terms;
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public long getTermCount() {
        return terms.size();
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

    public String getMeshMH() {
        return meshMH;
    }

    public void setMeshMH(String meshMH) {
        this.meshMH = meshMH;
    }

    public String getDoId() {
        return doId;
    }

    public void setDoId(Collection<String> doId) {
        this.doId = asString(doId);
    }

    public void setDoId(String doId) {
        this.doId = doId;
    }

    @JsonIgnore
    public Collection<String> getDoIdAsCollection() {
        return asCollection(doId);
    }

    @JsonIgnore
    public Collection<String> getMeSHMNAsCollection() {
        return asCollection(meSHMN);
    }

    public void setMeSHMN(Collection<String> meSHMN) {
        this.meSHMN = asString(meSHMN);
    }

    public String getMeSHUI() {
        return meSHUI;
    }

    public void setMeSHUI(String meSHUI) {
        this.meSHUI = meSHUI;
    }

    public String getDoName() {
        return doName;
    }

    public void setDoName(String doName) {
        this.doName = doName;
    }

    public void setDoName(Collection<String> doName) {
        this.doName = asString(doName, true);
    }

    @JsonIgnore
    public Collection<String> getDoNameAsCollection() {
        return asCollection(doName, true);
    }

    public String getDoIsA() {
        return doIsA;
    }

    public void setDoIsA(String doIsA) {
        this.doIsA = doIsA;
    }

    public void setDoIsA(Collection<String> doIsA) {
        this.doIsA = asString(doIsA);
    }

    @JsonIgnore
    public Collection<String> getDoIsAAsCollection() {
        return asCollection(doIsA);
    }

    @JsonIgnore
    public Collection<String> getDoIsAIdAsCollection() {
        Collection<String> doIsANames = asCollection(doIsA, false);

        return doIsANames.stream().map(value -> value.substring(0, value.indexOf("!")).trim()).collect(Collectors.toList());
    }

    public String getDoDef() {
        return doDef;
    }

    public void setDoDef(String doDef) {
        this.doDef = doDef;
    }

    public void setDoDef(Collection<String> doDef) {
        this.doDef = asString(doDef, true);
    }

    @JsonIgnore
    public Collection<String> getDoDefAsCollection() {
        return asCollection(doDef, true);
    }

    @JsonIgnore
    public Collection<String> getIcd10CMAsCollection() {
        return asCollection(icd10CM);
    }

    public void setIcd10CM(Collection<String> icd10CM) {
        this.icd10CM = asString(icd10CM);
    }

    @JsonIgnore
    public Collection<String> getOmimAsCollection() {
        return asCollection(omim);
    }

    public void setOmim(Collection<String> omim) {
        this.omim = asString(omim);
    }

    @JsonIgnore
    public Collection<String> getSnomedctAsCollection() {
        return asCollection(snomedct);
    }

    public void setSnomedct(Collection<String> snomedct) {
        this.snomedct = asString(snomedct);
    }

    @JsonIgnore
    public Collection<String> getUmlsCUIAsCollection() {
        return asCollection(umlsCUI);
    }

    public void setUmlsCUI(Collection<String> umlsCUI) {
        this.umlsCUI = asString(umlsCUI);
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getMeSHMN() {
        return meSHMN;
    }

    public void setMeSHMN(String meSHMN) {
        this.meSHMN = meSHMN;
    }

    public String getIcd10CM() {
        return icd10CM;
    }

    public void setIcd10CM(String icd10CM) {
        this.icd10CM = icd10CM;
    }

    public String getOmim() {
        return omim;
    }

    public void setOmim(String omim) {
        this.omim = omim;
    }

    public String getSnomedct() {
        return snomedct;
    }

    public void setSnomedct(String snomedct) {
        this.snomedct = snomedct;
    }

    public String getUmlsCUI() {
        return umlsCUI;
    }

    public void setUmlsCUI(String umlsCUI) {
        this.umlsCUI = umlsCUI;
    }

    @JsonIgnore
    public Set<PubMedDoc> getPubMedDocs() {
        return pubMedDocs;
    }

    public void setPubMedDocs(Set<PubMedDoc> pubMedDocs) {
        this.pubMedDocs = pubMedDocs;
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public long getPubMedDocCount() {
        return pubMedDocs.size();
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

        if (!(o instanceof Disease)) {
            return false;
        }

        Disease d = (Disease) o;

        return d.getMeSHUI().equals(meSHUI);
    }

    @Override
    public int hashCode() {
        return Objects.hash(meSHUI);
    }

}