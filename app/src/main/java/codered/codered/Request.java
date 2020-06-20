package codered.codered;

class Request {

    private String id, message;
    private int product, status;

    public Request(){}

    public Request(String requestId, int p, String m){
        this.id = requestId;
        this.product = p;
        this.message = m;
        // sets status to pending when created
        this.status = 0;
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
}
