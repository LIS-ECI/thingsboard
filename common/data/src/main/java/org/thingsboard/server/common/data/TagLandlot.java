/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.thingsboard.server.common.data;

/**
 *
 * @author carlos
 */
public class TagLandlot {
    String idLandlot, tag;
    long date;
    Polygon tagPolygon;

    public TagLandlot() {
    }
    
    public TagLandlot(String idLandlot, String tag, long date, Polygon tagPolygon) {
        this.idLandlot = idLandlot;
        this.tag = tag;
        this.date = date;
        this.tagPolygon = tagPolygon;
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

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public Polygon getTagPolygon() {
        return tagPolygon;
    }

    public void setTagPolygon(Polygon tagPolygon) {
        this.tagPolygon = tagPolygon;
    }
    
}
