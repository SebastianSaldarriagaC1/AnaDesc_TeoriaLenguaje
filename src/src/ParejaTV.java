package src;
        
public class ParejaTV {
    private final String tipo;
    private final String valor;

    public ParejaTV(String tipo, String valor) {
        this.tipo = tipo;
        this.valor = valor;
    }

    public String Tipo() {
        return tipo;
    }

    public String Valor() {
        return valor;
    }

    @Override
    public String toString() {
        return (valor + " es un " + tipo);
    }
}
