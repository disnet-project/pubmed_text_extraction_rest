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
import java.util.Date;
import java.util.Objects;

/**
 * @author Eduardo P. Garcia del Valle
 */
@Entity
public class Term {
    public static final String SOURCE_PUBMED = "pubmed";
    public static final String SOURCE_DISNET = "disnet";
    public static final String SOURCE_ALL = "all";
    @Id
    @Column(name = "termId", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @NotNull
    @Column(length = 500)
    private String name;
    @NotNull
    private String types;
    @NotNull
    private String cui;
    private int frequency;
    private boolean validated;
    @ManyToOne
    @JoinColumn(name = "disease_id")
    private Disease disease;
    @NotNull
    private Date createDate;
    @NotNull
    private String snapshot;
    @NotNull
    private String source;

    public Term() {
    }

    public Term(String cui) {
        this.cui = cui;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public void setTypes(String[] types) {
        this.types = StringUtils.arrayToCommaDelimitedString(types);
    }

    @JsonIgnore
    public String[] getTypesAsArray() {
        return StringUtils.commaDelimitedListToStringArray(types);
    }

    public String getCui() {
        return cui;
    }

    public void setCui(String cui) {
        this.cui = cui;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    public Disease getDisease() {
        return disease;
    }

    public void setDisease(Disease disease) {
        this.disease = disease;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(String snapshot) {
        this.snapshot = snapshot;
    }

    @Override
    public String toString() {
        return cui + ":" + frequency;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Term)) {
            return false;
        }

        Term t = (Term) o;

        return t.getCui().equals(cui);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cui);
    }


}