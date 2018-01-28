package messagecollection.vikrammastapps.com.models;


import java.io.Serializable;
import java.util.ArrayList;

public class SubcategoryModel implements Serializable {

    private boolean isListeningAllowed;
    private String title;
    private ArrayList<SubSubCategoryModel> textArray;

    public boolean isListeningAllowed() {
        return isListeningAllowed;
    }

    public void setListeningAllowed(boolean listeningAllowed) {
        isListeningAllowed = listeningAllowed;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<SubSubCategoryModel> getTextArray() {
        return textArray;
    }

    public void setTextArray(ArrayList<SubSubCategoryModel> textArray) {
        this.textArray = textArray;
    }
}
