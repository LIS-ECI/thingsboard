package org.thingsboard.server.common.data;

public class SparkDevice {

    private String id;
    private String idParcel;
    private String topic;

    public SparkDevice(){}

    public SparkDevice(String id,String idParcel,String topic){
        this.setId(id);
        this.setIdParcel(idParcel);
        this.setTopic(topic);
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdParcel() {
        return idParcel;
    }

    public void setIdParcel(String idParcel) {
        this.idParcel = idParcel;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
