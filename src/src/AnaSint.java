package src;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

public class AnaSint {

    private String[] term;
    private String[] noTerm;

    private String[][] anaSinTable;

    private Stack<String> pila;

    public AnaSint() {
        pila = new Stack<>();
    }

    public void prepararAnalizador(JTable table) {
        DefaultTableModel dtm = (DefaultTableModel) table.getModel();

        reconocerTerminales(dtm);
        reconocerNoTerminales(dtm);
        reconocerTablaAnaSint(dtm);
    }

    public void reconocerTerminales(DefaultTableModel table) {
        int col = table.getColumnCount();
        this.term = new String[col - 1];
        for (int i = 0; i < col - 1; i++) {
            this.term[i] = table.getColumnName(i + 1);
        }
    }

    public void reconocerNoTerminales(DefaultTableModel table) {
        int row = table.getRowCount();
        this.noTerm = new String[row];
        for (int i = 0; i < row; i++) {
            this.noTerm[i] = table.getValueAt(i, 0).toString();
        }
    }

    public void reconocerTablaAnaSint(DefaultTableModel table) {
        int row = table.getRowCount(), col = table.getColumnCount();
        this.anaSinTable = new String[table.getRowCount()][table.getColumnCount() - 1];

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col - 1; j++) {
                if (table.getValueAt(i, j + 1) == null) {
                    this.anaSinTable[i][j] = "";
                } else {
                    this.anaSinTable[i][j] = table.getValueAt(i, j + 1).toString();
                }
            }
        }
    }

    public ArrayList<ParejaTV> separarPalabra(String text) {
        ArrayList<ParejaTV> array = new ArrayList<>();
        String aux = "";
        for (int i = 0; i <= text.length(); i++) {
            Character c = safeCharAt(text, i);

            if (Character.isDigit(c)) {
                while (Character.isDigit(c) && i < text.length()) {
                    aux += c;
                    i++;
                    c = safeCharAt(text, i);
                }
                i--;
                array.add(new ParejaTV("num", aux));
                aux = "";
            } else if (Character.isLetter(c)) {
                while (Character.isLetter(c) && i < text.length()) {
                    aux += c;
                    i++;
                    c = safeCharAt(text, i);
                }
                i--;
                array.add(new ParejaTV("id", aux));
                aux = "";
            } else {
                if (Arrays.asList(term).contains(c.toString())) {
                    array.add(new ParejaTV(c.toString(), c.toString()));
                } else if (!c.toString().equals(" ")) {
                    array.add(new ParejaTV("Error", c.toString()));
                    return array;
                }
            }
        }
        return array;
    }

    public String[] reconocerDescendente(ArrayList<ParejaTV> parsedText) {
        pila = new Stack<>();
        pila.push("$");
        pila.push(noTerm[0]);

        String[] ret = {"", ""};

        int i = 0;
        int j = 1;
        ParejaTV ptv;
        String tope;
        while (i < parsedText.size()) {
            ptv = parsedText.get(i);
            tope = pila.peek();

            ret[0] += j + ". " + pila.toString() + "\n";
            ret[1] += j + ". " + cadenaQueFalta(parsedText, i) + "\n";

            if (ptv.Tipo().equals(tope)) {
                if (tope.equals("$")) {
                    ret[0] += "Palabra aceptada";
                    ret[1] += "Palabra aceptada";
                    return ret;
                } else {
                    pila.pop();
                    i++;
                }
            } else if (tope.equals("λ")) {
                pila.pop();
            } else {
                if (Arrays.asList(noTerm).contains(tope)) {
                    int indexTerm = findIndex(term, ptv.Tipo());
                    int indexNoTerm = findIndex(noTerm, tope);

                    if (indexTerm == -1 || indexNoTerm == -1) {
                        ret[0] += "Palabra NO aceptada";
                        ret[1] += "Palabra NO aceptada";
                        return ret;
                    } else {
                        String next = anaSinTable[indexNoTerm][indexTerm];

                        if (next.equals("")) {
                            ret[0] += "Palabra NO aceptada";
                            ret[1] += "Palabra NO aceptada";
                            return ret;
                        } else {
                            Stack<String> pilaAux = new Stack<>();
                            String[] splited = next.split("\\s+");

                            for (String s : splited) {
                                pilaAux.push(s);
                            }

                            pila.pop();

                            while (!pilaAux.isEmpty()) {
                                pila.push(pilaAux.pop());
                            }
                        }
                    }
                } else if (Arrays.asList(term).contains(tope)) {
                    ret[0] += "Palabra NO aceptada";
                    ret[1] += "Palabra NO aceptada";
                    return ret;
                }
            }
            j++;
        }
        return ret;
    }

    public Character safeCharAt(String str, int pos) {
        try {
            return str.charAt(pos);
        } catch (IndexOutOfBoundsException e) {
            return '$';
        }
    }

    public int findIndex(String[] strings, String str) {
        for (int i = 0; i < strings.length; i++) {
            if (strings[i].equals(str)) {
                return i;
            }
        }
        return -1;
    }

    public String cadenaQueFalta(ArrayList<ParejaTV> parsedText, int i) {
        String str = "";
        while (i < parsedText.size()) {
            str += parsedText.get(i).Valor();
            i++;
        }
        return str;
    }
}
