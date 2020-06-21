package codered.codered;

import android.graphics.drawable.Drawable;

import com.google.firebase.database.ServerValue;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

class Request implements Comparable<Request>{

    private String id, message, code;
    private int product, status;
    private Object timestamp;
    private Object meetTime;
    private double lat, lng;

    // arrays to translate saved index into a string
    public static String[] products = {"Tampon", "Pad", "Painkiller"};
    public static String[] states = {"Pending", "Answered", "Cancelled"};
    //Storing the icons of each product
    public static int[] productIcons = {R.drawable.tampon, R.drawable.pad, R.drawable.pill};

    public Request(){}

    public Request(String requestId, int p, String m, String c, double la, double ln, Object t){
        this.id = requestId;
        this.product = p;
        this.message = m;
        this.code = c;
        this.lat = la;
        this.lng = ln;
        this.meetTime = t;
        // sets status to pending when created
        this.status = 0;
        // records time of request
        this.timestamp = ServerValue.TIMESTAMP;
    }

    public static int secAgo(long time){
        // finds time difference in minutes
        Long diff = new Date().getTime() - time;
        int secDiff = (int) (diff / 1000);
        return secDiff;
    }

    //Generating the random code word
    public static String generateCode(){
        String[] adj = {"crispy", "moist", "happy", "crunchy", "red", "witty", "furry"};
        String[] noun = {"squirrel", "monkey", "smile", "potato chip", "banana", "ice cream"};
        int rand1 = (int)Math.floor(Math.random()*(adj.length));
        int rand2 = (int)Math.floor(Math.random()*(noun.length));
        return adj[rand1] + " " + noun[rand2];
    }


    //Storing and obtaining the contents of the Firebase into objects
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

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public Object getMeetTime() {
        return meetTime;
    }

    public void setMeetTime(Object meetTime) {
        this.meetTime = meetTime;
    }

    // Determining which requests are of higher priority based on their distance, and if distances are equal, how long ago it was sent
    @Override
    public int compareTo(Request o) {
        // returns 0 (same), 1 (puts o higher), -1 (puts this higher)
        int d1 = RequestFragment.findDistance(RequestFragment.location, lat, lng);
        int d2 = RequestFragment.findDistance(RequestFragment.location, o.lat, o.lng);
        if (d1>d2){
            return 1;
        } else if (d1==d2){
            if ((long)getTimestamp() < (long)o.getTimestamp()){
                return 1;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }
}
