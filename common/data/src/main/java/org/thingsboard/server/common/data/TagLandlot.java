/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.thingsboard.server.common.data;

import java.util.HashMap;

/**
 *
 * @author carlos
 */
public class TagLandlot {
    String idLandlot, tag, cropName, imageName;
    long date;
    HashMap<String, Double> telemetryData;
    Polygon tagPolygon;
    int timeElapsed;

    public TagLandlot() {
    }

    public TagLandlot(String idLandlot, String tag, String cropName, String imageName, long date, HashMap<String, Double> telemetryData, Polygon tagPolygon, int timeElapsed) {
        this.idLandlot = idLandlot;
        this.tag = tag;
        this.cropName = cropName;
        this.imageName = imageName;
        this.date = date;
        this.telemetryData = telemetryData;
        this.tagPolygon = tagPolygon;
        this.timeElapsed = timeElapsed;
    }

    public String getIdLandlot() {
        return idLandlot;
    }

    public void setIdLandlot(String idLandlot) {
        this.idLandlot = idLandlot;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public HashMap<String, Double> getTelemetryData() {
        return telemetryData;
    }

    public void setTelemetryData(HashMap<String, Double> telemetryData) {
        this.telemetryData = telemetryData;
    }

    public Polygon getTagPolygon() {
        return tagPolygon;
    }

    public void setTagPolygon(Polygon tagPolygon) {
        this.tagPolygon = tagPolygon;
    }

    public int getTimeElapsed() {
        return timeElapsed;
    }

    public void setTimeElapsed(int timeElapsed) {
        this.timeElapsed = timeElapsed;
    }
    
    @Override
    public String toString(){
        return "TagLandLot:[idLandlot: "+idLandlot+", tag: "+tag+", cropName: "+cropName+", imageName: "+imageName+", date: "+Long.toString(date)+", telemetryData: "+telemetryData.toString()+", timeElapsed: "+Integer.toString(timeElapsed)+", polygon: "+tagPolygon.toString();
    }
}
