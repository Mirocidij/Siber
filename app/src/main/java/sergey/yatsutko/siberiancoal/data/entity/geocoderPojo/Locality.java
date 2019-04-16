
package sergey.yatsutko.siberiancoal.data.entity.geocoderPojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Locality {

    @SerializedName("LocalityName")
    @Expose
    private String localityName;
    @SerializedName("Thoroughfare")
    @Expose
    private Thoroughfare thoroughfare;
    @SerializedName("DependentLocality")
    @Expose
    private DependentLocality dependentLocality;

    public String getLocalityName() {
        return localityName;
    }

    public void setLocalityName(String localityName) {
        this.localityName = localityName;
    }

    public Thoroughfare getThoroughfare() {
        return thoroughfare;
    }

    public void setThoroughfare(Thoroughfare thoroughfare) {
        this.thoroughfare = thoroughfare;
    }

    public DependentLocality getDependentLocality() {
        return dependentLocality;
    }

    public void setDependentLocality(DependentLocality dependentLocality) {
        this.dependentLocality = dependentLocality;
    }

}
