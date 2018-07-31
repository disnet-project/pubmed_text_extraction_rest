package es.upm.disnet.pubmed.retriever;

import es.upm.disnet.pubmed.common.util.Common;
import es.upm.disnet.pubmed.constants.Constants;
import es.upm.disnet.pubmed.enums.SourceEnum;
import es.upm.disnet.pubmed.model.document_structure.*;
import es.upm.disnet.pubmed.model.document_structure.code.Code;
import es.upm.disnet.pubmed.model.document_structure.code.Resource;
import es.upm.disnet.pubmed.parser.DiseaseOntologyOBOParser;
import es.upm.disnet.pubmed.parser.GenericSemiStructuredTextParser;
import es.upm.disnet.pubmed.parser.MeSHASCIIParser;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by gerardo on 08/03/2018.
 *
 * @author Gerardo Lagunes G. ${EMAIL}
 * @version ${<VERSION>}
 * @project pubmed_text_extraction_rest
 * @className DiseaseRetrieval
 * @see
 */
@Component
public class DiseaseRetrieval {

    @Autowired
    Common common;

    private static final Logger logger = LoggerFactory.getLogger(DiseaseRetrieval.class);
    // View https://meshb.nlm.nih.gov/treeView
    private final static List<String> EXCLUDED_MESH_MN = Arrays.asList(new String[]{"C21", "C22", "C23", "C24", "C25", "C26"});


    /**
     * Retorna una lista de enfermedades con información de d2018.bin y HumanDO.obo
     * @param snapshot
     * @return
     */
    public List<Disease> retrieve(String snapshot) {
        List<Disease> diseases = new ArrayList<>();
        try {
            StopWatch watch = new StopWatch();

            logger.info("Retrieval diseases for snapshot {}.", snapshot);

            watch.start();

            // Obtain MeSH records

            MeSHASCIIParser meSHASCIIParser = new MeSHASCIIParser();

            final List<GenericSemiStructuredTextParser.Record> meshRecords = meSHASCIIParser.parse("mesh/d2018.bin", StandardCharsets.UTF_8.name());
            logger.info("meshRecords in d2018.bin {}.", meshRecords.size());
            //for (GenericSemiStructuredTextParser.Record meshRec: meshRecords) {System.out.println("meshRec.getId(): "+meshRec.getId());}

            // Obtain DO records

            DiseaseOntologyOBOParser diseaseOntologyOBOParser = new DiseaseOntologyOBOParser();

            final List<GenericSemiStructuredTextParser.Record> doRecords = diseaseOntologyOBOParser.parse("do/HumanDO.obo", StandardCharsets.UTF_8.name());
            logger.info("doRecords in HumanDO.obo {}.", doRecords.size());
            // Populate Disease repository with diseases in DO that have MeSH terms

            List<GenericSemiStructuredTextParser.Record> testList = doRecords.stream().filter(o -> o.hasPropertyValue("xref", "MESH")).collect(Collectors.toList());

            testList.size();

            logger.info("doRecords in HumanDO.obo {} - {}.", doRecords.size(), testList.size());

            //TODO: extract to constants

            int count = 1;
            doRecords.stream().filter(o -> o.hasPropertyValue("xref", "MESH:D")).forEach(
                    o -> {
                        String meshUI = o.getPropertyValue("xref", "MESH:");

                        Optional<GenericSemiStructuredTextParser.Record> optMeshRecord = meshRecords.stream().filter(m -> m.getId().equals(meshUI)).findFirst();

                        if (!optMeshRecord.isPresent()) {
                            //logger.error("Mesh Record not found for UI {}", meshUI);
                            return;
                        }

                        try {
                            addDisease(o, optMeshRecord.get(), diseases, snapshot);
                        } catch (Exception e) {
                            logger.error("Error adding disease {}", meshUI, e);
                        }
                    }
            );

            //System.out.println("Diseases Retrieved from PubMed: " + diseases.size());

            watch.stop();

//            logger.info(
//                    "Populating diseases finished in {} seconds. {} diseases were added",
//                    watch.getTime(TimeUnit.SECONDS), diseaseService.countAll());

        } catch (Exception e) {
            logger.error("Error while populating Disease table", e);
        }

        return diseases;
    }


