package org.thingsboard.server.dao.mongo;

import com.drew.metadata.Tag;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSDownloadByNameOptions;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import com.drew.metadata.Metadata;

import com.mongodb.client.model.Filters;
import org.apache.tomcat.jni.Directory;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.aspectj.util.FileUtil;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.thingsboard.server.common.data.Image;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.thingsboard.server.common.data.Point;
import org.thingsboard.server.common.data.SpatialLandlot;

import static sun.security.krb5.Confounder.bytes;
//import org.apache.commons.io;

/**
 *
 * @author carlos
 */
public class MongoDbImage extends MongoConnection {

    private MongoDBSpatialLandlot spatialLandlot;

    public MongoDbImage() {
    }

    public MongoDbImage(MongoDBSpatialLandlot spatialLandlot){
        this.spatialLandlot = spatialLandlot;
    }

    public void uploadFile(File file, HashMap<String,String> metadata) throws Exception {
        // Get the input stream
        if(!findFilesStores(file.getName())){
            InputStream streamToUploadFrom = new FileInputStream(file);
            Document doc = new Document();
            metadata.forEach((k,v) -> doc.append(k,v));

            // Create some custom options
            GridFSUploadOptions options = new GridFSUploadOptions()
                    .chunkSizeBytes(1048576*2)
                    .metadata(doc);
            ObjectId fileId = getGridFSDatabase().uploadFromStream(file.getName(), streamToUploadFrom, options);
            System.out.println("Archivo ya subido!!");
        }else{
            throw new MongoDBException("File already exist!");
        }
    }

    public void uploadMultipleFiles(File file,Map<String,Document> tempInfo) throws Exception {
        if(!findFilesStores(file.getName())){
            InputStream streamToUploadFrom = new FileInputStream(file);
            if(file.getName().contains("RGB")){
                Image i = new Image();
                Metadata metadata = JpegMetadataReader.readMetadata(file);
                i = setImageValues(metadata,i);
                List<Double> coord = new ArrayList<>();
                coord.add(toDecimalResult(i.getLongitude().replaceAll(",",".")));
                coord.add(toDecimalResult(i.getLatitude().replaceAll(",",".")));
                i.setCoordinates(coord);
                System.out.println(i.toString());
                Point point = new Point(coord,"Point");
                SpatialLandlot sp = spatialLandlot.findNearestLandlot(point);
                System.out.println(sp.toString());
                Document doc = new Document();
                doc.append("landlotId",sp.getId());
                String dateString = i.getModifiedDate().replace(":","/");
                dateString = dateString.split(" ")[0];
                String reportDate = dateString;
                doc.append("date",reportDate);
                doc.append("coordinates",i.getCoordinates());
                GridFSUploadOptions options = new GridFSUploadOptions()
                        .chunkSizeBytes(1048576*2)
                        .metadata(doc);
                tempInfo.put(file.getName().replace("RGB.JPG",""),doc);
                ObjectId fileId = getGridFSDatabase().uploadFromStream(file.getName(), streamToUploadFrom, options);
                System.out.println("Archivo ya subido!!");
            }else{
                if(tempInfo.containsKey(file.getName().subSequence(0,file.getName().length()-7))){
                    GridFSUploadOptions options = new GridFSUploadOptions()
                            .chunkSizeBytes(1048576*2)
                            .metadata(tempInfo.get(file.getName().subSequence(0,file.getName().length()-7)));
                    ObjectId fileId = getGridFSDatabase().uploadFromStream(file.getName(), streamToUploadFrom, options);
                    System.out.println("Archivo ya subido!!");
                }
            }
        }else{
            throw new MongoDBException("File already exist!");
        }
    }

    public void findFilesStores() {
        getGridFSDatabase().find().forEach(
                new Block<GridFSFile>() {
                    @Override
                    public void apply(final GridFSFile gridFSFile) {
                        System.out.println(gridFSFile.getFilename());
                    }
                });
    }
    
