package com.sanderp.bartrider.pojo.quickplanner;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "@order",
        "@transfercode",
        "@origin",
        "@destination",
        "@origTimeMin",
        "@origTimeDate",
        "@destTimeMin",
        "@destTimeDate",
        "@line",
        "@bikeflag",
        "@trainHeadStation",
        "@load",
        "@trainId",
        "@trainIdx"
})
public class Leg implements Serializable {
    private static final long serialVersionUID = 4533555934408239702L;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy h:mm a", Locale.US);

    @JsonProperty("@order")
    private int order;
    @JsonProperty("@transfercode")
    private String transferCode;
    @JsonProperty("@origin")
    private String origin;
    @JsonProperty("@destination")
    private String destination;
    @JsonProperty("@origTimeMin")
    private String origTimeMin;
    @JsonProperty("@origTimeDate")
    private String origTimeDate;
    @JsonProperty("@destTimeMin")
    private String destTimeMin;
    @JsonProperty("@destTimeDate")
    private String destTimeDate;
    @JsonProperty("@line")
    private String line;
    @JsonProperty("@bikeflag")
    private int bikeFlag;
    @JsonProperty("@trainHeadStation")
    private String trainHeadStation;
    @JsonProperty("@load")
    private int load;
    @JsonProperty("@trainId")
    private long trainId;
    @JsonProperty("@trainIdx")
    private int trainIdx;

    @JsonIgnore
    private long etdOrigTime;
    @JsonIgnore
    private long etdDestTime;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getTransferCode() {
        return transferCode;
    }

    public void setTransferCode(String transferCode) {
        this.transferCode = transferCode;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getOrigTimeMin() {
        return origTimeMin;
    }

    public void setOrigTimeMin(String origTimeMin) {
        this.origTimeMin = origTimeMin;
    }

    public String getOrigTimeDate() {
        return origTimeDate;
    }

    public void setOrigTimeDate(String origTimeDate) {
        this.origTimeDate = origTimeDate;
        setEtdOrigTime(getOrigTimeEpoch());
    }

    /**
     * Converts leg origin time and date to epoch time.
     * @return the epoch time
     */
    public long getOrigTimeEpoch() {
        try {
            return DATE_FORMAT.parse(origTimeDate + " " + origTimeMin).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public String getDestTimeMin() {
        return destTimeMin;
    }

    public void setDestTimeMin(String destTimeMin) {
        this.destTimeMin = destTimeMin;
    }

    public String getDestTimeDate() {
        return destTimeDate;
    }

    public void setDestTimeDate(String destTimeDate) {
        this.destTimeDate = destTimeDate;
        setEtdDestTime(getDestTimeEpoch());
    }

    /**
     * Converts leg destination time and date to epoch time.
     * @return the epoch time
     */
    public long getDestTimeEpoch() {
        try {
            return DATE_FORMAT.parse(destTimeDate + " " + destTimeMin).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public int getBikeFlag() {
        return bikeFlag;
    }

    public void setBikeFlag(int bikeFlag) {
        this.bikeFlag = bikeFlag;
    }

    public String getTrainHeadStation() {
        return trainHeadStation;
    }

    public void setTrainHeadStation(String trainHeadStation) {
        this.trainHeadStation = trainHeadStation;
    }

    public int getLoad() {
        return load;
    }

    public void setLoad(int load) {
        this.load = load;
    }

    public long getTrainId() {
        return trainId;
    }

    public void setTrainId(long trainId) {
        this.trainId = trainId;
    }

    public int getTrainIdx() {
        return trainIdx;
    }

    public void setTrainIdx(int trainIdx) {
        this.trainIdx = trainIdx;
    }

    public long getEtdOrigTime() {
        return etdOrigTime;
    }

    public void setEtdOrigTime(long etdOrigTime) {
        this.etdOrigTime = etdOrigTime;
    }

    public long getEtdDestTime() {
        return etdDestTime;
    }

    public void setEtdDestTime(long etdDestTime) {
        this.etdDestTime = etdDestTime;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