    private void addDisease(GenericSemiStructuredTextParser.Record doRecord, GenericSemiStructuredTextParser.Record meshRecord, List<Disease> diseases, String snapshot) {
        Disease disease = new Disease();

        logger.trace("Adding disease with MESH UI {} from DO Id {}", meshRecord.getId(), doRecord.getId());
        //logger.info("Adding disease with MESH UI {} from DO Id {}", meshRecord.getId(), doRecord.getId());


        disease.setMeSHUI(meshRecord.getId());
        disease.setName(doRecord.getPropertyValue("name"));
        disease.setDefinition(doRecord.getPropertyValue("def"));
        disease.setLinks( getDiseaseLinks(doRecord.getPropertyValue("def")) );
        //set Disease Codes
        disease.setCodes(
                getCodes(doRecord.getPropertyValues("xref", "ICD10CM:"),
                        doRecord.getPropertyValues("xref", "OMIM:"),
                        doRecord.getPropertyValues("xref", "SNOMEDCT_US_2016_03_01:"),
                        doRecord.getPropertyValues("xref", "UMLS_CUI:"),
                        doRecord.getPropertyValues("xref", "ICD9CM:"),
                        doRecord.getPropertyValues("xref", "NCI:")
                        )
        );
        disease.setCodeCount(disease.getCodes().size());
        disease.setSynonyms( getSynonyms(doRecord.getPropertyValues("synonym")) );
        disease.setSynonymCount(disease.getSynonyms().size());

        // DO terms might point to the same MeSHUI. In that case, DO terms are merged by MeSHUI.
        if (diseases.contains(disease)){ /*containsName(diseases, meshRecord.getId())*/
            //Por el momento he decidido no hacer nada con los repetidos.
            //Lo comentaré mañana. Eduardo los fusiona
            Disease existingDisease = diseases.get( diseases.indexOf(disease) );
            //System.out.println("Found: " + meshRecord.getId() + " - " + existingDisease.getMeSHMH());
            //System.out.println("old: " + existingDisease.getName() + " | new: "+ disease.getName());
        }else {
            disease.setMeSHMH(meshRecord.getPropertyValue("MH"));
            disease.setMeSHTerms( getMeshTerms(Arrays.asList( meshRecord.getPropertyValue("MH").split(","))) );
            disease.setMeSHTermCount(disease.getMeSHTerms().size());

            // Filter excluded MeSH descriptors
            List<String> meshMNList =
                    meshRecord.getPropertyValues("MN")
                            .stream()
                            .filter(o -> !EXCLUDED_MESH_MN.contains(o.split("\\.")[0]))
                            .collect(Collectors.toList());

            if (meshMNList.isEmpty()) {
                return;
            }

//            System.out.println(meshRecord.getId() +", "+ doRecord.getId() + " - "+doRecord.getPropertyValue("name"));
            System.out.println(meshRecord.getId() + " - "+doRecord.getPropertyValue("name"));

            disease.setMeSHMN(getMeshTreeCodes(meshRecord.getPropertyValues("MN")));
            diseases.add(disease);
            //System.out.println("Not Found: " + meshRecord.getId());
        }

        //System.out.println("DISEASE: " + disease);

    }


    private List<Link> getDiseaseLinks(String definition){
        List<Link> links = new ArrayList<>();
        if (!common.isEmpty(definition)) {
            String[] urls = definition.split("url:");
            //System.out.println("length: " + urls.length);
            int count = 1, cLink = 1;
            for (String u : urls) {
                if (count!=1) {
                    //System.out.println("url: " + u.replace("http\\://", "").replace("https\\://", ""));
                    String url = u.replace("http\\://", "").replace("https\\://", "");
                    Link link = new Link();
                    link.setId(cLink);
                    link.setUrl(url);
                    if (url.contains("wiki")) {
                        Source source = new Source(Constants.SOURCE_WIKIPEDIA_CODE, Constants.SOURCE_WIKIPEDIA);
                        link.setSource(source);
                    }
                    links.add(link);
                    cLink++;
                }
                count++;
            }
        }

        if (links.size() > 0) return links;
        else return null;
    }


