package aptech.proj_NN_group2.model.entity;

import java.util.Objects;

public class Unit {
    private int unit_id;
    private String unit_name;

    public Unit() {
    }

    public Unit(int unit_id, String unit_name) {
        this.unit_id = unit_id;
        this.unit_name = unit_name;
    }

    public int getUnit_id() {
        return unit_id;
    }

    public void setUnit_id(int unit_id) {
        this.unit_id = unit_id;
    }

    public String getUnit_name() {
        return unit_name;
    }

    public void setUnit_name(String unit_name) {
        this.unit_name = unit_name;
    }

    @Override
    public String toString() {
        return unit_name == null ? "" : unit_name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Unit)) return false;
        Unit unit = (Unit) o;
        return unit_id == unit.unit_id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(unit_id);
    }
}