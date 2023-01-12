package ipleiria.pdm.homecoffee.components;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import ipleiria.pdm.homecoffee.R;

/**
 * Classe que cria um diálogo de carregamento customizável.
 * Ele contém métodos para iniciar, fechar e personalizar o diálogo com textos principais e secundários.
 */
public class LoadingDialog {

    /**
     * Atividade onde o diálogo será mostrado
     */
    private Activity activity;
    /**
     * Diálogo de carregamento
     */
    private AlertDialog alertDialog;
    /**
     * TextView que mostra o texto principal do diálogo
     */
    private TextView textViewLoadingDialogMainText;
    /**
     * TextView que mostra o texto secundário do diálogo
     */
    private TextView textViewLoadingDialogSubText;

    /**
     * Construtor da classe.
     * @param myactivity Atividade onde o diálogo será mostrado
     */
    public LoadingDialog(Activity myactivity)
    {
        activity= myactivity;
    }

    /**
     * Método para iniciar o diálogo de carregamento sem possibilidade de cancelamento.
     */
    public void startLoadingDialog(){
        startLoadingDialog(false);
    }

    /**
     * Método para iniciar o diálogo de carregamento.
     * @param cancelable flag para determinar se o diálogo pode ser cancelado ou não.
     */
    public void startLoadingDialog(boolean cancelable)
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(activity);
                LayoutInflater inflater = activity.getLayoutInflater();
                View view = inflater.inflate(R.layout.custom_dialog_loading,null);
                textViewLoadingDialogMainText = view.findViewById(R.id.textViewLoadingDialogMainText);
                textViewLoadingDialogSubText = view.findViewById(R.id.textViewLoadingDialogSubText);
                textViewLoadingDialogSubText.setVisibility(View.GONE);
                builder.setView(view);
                builder.setCancelable(cancelable);

                alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    /**
     * Método para configurar o texto secundário do diálogo.
     * @param text texto a ser mostrado no diálogo
     */
    public void setSubText(String text){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(text == null || text.trim().isEmpty()){
                    textViewLoadingDialogSubText.setVisibility(View.GONE);
                    return;
                }
                textViewLoadingDialogSubText.setText(text);
                textViewLoadingDialogSubText.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Método para definir o texto principal do diálogo de carregamento.
     * @param text Texto a ser exibido no diálogo de carregamento. Se for nulo ou vazio, a view será escondida
     */
    public void setMainText(String text){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(text == null || text.trim().isEmpty()){
                    textViewLoadingDialogMainText.setVisibility(View.GONE);
                    return;
                }
                textViewLoadingDialogMainText.setText(text);
                textViewLoadingDialogMainText.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Método para fechar o diálogo de carregamento.
     */
    public void dismisDialog()
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog.dismiss();
            }
        });
    }
}