package org.noise_planet.noisecapture;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Dimension;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Clase que permite crear la vista de TABS.
 * Se ha modificado el código del resumen final para que los botones en lugar de ser ToggleButton, sean botones normales.
 * No me interesa que sean checkeables porque al final puede ser que un mismo ruido aparezca en diferentes momentos.
 * Además, he creado una interfaz con 3 funciones
 * La primera (onButtonClicked) que llamaremos con cada click del botón y a la que pasaremos el nombre de la etiqueta para guardarlo en el fichero.
 * La segunda (onFinishedRecord) que llamaremos cuando pulsemos el botón para finalizar la grabación.
 * La tercera (onPausedRecord) que llamaremos cuando pulsemos el botón para pausar la grabación.
 * La cuarta (addingValue) para el valor de los db.
 */
public class MeasurementTagsFragment extends Fragment {

    private TimeListenerChronometer tagsListener;
    private Set<Integer> checkedTags = new TreeSet<>();
    private static final int selectedColor = Color.parseColor("#80cbc4");
    private final List<ToggleButton> listButtons = new ArrayList<>();

    public void setTagsListener(TimeListenerChronometer tagsListener) {
        this.tagsListener = tagsListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_measurement_tags, container, false);
        // Read the last stored record
        Map<String, Storage.TagInfo> tagToIndex = new HashMap<>(Storage.TAGS_INFO.length);
        for (Storage.TagInfo sysTag : Storage.TAGS_INFO) {
            tagToIndex.put(sysTag.name, sysTag);
        }
        Resources r = getResources();
        String[] tags = r.getStringArray(R.array.tags);
        // Append tags items
        for (Storage.TagInfo tagInfo : Storage.TAGS_INFO) {
            ViewGroup tagContainer = (ViewGroup) view.findViewById(tagInfo.location);
            if (tagContainer != null && tagInfo.id < tags.length) {
                addTag(tags[tagInfo.id], tagInfo.id, tagContainer, tagInfo.color != -1 ? r.getColor
                        (tagInfo.color) : -1);
            }
        }
        return view;
    }

    private void addTag(String tagName, int id, ViewGroup column, int color) {
        final ToggleButton tagButton = new ToggleButton(requireContext());
        if (color != -1) {
            LinearLayout colorBox = new LinearLayout(requireContext());
            // Convert the dps to pixels, based on density scale
            final int tagPaddingPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    1, getResources().getDisplayMetrics());
            final int tagPaddingPxBottom = (int) TypedValue.applyDimension(TypedValue
                            .COMPLEX_UNIT_DIP,
                    3, getResources().getDisplayMetrics());
            //use a GradientDrawable with only one color set, to make it a solid color
            colorBox.setBackgroundResource(R.drawable.tag_round_corner);
            GradientDrawable gradientDrawable = (GradientDrawable) colorBox.getBackground();
            gradientDrawable.setColor(color);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams
                    .MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(tagPaddingPx, tagPaddingPx, tagPaddingPx, tagPaddingPxBottom);
            colorBox.setLayoutParams(params);
            colorBox.addView(tagButton);
            column.addView(colorBox);
        } else {
            column.addView(tagButton);
        }
        tagButton.setTextOff(tagName);
        tagButton.setTextOn(tagName);
        boolean isChecked = checkedTags.contains(id);
        tagButton.setChecked(isChecked);
        if (isChecked) {
            tagButton.setTextColor(selectedColor);
        }
        tagButton.setOnCheckedChangeListener(new CommentActivity.TagStateListener(id, checkedTags));
        tagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (((MeasurementActivity) getActivity()).getStart() != null && !((MeasurementActivity) getActivity()).getIsPaused()) {
                        if (tagButton.isChecked()) {
                            listButtons.add(tagButton); // Se añade a la lista
                        } else {
                            listButtons.remove(tagButton); // Se quita de la lista.
                        }
                        /**
                         * Se pasa el tag, si está marcado o no y la lista de botones que están marcados.
                         */
                        tagsListener.onButtonClicked(tagButton.getText().toString(), tagButton.isChecked(), listButtons);
                    } else {
                        Toast.makeText(requireContext(), "El proceso de grabación debe estar iniciado", Toast.LENGTH_LONG).show();
                        tagButton.setChecked(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        tagButton.setMinHeight(0);
        tagButton.setMinimumHeight(0);
        if(tagButton.getText().toString().contentEquals("Fuegos artificiales") || tagButton.getText().toString().contentEquals("Ruido industrial")) {
            tagButton.setTextSize(Dimension.SP, 7);

        } else {
            tagButton.setTextSize(Dimension.SP, 9);
        }
        tagButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        tagButton.invalidate();
    }

    /**
     * Interfaces para inicio, pausa, fin y value.
     */
    public interface TimeListenerChronometer {
        void onButtonClicked(String name, Boolean isChecked, List<ToggleButton> list) throws JSONException;

        void onFinishedRecord();

        void onPausedRecord(Boolean paused);

        void onAddingValue(Double leq);
    }

}