    private List<Synonym> getSynonyms(List<String> synonyms){
        List<Synonym> synonymList = new ArrayList<>();
        List<Code> codes;
        if (synonyms != null) {
            //System.out.println("synonym: " + doRecord.getPropertyValues("synonym").toString());
            int count = 1;
            for (String synonym: synonyms) {
                String[] resource = synonym.split("EXACT");
                if (resource.length == 2){
                    Synonym s = new Synonym();
                    codes = new ArrayList<>();
                    s.setId(count);
                    s.setName(resource[0].replace("\"","").trim());
                    String[] source = resource[1].split(":");
                    if (source.length == 2){
                        if (!common.isEmpty(source[0]) && !common.isEmpty(source[1])){
                            Code code = new Code();
                            code.setCode(source[1].replace("]","").trim());
                            code.setResource(identifyResource(source[0].replace("[","").trim()));
                            codes.add(code);
                        }
                    }
                    s.setCodes(codes);
                    s.setCodeCount(s.getCodes().size());
                    synonymList.add(s);
                    count++;
                }
            }
//            System.out.println("Synonym: " + synonymList);
        }
        return synonymList;
    }

    private Resource identifyResource(String resource){
        int len = SourceEnum.values().length;
        for (SourceEnum source: SourceEnum.values()) {
            //System.out.println(resource+" == "+source.getDescripcion().toUpperCase().replace("-", "").replace("_", ""));
            if (resource.contains(source.getDescripcion().toUpperCase().replace("-", "").replace("_", ""))){
//                System.out.println(resource+" == "+source.getDescripcion().toUpperCase().replace("-", "").replace("_", ""));
                return new Resource(source.getClave(), source.getDescripcion());
            }/*else{
                SourceEnum sourceEnum = ;
                sourceEnum.setClave(len+1);
                sourceEnum.setDescripcion(resource);
                return new Resource(sourceEnum.getClave() , sourceEnum.getDescripcion());
            }*/
        }
        return null;
    }

    private List<Term> getMeshTerms(List<String> meshTerms){
        List<Term> terms = new ArrayList<>();
        Resource mesh = new Resource(SourceEnum.MeSH.getClave(), SourceEnum.MeSH.getDescripcion());

        if (meshTerms != null) {
            int count = 1;
            for (String term: meshTerms) {
                Term t = new Term();
                t.setId(count);
                t.setName(term);
                t.setResource(mesh);
                terms.add(t);
                count++;
            }
        }
        return terms;
    }


    private List<Code> getCodes(List<String> icd10Codes, List<String> omimCodes, List<String> snomedCtCodes, List<String> umlsCodes, List<String> icd9Codes, List<String> nciCodes){
        List<Code> codes = new ArrayList<>();
        Resource icd10 = new Resource(SourceEnum.ICD_10.getClave(), SourceEnum.ICD_10.getDescripcion());
        Resource icd9 = new Resource(SourceEnum.ICD_9.getClave(), SourceEnum.ICD_9.getDescripcion());
        Resource omim = new Resource(SourceEnum.OMIM.getClave(), SourceEnum.OMIM.getDescripcion());
        Resource snomed_ct = new Resource(SourceEnum.SNOMED_CT.getClave(), SourceEnum.SNOMED_CT.getDescripcion());
        Resource umls = new Resource(SourceEnum.UMLS.getClave(), SourceEnum.UMLS.getDescripcion());
        Resource nci = new Resource(SourceEnum.NCI.getClave(), SourceEnum.NCI.getDescripcion());
        int count = 1;
        count = getGenericCode(codes, icd10Codes, count, icd10);
        count = getGenericCode(codes, icd9Codes, count, icd9);
        count = getGenericCode(codes, omimCodes, count, omim);
        count = getGenericCode(codes, snomedCtCodes, count, snomed_ct);
        count = getGenericCode(codes, umlsCodes, count, umls);
        count = getGenericCode(codes, nciCodes, count, nci);

        return codes;
    }



    private int getGenericCode(List<Code> codeList, List<String> codes, int count, Resource resource){
        if (codes != null) {
            for (String code : codes) {
                Code c = new Code();
                c.setId(count);
                c.setCode(code);
                c.setResource(resource);
                codeList.add(c);
                count++;
            }
        }
        return count;
    }


    private List<String> getMeshTreeCodes(List<String> codes){
        // Filter excluded MeSH descriptors
        List<String> meshMNList =
                codes
                        .stream()
                        .filter(o -> !EXCLUDED_MESH_MN.contains(o.split("\\.")[0]))
                        .collect(Collectors.toList());

        if (meshMNList.isEmpty()) return null;
        else return meshMNList;
    }


    private Collection<String> combine(Collection<String> a, Collection<String> b) {
        return Stream.concat(a.stream(), b.stream()).collect(Collectors.toList());
    }

    public boolean containsName(final List<Disease> list, final String meshUI){
        return list.stream().map(Disease::getMeSHUI).filter(meshUI::equals).findFirst().isPresent();
    }

}
