package org.thingsboard.server.common.data;

public class SparkDevice {

    private String id;
    private String idLandlot;
    private String topic;
    private String token;

    public SparkDevice(){}

    public SparkDevice(String id,String idLandlot,String topic,String token){
        this.setId(id);
        this.setIdLandlot(idLandlot);
        this.setTopic(topic);
        this.setToken(token);
    }

    public String getToken() {return token;}

    public void setToken(String token){
        this.token=token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdLandlot() {
        return idLandlot;
    }

    public void setIdLandlot(String idLandlot) {
        this.idLandlot = idLandlot;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
