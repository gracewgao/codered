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
        safetyCard.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), SafetyActivity.class);
                getActivity().startActivity(i);
            }
        });
        productCard = v.findViewById(R.id.product_card);
        productCard.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), SafetyActivity.class);
                getActivity().startActivity(i);
            }
        });
        resourceCard = v.findViewById(R.id.resource_card);
        resourceCard.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ResourceActivity.class);
                getActivity().startActivity(i);
            }
        });

        return v;

    }
}
