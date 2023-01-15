package ipleiria.pdm.homecoffee.components.resources;

import com.jjoe64.graphview.series.DataPointInterface;

import java.io.Serializable;
import java.util.Date;
/**
 * Implementação de DataPoint com capacidade de ser comparado.
 */
public class DataPointImpl implements DataPointInterface, Serializable, Comparable<DataPointImpl> {
    private static final long serialVersionUID=1428263322645L;

    /**
     * valor que representará o x no gráfico
     */
    private double x;
    /**
     * valor que representará o y no gráfico
     */
    private double y;

    public DataPointImpl(){}
    /**
     * Construtor para x e y como doubles
     * @param x double, representando o valor de X.
     * @param y double, representando o valor de Y.
     */
    public DataPointImpl(double x, double y) {
        this.x=x;
        this.y=y;
    }
    /**
     * Construtor para x e y como Date e double
     * @param x Date, representando o valor de X.
     * @param y double, representando o valor de Y.
     */
    public DataPointImpl(Date x, double y) {
        this.x = x.getTime();
        this.y = y;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "["+x+"/"+y+"]";
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
