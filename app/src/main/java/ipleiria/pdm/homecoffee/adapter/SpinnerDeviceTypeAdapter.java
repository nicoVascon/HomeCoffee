package ipleiria.pdm.homecoffee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ipleiria.pdm.homecoffee.Enums.DeviceType;
import ipleiria.pdm.homecoffee.R;
/**
 * Classe adapter para o spinner de tipos de dispositivos. Cria a exibição personalizada para cada tipo de dispositivo.
 */
public class SpinnerDeviceTypeAdapter extends ArrayAdapter<DeviceType> {

    /**
     * Construtor.
     * @param context O contexto atual.
     * @param resource O recurso de layout do spinner.
     * @param deviceTypes Array de tipos de dispositivos.
     */
    public SpinnerDeviceTypeAdapter(Context context, int resource, DeviceType[] deviceTypes){
        super(context, resource, deviceTypes);
    }
    /**
     * Cria a exibição personalizada para um tipo de dispositivo.
     * @param position Posição do tipo de dispositivo.
     * @param convertView A visão a ser convertida.
     * @param parent O grupo pai que contém o spinner.
     * @return A exibição personalizada para o tipo de dispositivo na posição especificada.
     */
    public View getCustomView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View layout = inflater.inflate(R.layout.item_devicetype_layout, parent, false);
        TextView txtDevName_devType = layout.findViewById(R.id.textViewDeviceName_spinner);
        ImageView imgPhoto_devType = layout.findViewById(R.id.imageViewDeviceTypePhoto_spinner);

        DeviceType deviceType = DeviceType.values()[position];
        switch (deviceType){
            case DIGITAL:
                imgPhoto_devType.setImageResource(R.drawable.digital_icon);
                txtDevName_devType.setText(getContext().getResources().getString(R.string.deviceTypeName_Digital));
                break;
            case ANALOG:
                imgPhoto_devType.setImageResource(R.drawable.analog_icon);
                txtDevName_devType.setText(getContext().getResources().getString(R.string.deviceTypeName_Analog));
                break;
            case PRESENCE:
                imgPhoto_devType.setImageResource(R.drawable.presence_icon);
                txtDevName_devType.setText(getContext().getResources().getString(R.string.deviceTypeName_Presence));
                break;
            case HUMIDITY:
                imgPhoto_devType.setImageResource(R.drawable.humiditysensor);
                txtDevName_devType.setText(getContext().getResources().getString(R.string.deviceTypeName_Humidity));
                break;
            case TEMPERATURE:
                imgPhoto_devType.setImageResource(R.drawable.temperaturesensor);
                txtDevName_devType.setText(getContext().getResources().getString(R.string.deviceTypeName_Temperature));
                break;
            case LUMINOSITY:
                imgPhoto_devType.setImageResource(R.drawable.lightsensor);
                txtDevName_devType.setText(getContext().getResources().getString(R.string.deviceTypeName_Light));
                break;
            case ACCELERATION:
                imgPhoto_devType.setImageResource(R.drawable.accelerationsensor);
                txtDevName_devType.setText(getContext().getResources().getString(R.string.deviceTypeName_Acceleration));
                break;
            case PRESSURE:
                imgPhoto_devType.setImageResource(R.drawable.preassuresensor);
                txtDevName_devType.setText(getContext().getResources().getString(R.string.deviceTypeName_Pressure));
                break;
        }
        return layout;
    }

    // It gets a View that displays in the drop down popup the data at the specified position
    /**
     * Este método é chamado para obter a exibição personalizada para um item no dropdown do spinner.
     * @param position Posição do item.
     * @param convertView A visão a ser convertida.
     * @param parent O grupo pai que contém o spinner.
     * @return A exibição personalizada para o item na posição especificada.
     */
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    // It gets a View that displays the data at the specified position
    /**
     * Este método é chamado para obter a exibição personalizada para um item no spinner.
     * @param position Posição do item.
     * @param convertView A visão a ser convertida.
     * @param parent O grupo pai que contém o spinner.
     * @return A exibição personalizada para o item na posição especificada.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }
}
