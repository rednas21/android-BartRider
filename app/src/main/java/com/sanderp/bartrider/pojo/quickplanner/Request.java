package com.sanderp.bartrider.pojo.quickplanner;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.sanderp.bartrider.pojo.realtimeetd.RealTimeEtdPojo;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "trip"
})
public class Request implements Serializable {
    private static final long serialVersionUID = 7493110658643323575L;

    @JsonProperty("trip")
    private List<Trip> trips = null;

    public Trip getTrip(int index) {
        return trips.get(index);
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }

    public ArrayList<Object> buildBatchLoad() {
        ArrayList<Object> batchLoad = new ArrayList<>();
        HashSet<String> headStationSet = new HashSet<>();
        for (Trip trip : trips) {
            String origAbbr = trip.getOrigin();
            String headAbbr = trip.getLeg(0).getTrainHeadStation();
            if (!headStationSet.contains(headAbbr)) {
                RealTimeEtdPojo pojo = new RealTimeEtdPojo();
                pojo.setOrigAbbr(origAbbr);
                pojo.setHeadAbbr(headAbbr);
                batchLoad.add(pojo);
                headStationSet.add(headAbbr);
            }
        }
        return batchLoad;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
