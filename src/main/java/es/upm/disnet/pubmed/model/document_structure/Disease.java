package es.upm.disnet.pubmed.model.document_structure;

import com.fasterxml.jackson.annotation.JsonInclude;
import es.upm.disnet.pubmed.model.document_structure.code.Code;

import java.util.List;
import java.util.Objects;

/**
 * Created by gerardo on 29/3/17.
 * @project ExtractionInformationWikipedia
 * @version ${<VERSION>}
 * @author Gerardo Lagunes G.
 * @className Disease
 * @see
 */
public class Disease {

    private Integer id;
    private String name;
    private String definition;
    private String cui;
    private String meSHUI;//único para pubmed
    private String meSHMH;//términos mesh con los que se buscan los artículos
    //mismos que meSHMH, pero dentro de una lista separados
    private Integer meSHTermCount;
    private List<Term> meSHTerms;
    private List<String> meSHMN;//códigos de la estructura de árbol
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer synonymCount;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Synonym> synonyms;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer codeCount;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Code> codes;
    private String isA;//Relacionado con otras enfermedades
    private List<Link> links;



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getCui() {
        return cui;
    }

    public void setCui(String cui) {
        this.cui = cui;
    }

    public String getMeSHUI() {
        return meSHUI;
    }

    public void setMeSHUI(String meSHUI) {
        this.meSHUI = meSHUI;
    }

    public String getMeSHMH() {
        return meSHMH;
    }

    public void setMeSHMH(String meSHMH) {
        this.meSHMH = meSHMH;
    }

    public Integer getMeSHTermCount() {
        return meSHTermCount;
    }

    public void setMeSHTermCount(Integer meSHTermCount) {
        this.meSHTermCount = meSHTermCount;
    }

    public List<Term> getMeSHTerms() {
        return meSHTerms;
    }

    public void setMeSHTerms(List<Term> meSHTerms) {
        this.meSHTerms = meSHTerms;
    }

    public List<String> getMeSHMN() {
        return meSHMN;
    }

    public void setMeSHMN(List<String> meSHMN) {
        this.meSHMN = meSHMN;
    }

    public Integer getSynonymCount() {
        return synonymCount;
    }

    public void setSynonymCount(Integer synonymCount) {
        this.synonymCount = synonymCount;
    }

    public List<Synonym> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(List<Synonym> synonyms) {
        this.synonyms = synonyms;
    }

    public Integer getCodeCount() {
        return codeCount;
    }

    public void setCodeCount(Integer codeCount) {
        this.codeCount = codeCount;
    }

    public List<Code> getCodes() {
        return codes;
    }

    public void setCodes(List<Code> codes) {
        this.codes = codes;
    }

    public String getIsA() {
        return isA;
    }

    public void setIsA(String isA) {
        this.isA = isA;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Disease)) return false;
        Disease disease = (Disease) o;
        return Objects.equals(getMeSHUI(), disease.getMeSHUI());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMeSHUI());
    }

    @Override
    public String toString() {
        return "Disease{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", definition='" + definition + '\'' +
                ", cui='" + cui + '\'' +
                ", meSHUI='" + meSHUI + '\'' +
                ", meSHMH='" + meSHMH + '\'' +
                ", meSHTermCount=" + meSHTermCount +
                ", meSHTerms=" + meSHTerms +
                ", meSHMN=" + meSHMN +
                ", synonymCount=" + synonymCount +
                ", synonyms=" + synonyms +
                ", codeCount=" + codeCount +
                ", codes=" + codes +
                ", isA='" + isA + '\'' +
                ", links=" + links +
                '}';
    }
}
