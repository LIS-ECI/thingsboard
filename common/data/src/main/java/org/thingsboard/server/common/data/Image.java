package org.thingsboard.server.common.data;

import java.util.Arrays;

/**
 *
 * @author carlos
 */
public class Image{
    
    private String name, latitude, longitude, fileSize, modifiedDate;
    private Double[] coordinates;
    
    public Image(){
    }

    public Image(String name, String latitude, String longitude, String fileSize, String modifiedDate, Double[] coordinates) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.fileSize = fileSize;
        this.modifiedDate = modifiedDate;
        this.coordinates = coordinates;
    }
    
    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public Double[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Double[] coordinates) {
        this.coordinates = coordinates;
    }
    
    @Override
    public String toString(){
        return "[name: "+name+", latitude: "+latitude+", longitud: "+longitude+", fileSize: "+fileSize+", modifiedDate: "+modifiedDate+", coordinates: "+Arrays.toString(coordinates)+"]";
    }
    
    
}