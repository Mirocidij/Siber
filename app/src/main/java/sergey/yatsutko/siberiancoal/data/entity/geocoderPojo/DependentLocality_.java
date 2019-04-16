
package sergey.yatsutko.siberiancoal.data.entity.geocoderPojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DependentLocality_ {

    @SerializedName("DependentLocalityName")
    @Expose
    private String dependentLocalityName;
    @SerializedName("Thoroughfare")
    @Expose
    private Thoroughfare_ thoroughfare;

    public String getDependentLocalityName() {
        return dependentLocalityName;
    }

    public void setDependentLocalityName(String dependentLocalityName) {
        this.dependentLocalityName = dependentLocalityName;
    }

    public Thoroughfare_ getThoroughfare() {
        return thoroughfare;
    }

    public void setThoroughfare(Thoroughfare_ thoroughfare) {
        this.thoroughfare = thoroughfare;
    }

}
