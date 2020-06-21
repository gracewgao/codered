package codered.codered;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LearnFragment extends Fragment {

    private CardView safetyCard;
    private CardView productCard;
    private CardView resourceCard;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_learn, null);
        safetyCard = v.findViewById(R.id.safety_card);
        productCard = v.findViewById(R.id.product_card);
        resourceCard = v.findViewById(R.id.resource_card);

        // If the safety card is selected, then the SafetyActivity (How to use codeRED safely page) will open, launching the appropriate XML file
        safetyCard.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), SafetyActivity.class);
                getActivity().startActivity(i);

            }
        });

        // If the menstrual product card is selected, then the ProductsActivity (How to use codeRED safely page) will open, launching the appropriate XML file
        productCard.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ProductsActivity.class);
                getActivity().startActivity(i);
            }
        });

        // If the resource card is selected, then the ResourceActivity (How to use codeRED safely page) will open, launching the appropriate XML file
        resourceCard.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ResourceActivity.class);
                getActivity().startActivity(i);
            }
        });
        //returning the view window and its respective fragments e.g. navigation
        return v;


    }
}
