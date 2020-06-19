package codered.codered;

class Request {

    private String id;
    private String message;

    public Request(){}

    public Request(String requestId, String m){
        this.id = requestId;
        this.message = m;
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
}
