package ipleiria.pdm.homecoffee.components;

import android.content.Context;
import android.util.AttributeSet;

import com.jjoe64.graphview.GraphView;

/**
 * Classe estendida da classe GraphView, oferece uma implementação personalizada.
 */
public class GraphView_Custom extends GraphView {

    /**
     * Construtor padrão para a classe GraphView_Custom
     * @param context contexto da aplicação
     */
    public GraphView_Custom(Context context) {
        super(context);
    }

    /**
     * Construtor para a classe GraphView_Custom, com o conjunto de atributos personalizados.
     * @param context contexto da aplicação
     * @param attrs conjunto de atributos personalizados
     */
    public GraphView_Custom(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Construtor para a classe GraphView_Custom, com o conjunto de atributos personalizados e um estilo.
     * @param context contexto da aplicação
     * @param attrs conjunto de atributos personalizados
     * @param defStyle estilo
     */
    public GraphView_Custom(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Método para inicializar a classe GraphView_Custom
     */
    public void init(){
        super.init();
    }
}
