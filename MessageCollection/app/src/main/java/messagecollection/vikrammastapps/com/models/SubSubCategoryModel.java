package messagecollection.vikrammastapps.com.models;


import java.io.Serializable;
import java.util.ArrayList;

public class SubSubCategoryModel implements Serializable {

    private String subtitle;
    private ArrayList<String> subArray;



    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public ArrayList<String> getSubArray() {
        return subArray;
    }

    public void setSubArray(ArrayList<String> subArray) {
        this.subArray = subArray;
    }
}
