package com.example.nathanvw.mealplan;

import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;
/**
 * Created by nathanvw on 4/2/19.
 *
 * A class designed to automatically parse recipes from various sources
 */

enum State {FILLER, INGREDIENTS, STEPS}

class RecipeParser {
    private static final String TAG = "nvw-recpar";

    RecipeParser(){}

    // Going to try and format this according to bon appetit
    Recipe parseFromTextFile(AssetManager am, String filename){
        // Load file
        InputStream is = null;
        try {
            is = am.open(filename);
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }

        /* Read line by line
        String line = "";
        State curr_state = ""
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))){
            while((line = br.readLine()) != null) {
                String[] line_str = line.split(delimeter);
            }
        } catch(IOException e){
            e.printStackTrace();
            return null;
        }*/

        // Read everything into a string
        String line = null;
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))){
            while((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            br.close();
        } catch(IOException e){
            e.printStackTrace();
            return null;
        }
        String all_text = sb.toString();

        Log.i(TAG,"---------------------------------- before --------------------------------");
        Log.i(TAG,all_text+"\n\n");

        // Regex galore!
        Log.i(TAG,"--------------------------------- ingr_text -------------------------------");
        String pattern = "(?s)(Ingredients)(.*)(Recipe Preparation)(.*)";
        Log.i(TAG,"ingredients found: "+(all_text.matches(pattern)?"FOUND":"NOPE"));
        String ingr_text = all_text.replaceAll(pattern,"$2");
        Log.i(TAG,ingr_text+"\n\n");


        Log.i(TAG,"--------------------------------- step_text -------------------------------");
        Log.i(TAG,"preparation found: "+(all_text.matches(pattern)?"FOUND":"NOPE"));
        String step_text = all_text.replaceAll(pattern,"$4");
        Log.i(TAG,step_text+"\n\n");


        return null;
    }
}
