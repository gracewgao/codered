package codered.codered;

import com.google.firebase.database.ServerValue;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

class Request {

    private String id, message, code;
    private int product, status;
    private Object timestamp;

    public Request(){}

    public Request(String requestId, int p, String m, String c){
        this.id = requestId;
        this.product = p;
        this.message = m;
        this.code = c;
        // sets status to pending when created
        this.status = 0;
        // records time of request
        this.timestamp = ServerValue.TIMESTAMP;
    }

    public static String convertTime(long time){
        String message = "";
        Format format;
        Date recordedTime = new Date(time);
        Long currentTime = new Date().getTime();
        Date midnight = new Date(currentTime - (time % (24 * 60 * 60 * 1000)));
        Date thisWeek = new Date(currentTime - (time % (30 * 24 * 60 * 60 * 1000)));

        //Displays the modifiedTime if timestamp is from the current day
        if (recordedTime.after(midnight)) {
            message += "Today at ";
            format = new SimpleDateFormat("h:mm a");
            message += format.format(recordedTime);
        } else if (recordedTime.after(thisWeek)){
            format = new SimpleDateFormat("MMMM d");
            message += format.format(recordedTime);
        } else{
            message ="A long time ago";
        }
        return message;
    }

    public static String generateCode(){
        String[] adj = {"crispy", "moist", "happy", "crunchy", "red", "witty", "furry"};
        String[] noun = {"squirrel", "monkey", "smile", "potato chip", "banana", "ice cream"};
        int rand1 = (int)Math.floor(Math.random()*(adj.length));
        int rand2 = (int)Math.floor(Math.random()*(noun.length));
        return adj[rand1] + " " + noun[rand2];
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getProduct() {
        return product;
    }

    public void setProduct(int product) {
        this.product = product;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
