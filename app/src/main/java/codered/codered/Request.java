package codered.codered;

class Request {

    private String id, message;
    private int product;

    public Request(){}

    public Request(String requestId, int p, String m){
        this.id = requestId;
        this.product = p;
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

    public int getProduct() {
        return product;
    }

    public void setProduct(int product) {
        this.product = product;
    }
}
