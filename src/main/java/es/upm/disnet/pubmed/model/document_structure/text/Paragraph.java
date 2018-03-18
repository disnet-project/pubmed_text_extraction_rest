package es.upm.disnet.pubmed.model.document_structure.text;
/**
 * Created by gerardo on 4/4/17.
 *
 * @author Gerardo Lagunes G. ${EMAIL}
 * @version ${<VERSION>}
 * @project ExtractionInformationWikipedia
 * @className Paragraph
 * @see
 */
public class Paragraph extends Text {

    private String text;


    public Paragraph() {
    }

    public Paragraph(int id, int textOrder, String paperId, String text) {
        super(id, textOrder, paperId);
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Paragraph{" +
                " id=" + getId() + '\'' +
                ", order=" + getTextOrder() + '\'' +
                ", paperId=" + getPaperId() + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
