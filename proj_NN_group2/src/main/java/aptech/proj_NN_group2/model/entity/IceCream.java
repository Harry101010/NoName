package aptech.proj_NN_group2.model.entity;

public class IceCream {
    private int ice_cream_id;
    private String ice_cream_name;
    private boolean is_active;

    public IceCream() {}

    public IceCream(int ice_cream_id, String ice_cream_name, boolean is_active) {
        this.ice_cream_id = ice_cream_id;
        this.ice_cream_name = ice_cream_name;
        this.is_active = is_active;
    }

    public int getIce_cream_id() { return ice_cream_id; }
    public void setIce_cream_id(int ice_cream_id) { this.ice_cream_id = ice_cream_id; }

    public String getIce_cream_name() { return ice_cream_name; }
    public void setIce_cream_name(String ice_cream_name) { this.ice_cream_name = ice_cream_name; }

    public boolean isIs_active() { return is_active; }
    public void setIs_active(boolean is_active) { this.is_active = is_active; }

    @Override
    public String toString() { return ice_cream_name; }
}
