package voto.ado.sainthannaz.votoseguro.aplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import voto.ado.sainthannaz.votoseguro.R;

/**
 * Created by Admin on 04-06-2015.
 */
public class ContentFragment_GeneralMain extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_fragment_general_main,container,false);
        return v;
    }
}
