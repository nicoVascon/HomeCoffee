package ipleiria.pdm.homecoffee.ui.Devices;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DevicesViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public DevicesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Device fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