    public void findFilesByPrefix(String prefix) {
        getGridFSDatabase().find().forEach(
                new Block<GridFSFile>() {
                    @Override
                    public void apply(final GridFSFile gridFSFile) {
                        System.out.println(gridFSFile.getFilename());
                    }
                });
    }
    
    public List<Image> findPhotosByPrefix(String prefix) {
        List<Image> imgs = new ArrayList<>();
        getGridFSDatabase().find(Filters.regex("filename", prefix)).forEach(new Block<GridFSFile>() {
            @Override
            public void apply(final GridFSFile gridFSFile) {
                FileOutputStream streamToDownloadTo;
                Image img = new Image();
                try {
                    streamToDownloadTo = new FileOutputStream(gridFSFile.getFilename());
                    getGridFSDatabase().downloadToStream(gridFSFile.getFilename(), streamToDownloadTo);
                    streamToDownloadTo.close();
                   
                    
                    File f = new File(gridFSFile.getFilename());
                    BufferedImage bfimg = ImageIO.read(f);
                    img.setImg(bfimg);
                    img.setName(gridFSFile.getFilename());
                    img.setCoordinates(((List<Double>) gridFSFile.getMetadata().get("coordinates")));
                    imgs.add(img);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(MongoDbImage.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(MongoDbImage.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
        return imgs;
    }

    public File getFrontImage(String farmId) throws Exception {
        GridFSFile gridFSFile = getGridFSDatabase().find(Filters.eq("metadata.FarmId", farmId)).first();
        if(gridFSFile.getFilename() != null){
            GridFSDownloadStream downloadStream = getGridFSDatabase().openDownloadStreamByName(gridFSFile.getFilename());
            int fileLength = (int) downloadStream.getGridFSFile().getLength();
            byte[] bytesToWriteTo = new byte[fileLength];
            downloadStream.read(bytesToWriteTo);
            downloadStream.close();
            File frontFile = new File(gridFSFile.getFilename());
            org.apache.commons.io.FileUtils.writeByteArrayToFile(frontFile, bytesToWriteTo);
            return frontFile;
        }else{
            throw new MongoDBException("It wasn´t posible to load the file");
        }
    }

    public String downloadFile(String farmId) throws Exception {
        GridFSFile gridFSFile = getGridFSDatabase().find(Filters.eq("metadata.FarmId", farmId)).first();
        try {
            FileOutputStream streamToDownloadTo = new FileOutputStream( gridFSFile.getFilename());
            getGridFSDatabase().downloadToStream(gridFSFile.getFilename(), streamToDownloadTo);
            streamToDownloadTo.close();

            File f = new File( gridFSFile.getFilename());
            String resultBase64Encoded = "";
            if (f.exists()) {
                resultBase64Encoded = Base64.getEncoder().encodeToString(org.apache.commons.io.FileUtils.readFileToByteArray(f));
                f.delete();
            } else {
                System.out.println("no existe el archivo");
            }
            return resultBase64Encoded;

        } catch (IOException e) {
            throw new MongoDBException("It wasn´t posible to load the file");
        }
    }
    
    public List<File> findFilesByPrefixInName(String prefix){
        List<File> files = new ArrayList<>();
        getGridFSDatabase().find(Filters.regex("filename", prefix)).forEach(new Block<GridFSFile>() {
            @Override
            public void apply(final GridFSFile gridFSFile) {
                FileOutputStream streamToDownloadTo;
                try {
                    streamToDownloadTo = new FileOutputStream(gridFSFile.getFilename());
                    getGridFSDatabase().downloadToStream(gridFSFile.getFilename(), streamToDownloadTo);
                    streamToDownloadTo.close();

                    File f = new File(gridFSFile.getFilename());
                    files.add(f);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(MongoDbImage.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(MongoDbImage.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
        return files;
    }

    public List<Image> downloadMapsFile(String landlotId, long date) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String reportDate = dateFormat.format(date);
        GridFSFindIterable gridFSFiles = getGridFSDatabase().find(Filters.and(Filters.eq("metadata.landlotId", landlotId),Filters.eq("metadata.date", reportDate)));
        List<Image> data = new ArrayList<>();
        for (GridFSFile gridFSFile: gridFSFiles){
            try {
                Image image= new Image();
                FileOutputStream streamToDownloadTo = new FileOutputStream( gridFSFile.getFilename());
                getGridFSDatabase().downloadToStream(gridFSFile.getFilename(), streamToDownloadTo);
                streamToDownloadTo.close();
                File f = new File( gridFSFile.getFilename());
                String resultBase64Encoded = "";
                if (f.exists()) {
                    resultBase64Encoded = Base64.getEncoder().encodeToString(org.apache.commons.io.FileUtils.readFileToByteArray(f));

                } else {
                    System.out.println("no existe el archivo");
                }
                image.setCoordinates((List<Double>) gridFSFile.getMetadata().get("coordinates"));
                image.setName(gridFSFile.getFilename());
                image.setSrc(resultBase64Encoded);
                System.out.println(image.toString());
                data.add(image);
                f.delete();
            } catch (IOException e) {
                e.printStackTrace();
                throw new MongoDBException("It wasn´t posible to load the file");
            }

        }
        return data;
    }
    
    private Image setImageValues(Metadata metadata, Image img){
        for (com.drew.metadata.Directory directory : metadata.getDirectories()) {
            for (Tag tag : directory.getTags()) {
                if(tag.getTagName().equals("GPS Latitude")){
                    img.setLatitude(tag.getDescription());
                }else if(tag.getTagName().equals("GPS Longitude")){
                    img.setLongitude(tag.getDescription());
                }else if(tag.getTagName().equals("File Size")){
                    img.setFileSize(tag.getDescription());
                }else if(tag.getTagName().equals("File Name")){
                    img.setName(tag.getDescription());
                }else if(tag.getTagName().equals("Date/Time")){
                    img.setModifiedDate(tag.getDescription());
                } 
            }
        }
        return img;
    }

    public static Double toDecimalResult(String degreeCoordinate) {
        try {
            String[] degree = degreeCoordinate.replaceAll("[^0-9.\\s-]", "").split(" ");
            Double decimalConv = toDecimal(degree);
            return decimalConv;
        } catch(Exception ex) {
            System.out.println(String.format("Error en el formato de las coordenadas:"));
            return null;
        }
    }

    public static Double toDecimal(String latOrLng) {
        try {
            String[] latlng = latOrLng.replaceAll("[^0-9.\\s-]", "").split(" ");
            Double dlatlng = toDecimal(latlng);
            return dlatlng;
        } catch(Exception ex) {
            System.out.println(String.format("Error en el formato de las coordenadas: %s ", new Object[]{latOrLng}));
            return null;
        }
    }

    public static Double toDecimal(String[] coord) {
        double d = Double.parseDouble(coord[0]);
        double m = Double.parseDouble(coord[1]);
        double s = Double.parseDouble(coord[2]);
        double signo = 1;
        if (coord[0].startsWith("-"))
            signo = -1;
        return signo * (Math.abs(d) + (m / 60.0) + (s / 3600.0));
    }

    private boolean findFilesStores(String fileName) {
        List<String> fileNames = new ArrayList<>();
        getGridFSDatabase().find().forEach(
                new Block<GridFSFile>() {
                    @Override
                    public void apply(final GridFSFile gridFSFile) {
                        fileNames.add(gridFSFile.getFilename());
                    }
                });
        return fileNames.contains(fileName);
    }

    public List<Long> datesOfFilesLandlot(long startDate,long finishDate) {
        List<Long> dates = new ArrayList<>();
        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd");
        getGridFSDatabase().find().forEach(
                new Block<GridFSFile>() {
                    @Override
                    public void apply(final GridFSFile gridFSFile) {
                        String dateFile = gridFSFile.getMetadata().getString("date");
                        try {
                            if(dateFile != null){
                                Date d = f.parse(dateFile);
                                long milliseconds = d.getTime();
                                if(milliseconds >= startDate && milliseconds <= finishDate && !dates.contains(milliseconds)){
                                    dates.add(milliseconds);
                                }
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
        return dates;
    }
}
