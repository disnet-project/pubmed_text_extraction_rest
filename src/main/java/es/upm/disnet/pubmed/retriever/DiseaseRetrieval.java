package es.upm.disnet.pubmed.retriever;

import es.upm.disnet.pubmed.constants.Constants;
import es.upm.disnet.pubmed.enums.SourceEnum;
import es.upm.disnet.pubmed.model.document_structure.Disease;
import es.upm.disnet.pubmed.model.document_structure.Synonym;
import es.upm.disnet.pubmed.model.document_structure.Term;
import es.upm.disnet.pubmed.model.document_structure.code.Code;
import es.upm.disnet.pubmed.model.document_structure.code.Resource;
import es.upm.disnet.pubmed.parser.DiseaseOntologyOBOParser;
import es.upm.disnet.pubmed.parser.GenericSemiStructuredTextParser;
import es.upm.disnet.pubmed.parser.MeSHASCIIParser;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
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

    private static final Logger logger = LoggerFactory.getLogger(DiseaseRetrieval.class);
    // View https://meshb.nlm.nih.gov/treeView
    private final static List<String> EXCLUDED_MESH_MN = Arrays.asList(new String[]{"C21", "C22", "C23", "C24", "C25", "C26"});


    /**
     * Retorna una lista de enfermedades con información de d2018.bin y HumanDO.obo
     * @param snapshot
     * @return
     */
    public void retrieval(String snapshot) {
        List<Disease> diseases = new ArrayList<>();
        try {
            StopWatch watch = new StopWatch();

            logger.info("Retrieval diseases for snapshot {}.", snapshot);

            watch.start();

            // Obtain MeSH records

            Path meshPath = Paths.get(
                    Thread.currentThread().getContextClassLoader().getResource("mesh/d2018.bin").toURI());

            MeSHASCIIParser meSHASCIIParser = new MeSHASCIIParser();

            final List<GenericSemiStructuredTextParser.Record> meshRecords = meSHASCIIParser.parse(meshPath, StandardCharsets.UTF_8.name());
            logger.info("meshRecords in d2018.bin {}.", meshRecords.size());
            //for (GenericSemiStructuredTextParser.Record meshRec: meshRecords) {System.out.println("meshRec.getId(): "+meshRec.getId());}

            // Obtain DO records

            Path doPath = Paths.get(
                    Thread.currentThread().getContextClassLoader().getResource("do/HumanDO.obo").toURI());

            DiseaseOntologyOBOParser diseaseOntologyOBOParser = new DiseaseOntologyOBOParser();

            final List<GenericSemiStructuredTextParser.Record> doRecords = diseaseOntologyOBOParser.parse(doPath, StandardCharsets.UTF_8.name());

            // Populate Disease repository with diseases in DO that have MeSH terms

            List<GenericSemiStructuredTextParser.Record> testList = doRecords.stream().filter(o -> o.hasPropertyValue("xref", "MESH")).collect(Collectors.toList());

            testList.size();

            logger.info("doRecords in HumanDO.obo {} - {}.", doRecords.size(), testList.size());

            //TODO: extract to constants

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

            System.out.println("sixe: " + diseases.size());

            watch.stop();

//            logger.info(
//                    "Populating diseases finished in {} seconds. {} diseases were added",
//                    watch.getTime(TimeUnit.SECONDS), diseaseService.countAll());

        } catch (Exception e) {
            logger.error("Error while populating Disease table", e);
        }
    }


    private void addDisease(GenericSemiStructuredTextParser.Record doRecord, GenericSemiStructuredTextParser.Record meshRecord, List<Disease> diseases, String snapshot) {
        Disease disease = new Disease();

        logger.trace("Adding disease with MESH UI {} from DO Id {}", meshRecord.getId(), doRecord.getId());
        //logger.info("Adding disease with MESH UI {} from DO Id {}", meshRecord.getId(), doRecord.getId());
        System.out.println(meshRecord.getId() +", "+ doRecord.getId());

        disease.setMeshUI(meshRecord.getId());
        disease.setName(doRecord.getPropertyValue("name"));
        disease.setDefinition(doRecord.getPropertyValue("def"));
        //set Disease Codes
        disease.setCodes(
                getCodes(doRecord.getPropertyValues("xref", "ICD10CM:"),
                        doRecord.getPropertyValues("xref", "OMIM:"),
                        doRecord.getPropertyValues("xref", "SNOMEDCT_US_2016_03_01:"),
                        doRecord.getPropertyValues("xref", "UMLS_CUI:")
                        )
        );
        disease.setCodeCount(disease.getCodes().size());
        disease.setSynonyms( getSynonyms(doRecord.getPropertyValues("synonym")) );

        if (diseases.contains(disease)){ /*containsName(diseases, meshRecord.getId())*/
            Disease existingDisease = diseases.get( diseases.indexOf(disease) );
            System.out.println("Found: " + meshRecord.getId());
            System.out.println("old: " + existingDisease.getMeshMH() + " | new: "+ disease.getMeshMH());
        }else {
            disease.setMeshMH(meshRecord.getPropertyValue("MH"));
            disease.setMeshTerms( getMeshTerms(Arrays.asList( meshRecord.getPropertyValue("MH").split(","))) );
            disease.setMeshTermCount(disease.getMeshTerms().size());
            disease.setMeshMN(getMeshTreeCodes(meshRecord.getPropertyValues("MN")));
            diseases.add(disease);
            //System.out.println("Not Found: " + meshRecord.getId());
        }




    }


    private List<Synonym> getSynonyms(List<String> synonyms){
        List<Synonym> synonymList = new ArrayList<>();
        if (synonyms != null) {
            //System.out.println("synonym: " + doRecord.getPropertyValues("synonym").toString());
            int count = 1;
            for (String synonym: synonyms) {
                String[] resource = synonym.split("EXACT");
                if (resource.length > 0){
                    Synonym s = new Synonym();
                    s.setId(count);
                    s.setName(resource[0].trim());
                    String[] source = resource[1].split(":");
                    if (source.length > 0){

                    }
                    count++;
                }
            }
            //System.out.println("synonym: " + synonyms.);
        }
        return synonymList;
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


    private List<Code> getCodes(List<String> icd10Codes, List<String> omimCodes, List<String> snomedCtCodes, List<String> umlsCodes){
        List<Code> codes = new ArrayList<>();
        Resource icd10 = new Resource(SourceEnum.ICD_10.getClave(), SourceEnum.ICD_10.getDescripcion());
        Resource omim = new Resource(SourceEnum.OMIM.getClave(), SourceEnum.OMIM.getDescripcion());
        Resource snomed_ct = new Resource(SourceEnum.SNOMED_CT.getClave(), SourceEnum.SNOMED_CT.getDescripcion());
        Resource umls = new Resource(SourceEnum.UMLS.getClave(), SourceEnum.UMLS.getDescripcion());
        int count = 1;
        count = getGenericCode(codes, icd10Codes, count, icd10);
        count = getGenericCode(codes, omimCodes, count, omim);
        count = getGenericCode(codes, snomedCtCodes, count, snomed_ct);
        count = getGenericCode(codes, umlsCodes, count, umls);

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
        return list.stream().map(Disease::getMeshUI).filter(meshUI::equals).findFirst().isPresent();
    }

}
