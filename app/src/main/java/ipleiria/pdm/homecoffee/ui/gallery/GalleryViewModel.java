package ipleiria.pdm.homecoffee.ui.gallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Classe responsável pela lógica de negócio da fragment Gallery.
 * Utiliza o conceito de LiveData para notificar a fragment Gallery da mudança de estado do texto.
 */
public class GalleryViewModel extends ViewModel {

    /**
     * Atributo que armazena o texto da fragment Gallery.
     * Utiliza o conceito de MutableLiveData para permitir a mudança de valor do atributo.
     */
    private final MutableLiveData<String> mText;

    /**
     * Construtor padrão da classe.
     * Inicializa o atributo mText com o valor "This is gallery fragment"
     */
    public GalleryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    /**
     * Método que retorna o valor do atributo mText.
     * Utiliza o conceito de LiveData para garantir que a fragment Gallery será notificada caso ocorra mudança no valor do atributo.
     * @return o valor do atributo mText
     */
    public LiveData<String> getText() {
        return mText;
    }
}