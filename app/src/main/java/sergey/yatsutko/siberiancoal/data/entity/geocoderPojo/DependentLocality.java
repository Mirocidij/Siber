
package sergey.yatsutko.siberiancoal.data.entity.geocoderPojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DependentLocality {

    @SerializedName("DependentLocalityName")
    @Expose
    private String dependentLocalityName;
    @SerializedName("DependentLocality")
    @Expose
    private DependentLocality_ dependentLocality;
    @SerializedName("Thoroughfare")
    @Expose
    private Thoroughfare__ thoroughfare;

    public String getDependentLocalityName() {
        return dependentLocalityName;
    }

    public void setDependentLocalityName(String dependentLocalityName) {
        this.dependentLocalityName = dependentLocalityName;
    }

    public DependentLocality_ getDependentLocality() {
        return dependentLocality;
    }

    public void setDependentLocality(DependentLocality_ dependentLocality) {
        this.dependentLocality = dependentLocality;
    }

    public Thoroughfare__ getThoroughfare() {
        return thoroughfare;
    }

    public void setThoroughfare(Thoroughfare__ thoroughfare) {
        this.thoroughfare = thoroughfare;
    }

}
