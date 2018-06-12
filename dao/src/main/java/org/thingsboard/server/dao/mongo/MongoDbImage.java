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
import static sun.security.krb5.Confounder.bytes;
//import org.apache.commons.io;

/**
 *
 * @author carlos
 */
public class MongoDbImage extends MongoConnection {

    public MongoDbImage(){}

    public void uploadFile(File file, HashMap<String,String> metadata) throws FileNotFoundException {
        // Get the input stream
        InputStream streamToUploadFrom = new FileInputStream(file);
        Document doc = new Document();
        metadata.forEach((k,v) -> doc.append(k,v));

        // Create some custom options
        GridFSUploadOptions options = new GridFSUploadOptions()
                .chunkSizeBytes(1048576*2)
                .metadata(doc);
        ObjectId fileId = getGridFSDatabase().uploadFromStream(file.getName(), streamToUploadFrom, options);
        System.out.println("Archivo ya subido!!");
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

    /*public static void main(String[] args) throws Throwable {

        MongoClient mongoClient = new MongoClient();

        myDatabase = mongoClient.getDatabase("prueba");

        gridFSBucket = GridFSBuckets.create(myDatabase);

        File file = new File("//home//carlos//Downloads//Carlos//DJI_0194.JPG");

        if (file.exists()) {

            System.out.println("El archivo si existe!!");

            uploadFile(file);

            findFilesStores();

            downloadFile(file.getName());

            //restoreFile(file.getName());
        } else {
            System.out.println("El archivo no existe!!");
        }

    }*/



    /*public void downloadFile(String nameFile) throws FileNotFoundException, IOException {
        FileOutputStream streamToDownloadTo = new FileOutputStream("/home/carlos/Documents/" + nameFile);
        GridFSDownloadByNameOptions downloadOptions = new GridFSDownloadByNameOptions().revision(0);
        getGridFSDatabase().downloadToStreamByName(nameFile, streamToDownloadTo, downloadOptions);
        streamToDownloadTo.close();
    }*/

    /*public void restoreFile(String nameFile) throws IOException {
        GridFSDownloadStream downloadStream = getGridFSDatabase().openDownloadStreamByName(nameFile);
        int fileLength = (int) downloadStream.getGridFSFile().getLength();
        byte[] bytesToWriteTo = new byte[fileLength];
        downloadStream.read(bytesToWriteTo);
        downloadStream.close();
    }*/

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
            System.out.println(streamToDownloadTo.toString());

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





    public Map<Image, String> downloadMapsFile(String parcelId, String date) throws Exception {
        GridFSFindIterable gridFSFiles = getGridFSDatabase().find(Filters.and(Filters.eq("metadata.ParcelId", parcelId),Filters.eq("metadata.Date", date)));
        Map<Image, String> data = new HashMap<>();
        for (GridFSFile gridFSFile: gridFSFiles){
            try {
                Image image= new Image();
                FileOutputStream streamToDownloadTo = new FileOutputStream( gridFSFile.getFilename());
                getGridFSDatabase().downloadToStream(gridFSFile.getFilename(), streamToDownloadTo);
                streamToDownloadTo.close();
                System.out.println(streamToDownloadTo.toString());



                File f = new File( gridFSFile.getFilename());
                String resultBase64Encoded = "";
                if (f.exists()) {
                    resultBase64Encoded = Base64.getEncoder().encodeToString(org.apache.commons.io.FileUtils.readFileToByteArray(f));
                    f.delete();
                } else {
                    System.out.println("no existe el archivo");
                }

                Metadata metadata = JpegMetadataReader.readMetadata(f);

                image=setImageValues(metadata,image);

                data.put(image,resultBase64Encoded);

            } catch (IOException e) {
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
                    System.out.println("    "+tag.getDescription());
                }else if(tag.getTagName().equals("GPS Longitude")){
                    img.setLongitude(tag.getDescription());
                    System.out.println("    "+tag.getDescription());
                }else if(tag.getTagName().equals("File Modified Date")){
                    img.setModifiedDate(tag.getDescription());
                    System.out.println("    "+tag.getDescription());
                }else if(tag.getTagName().equals("File Size")){
                    img.setFileSize(tag.getDescription());
                    System.out.println("    "+tag.getDescription());
                }else if(tag.getTagName().equals("File Name")){
                    img.setName(tag.getDescription());
                    System.out.println("    "+tag.getDescription());
                }
            }
        }
        return img;
    }
}
