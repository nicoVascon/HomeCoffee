package ipleiria.pdm.homecoffee.components.resources;

import com.jjoe64.graphview.series.DataPoint;

import java.util.Date;
/**
 * Implementação de DataPoint com capacidade de ser comparado.
 */
public class DataPointImpl extends DataPoint implements Comparable<DataPointImpl> {
    /**
     * Construtor para x e y como doubles
     * @param x double, representando o valor de X.
     * @param y double, representando o valor de Y.
     */
    public DataPointImpl(double x, double y) {
        super(x, y);
    }
    /**
     * Construtor para x e y como Date e double
     * @param x Date, representando o valor de X.
     * @param y double, representando o valor de Y.
     */
    public DataPointImpl(Date x, double y) {
        super(x, y);
    }
    /**
     * Compara esse objeto DataPointImpl com o objeto especificado.
     * @param o Outro DataPointImpl para comparar.
     * @return Um valor negativo, zero ou positivo, dependendo se o valor de X deste objeto é menor, igual ou maior que o valor de X do objeto especificado.
     */
    @Override
    public int compareTo(DataPointImpl o) {
        if(o.getX() - this.getX() > 0){
            return -1;
        }
        return 1;
    }
}
