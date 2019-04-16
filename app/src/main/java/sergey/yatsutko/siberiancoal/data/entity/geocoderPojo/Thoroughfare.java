
package sergey.yatsutko.siberiancoal.data.entity.geocoderPojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Thoroughfare {

    @SerializedName("ThoroughfareName")
    @Expose
    private String thoroughfareName;
    @SerializedName("Premise")
    @Expose
    private Premise premise;

    public String getThoroughfareName() {
        return thoroughfareName;
    }

    public void setThoroughfareName(String thoroughfareName) {
        this.thoroughfareName = thoroughfareName;
    }

    public Premise getPremise() {
        return premise;
    }

    public void setPremise(Premise premise) {
        this.premise = premise;
    }

}
