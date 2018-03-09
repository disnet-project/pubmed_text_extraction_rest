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
    private String meshUI;//único para pubmed
    private String meshMH;//términos mesh con los que se buscan los artículos
    //mismos que meshMH, pero dentro de una lista separados
    private Integer meshTermCount;
    private List<Term> meshTerms;
    private List<String> meshMN;//códigos de la estructura de árbol
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer synonymCount;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Synonym> synonyms;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer codeCount;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Code> codes;
    private String isA;//Relacionado con otras enfermedades



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

    public String getMeshUI() {
        return meshUI;
    }

    public void setMeshUI(String meshUI) {
        this.meshUI = meshUI;
    }

    public String getMeshMH() {
        return meshMH;
    }

    public void setMeshMH(String meshMH) {
        this.meshMH = meshMH;
    }

    public Integer getMeshTermCount() {
        return meshTermCount;
    }

    public void setMeshTermCount(Integer meshTermCount) {
        this.meshTermCount = meshTermCount;
    }

    public List<Term> getMeshTerms() {
        return meshTerms;
    }

    public void setMeshTerms(List<Term> meshTerms) {
        this.meshTerms = meshTerms;
    }

    public List<String> getMeshMN() {
        return meshMN;
    }

    public void setMeshMN(List<String> meshMN) {
        this.meshMN = meshMN;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Disease)) return false;
        Disease disease = (Disease) o;
        return Objects.equals(getMeshUI(), disease.getMeshUI());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMeshUI());
    }
}
