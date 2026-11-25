package model;

import java.util.ArrayList;
import java.util.List;

public class Comanda {

    private int id;
    private int clientId;
    private double total;
    private boolean descompteAplicat;
    private List<LiniaComanda> linies = new ArrayList<>();

    public Comanda() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getClientId() { return clientId; }
    public void setClientId(int clientId) { this.clientId = clientId; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public boolean isDescompteAplicat() { return descompteAplicat; }
    public void setDescompteAplicat(boolean descompteAplicat) { this.descompteAplicat = descompteAplicat; }
    public List<LiniaComanda> getLinies() { return linies; }
    public void setLinies(List<LiniaComanda> linies) { this.linies = linies; }
}
