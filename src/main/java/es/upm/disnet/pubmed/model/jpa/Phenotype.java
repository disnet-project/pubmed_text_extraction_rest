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
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;

/**
 * @author Eduardo P. Garcia del Valle
 */
@Entity
public class Phenotype {

    private static final String FULL_TEXT_DELIMITER = ";;";
    @Id
    @Column(name = "phenotypeId", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String meshMH;
    @Column(length = 1000)
    private String meSHMN;
    @Column(unique = true, nullable = false)
    private String meSHUI;
    @NotNull
    @Column(length = 500)
    private String poId;
    @NotNull
    @Column(length = 1000)
    private String poName;
    @Column(length = 1000)
    private String poIsA;
    @Column(length = 3000)
    private String poDef;
    @Column(length = 3000)
    private String snomedct;
    @Column(length = 1000)
    private String umlsCUI;
    @NotNull
    private Date createDate;
    @NotNull
    private String snapshot;

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

    public String getPoId() {
        return poId;
    }

    public void setPoId(String poId) {
        this.poId = poId;
    }

    public void setPoId(Collection<String> poId) {
        this.poId = asString(poId);
    }

    @JsonIgnore
    public Collection<String> getPoIdAsCollection() {
        return asCollection(poId);
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

    public String getPoName() {
        return poName;
    }

    public void setPoName(Collection<String> poName) {
        this.poName = asString(poName, true);
    }

    public void setPoName(String poName) {
        this.poName = poName;
    }

    @JsonIgnore
    public Collection<String> getPoNameAsCollection() {
        return asCollection(poName, true);
    }

    public String getPoIsA() {
        return poIsA;
    }

    public void setPoIsA(Collection<String> poIsA) {
        this.poIsA = asString(poIsA);
    }

    public void setPoIsA(String poIsA) {
        this.poIsA = poIsA;
    }

    @JsonIgnore
    public Collection<String> getPoIsAAsCollection() {
        return asCollection(poIsA);
    }

    public String getPoDef() {
        return poDef;
    }

    public void setPoDef(Collection<String> poDef) {
        this.poDef = asString(poDef, true);
    }

    public void setPoDef(String poDef) {
        this.poDef = poDef;
    }

    @JsonIgnore
    public Collection<String> getPoDefAsCollection() {
        return asCollection(poDef, true);
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

        if (!(o instanceof Phenotype)) {
            return false;
        }

        Phenotype d = (Phenotype) o;

        return d.getMeSHUI().equals(meSHUI);
    }

    @Override
    public int hashCode() {
        return Objects.hash(meSHUI);
    }

}