/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.thingsboard.server.dao.mongo;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.thingsboard.server.common.data.Image;
import org.thingsboard.server.common.data.TagLandlot;

/**
 *
 * @author carlos
 */
public class ImageML {
    private List<Image> imgs;
    private TagLandlot tg;

    public ImageML() {
    }

    public ImageML(List<Image> imgs, TagLandlot tg) throws IOException {
        this.imgs = imgs;
        this.tg = tg;
    }
    
    private boolean intersects(int[] A, int[] B, double[] P) {
        if (A[1] > B[1])
            return intersects(B, A, P);
 
        if (P[1] == A[1] || P[1] == B[1])
            P[1] += 0.0001;
 
        if (P[1] > B[1] || P[1] < A[1] || P[0] >= Math.max(A[0], B[0]))
            return false;
 
        if (P[0] < Math.min(A[0], B[0]))
            return true;
 
        double red = (P[1] - A[1]) / (double) (P[0] - A[0]);
        double blue = (B[1] - A[1]) / (double) (B[0] - A[0]);
        return red >= blue;
    }
    
    private boolean detectRisk(double value, int days){
        double result = (0.0000016*Math.pow(value, 3))-(0.0004905*Math.pow(value, 2))+(0.0390512*value);
        return value>=result;
    }
 
    private boolean contains(int[][] shape, double[] pnt) {
        boolean inside = false;
        int len = shape.length;
        for (int i = 0; i < len; i++) {
            if (intersects(shape[i], shape[(i + 1) % len], pnt))
                inside = !inside;
        }
        return inside;
    }
    
    
    private double getWidthGPStoPixels(double[] upperRight, double[] upperLeft, int widthImage, double checkLongitude){
        double diffLongitude = Math.abs(upperRight[0] - upperLeft[0]);
        double diffcheckLong = Math.abs(checkLongitude - upperLeft[0]);
        return  (diffcheckLong*widthImage)/diffLongitude;
    }
    
    private double getHeightGPStoPixels(double[] upperRight, double[] upperLeft, int heightImage, double checkLatitude){
        double diffLatitude = Math.abs(upperRight[1] - upperLeft[1]);
        double diffcheckLat = Math.abs(checkLatitude - upperLeft[1]);
        return  (diffcheckLat*heightImage)/diffLatitude;
    }

    public void calculateNDVI() throws IOException {

        BufferedImage red = null;
        BufferedImage nir = null;
        List<Double> coordinatesC = null;

        int height = 0, width = 0;

        for (Image img : imgs) {
            if (img.getName().contains("NIR")) {
                nir = img.getImg();
                height = img.getImg().getHeight();
                width = img.getImg().getWidth();
                coordinatesC = img.getCoordinates();
            }else if(img.getName().contains("RED")){
                red = img.getImg();
            }
        }
        
        double[][] nvdi = new double[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Color nircolor = new Color(nir.getRGB(j, i));
                Color redcolor = new Color(red.getRGB(j, i));
                double sumnir = ((int) (nircolor.getRed() * 0.21)) + ((int) (nircolor.getGreen() * 0.72)) + ((int) (nircolor.getBlue() * 0.07));
                double sumred = ((int) (redcolor.getRed() * 0.21)) + ((int) (redcolor.getGreen() * 0.72)) + ((int) (redcolor.getBlue() * 0.07));
                nvdi[i][j] = ((sumnir - sumred) / (sumnir + sumred));
            }
        }
        
        //Centro
        System.out.println(coordinatesC);

        //punto superior derecha
        double[] coordinatesSD = {coordinatesC.get(0) + 0.0001, coordinatesC.get(1) + 0.0001};

        //punto superio izquierda
        double[] coordinatesSI = {coordinatesC.get(0) - 0.0001, coordinatesC.get(1) + 0.0001};

        //punto inferior derecha
        double[] coordinatesID = {coordinatesC.get(0) + 0.0001, coordinatesC.get(1) - 0.0001};

        //punto inferior izquierda
        double[] coordinatesII = {coordinatesC.get(0) - 0.0001, coordinatesC.get(1) - 0.0001};
       
        
        int cont = 0;
        int[][] tagCoordinates = new int[tg.getTagPolygon().getCoordinates().get(0).size()][2];

        //Tag
        for (List<Double> d : tg.getTagPolygon().getCoordinates().get(0)) {
            int gpstopixellong = (int)getWidthGPStoPixels(coordinatesSD, coordinatesSI, width, d.get(0));
            int gpstopixellat = (int)getHeightGPStoPixels(coordinatesSD, coordinatesID, height, d.get(1));
            tagCoordinates[cont][0] = gpstopixellong;
            tagCoordinates[cont][1] = gpstopixellat;
            cont+=1;
        }
        
        int cont1 = 0;int cont2 = 0;
        
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                double[] position = {i,j};
                if(contains(tagCoordinates, position)){
                    cont1+=1;
                    if(detectRisk(nvdi[i][j],10)){
                        cont2+=1;
                    }
                }
            }
        }
        System.out.println("PROBABILITY: "+Double.toString(((double)cont2/(double)cont1)));
        if(((double)cont2/(double)cont1)>0.4){
            System.out.println("THE CROP HAS A PEST RISK GREATHER THAN 0.4%");
        }
        
    }
}
