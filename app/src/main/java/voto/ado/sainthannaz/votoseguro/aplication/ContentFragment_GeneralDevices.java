package voto.ado.sainthannaz.votoseguro.aplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rey.material.widget.Button;

import voto.ado.sainthannaz.votoseguro.R;

/**
 * Created by Admin on 04-06-2015.
 */
public class ContentFragment_GeneralDevices extends Fragment {
    View view;
    Button btnAddDevice;
    Button btnDelDevice;
    Button btnModDevice;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_fragment_general_devices,container,false);

        btnAddDevice = (Button) view.findViewById(R.id.btnAddDevice);
        btnAddDevice.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(),"Add user",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), RegisterDeviceActivity.class);
                startActivity(intent);
                //getActivity().finish();
            }
        });

        return view;
    }
}
