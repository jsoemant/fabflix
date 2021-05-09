import java.util.HashMap;

/**
 * This User class only has the username field in this example.
 * You can add more attributes such as the user's shopping cart items.
 */
public class User {
    private String username;
    private String id;
    private HashMap<String, HashMap<String, String>> cart;
    private String role;

    public User(String username) {
        this.username = username;
        this.role = "employee";
    }

    public User(String username, String id) {
        this.username = username;
        this.id = id;
        //Customer cart contains {id:{title:"", quantity:""}}
        this.cart = new HashMap<String, HashMap<String, String>>();
        this.role = "customer";
    }

    public String getRole() { return this.role; }

    public HashMap<String, HashMap<String, String>> getCart() {
        return this.cart;
    }

    public synchronized void editCart(String id, String title, String action) {
        if (action.equals("subtract")) {
            this.subtractFromCart(id);
        } else if (action.equals("add")) {
            this.addToCart(id, title);
        } else if (action.equals("remove")){
            this.removeFromCart(id);
        } else {
            this.deleteCart();
        }
    }

    public synchronized void subtractFromCart(String id) {
        HashMap<String, String> item = this.cart.get(id);

        int currentQty = Integer.parseInt(item.get("qty"));
        int newQty = currentQty - 1;

        if (newQty == 0) {
            this.removeFromCart(id);
        } else {
            item.put("qty", Integer.toString(newQty));
        }
    }

    public synchronized void addToCart(String id, String title) {
        HashMap<String, String> item = this.cart.get(id);

        if (this.cart.containsKey(id)) {
            int currentQty = Integer.parseInt(item.get("qty"));
            int newQty = currentQty + 1;
            item.put("qty", Integer.toString(newQty));
        } else {
            HashMap<String, String> entry = new HashMap<String, String>();
            entry.put("title", title);
            entry.put("qty", "1");
            this.cart.put(id, entry);
        }
    }

    public synchronized void removeFromCart(String id) { this.cart.remove(id); }

    public synchronized void deleteCart() {
        this.cart.clear();
    }

    public String getId() { return this.id; }

}
