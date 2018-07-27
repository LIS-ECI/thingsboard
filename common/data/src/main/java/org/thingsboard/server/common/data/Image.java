package org.thingsboard.server.common.data;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 *
 * @author carlos
 */
public class Image{
    
    private String name;
    private String latitude;
    private String longitude;
    private String fileSize;
    private String modifiedDate;
    private String src;
    private BufferedImage img;
    private List<Double> coordinates;
    
    public Image(){
    }

    public Image(String name, String latitude, String longitude, String fileSize, String modifiedDate, List<Double> coordinates,String src) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.fileSize = fileSize;
        this.modifiedDate = modifiedDate;
        this.coordinates = coordinates;
        this.src=src;
    }

    public Image(String name, String latitude, String longitude, String fileSize, String modifiedDate, String src, BufferedImage img, List<Double> coordinates) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.fileSize = fileSize;
        this.modifiedDate = modifiedDate;
        this.src = src;
        this.img = img;
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
    
    public List<Double> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Double> coordinates) {
        this.coordinates = coordinates;
    }
    
    @Override
    public String toString(){
        return "name: "+name+", latitude: "+latitude+", longitud: "+longitude+", fileSize: "+fileSize+", modifiedDate: "+modifiedDate+", coordinates: "+coordinates+" ";
    }


    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public BufferedImage getImg() {
        return img;
    }

    public void setImg(BufferedImage img) {
        this.img = img;
    }
    
    
}