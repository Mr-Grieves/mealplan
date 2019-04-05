package com.example.nathanvw.mealplan;

import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 * Created by nathanvw on 4/2/19.
 *
 * A class designed to automatically parse recipes from various sources
 */

//enum State {FILLER, INGREDIENTS, STEPS}

class RecipeParser {
    private static final String TAG = "nvw-recpar";

    private HashMap<String,Unit> known_measurements = new HashMap<String,Unit>() {{
        //sel
        // ...

        //g
        put("g",Unit.g);
        put("gram",Unit.g);
        put("grams",Unit.g);

        //oz
        put("oz",Unit.oz);
        put("ounce",Unit.oz);
        put("ounces",Unit.oz);

        //lb
        put("lb",Unit.lb);
        put("lbs",Unit.lb);
        put("pound",Unit.lb);
        put("pounds",Unit.lb);

        //ml
        put("ml",Unit.ml);
        put("mls",Unit.ml);
        put("millilitre",Unit.ml);
        put("millilitres",Unit.ml);

        //tsp
        put("tsp",Unit.tsp);
        put("tsps",Unit.tsp);
        put("teaspoon",Unit.tsp);
        put("teaspoons",Unit.tsp);

        //tbsp
        put("tbsp",Unit.tbsp);
        put("tbsps",Unit.tbsp);
        put("tablespoon",Unit.tbsp);
        put("tablespoons",Unit.tbsp);

        //cup
        put("cp",Unit.cup);
        put("cup",Unit.cup);
        put("cups",Unit.cup);
    }};

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

        // Prepare Recipe
        Recipe recipe = new Recipe();

        // Regex galore!
        //Log.i(TAG,"--------------------------------- ingr_text -------------------------------");
        String pattern = "(?s)(Ingredients)(.*)(Recipe Preparation)(.*)";
        //Log.i(TAG,"ingredients found: "+(all_text.matches(pattern)?"FOUND":"NOPE"));
        String ingr_text = all_text.replaceAll(pattern,"$2");

        // Parse out all ingredients from 'Ingredients'
        parseIngredientsFromText(ingr_text, recipe);


        //Log.i(TAG,"--------------------------------- step_text -------------------------------");
        //Log.i(TAG,"preparation found: "+(all_text.matches(pattern)?"FOUND":"NOPE"));
        String step_text = all_text.replaceAll(pattern,"$4");
        //Log.i(TAG,step_text+"\n\n");

        // Parse out all steps from 'Recipe Preparation'
        parseStepsFromText(step_text, recipe);

        return null;
    }

    void parseIngredientsFromText(String raw_text, Recipe recipe){
        String[] ings_text = raw_text.split("\n");
        for (String ing_text: ings_text) {
            // Skip empties
            if(ing_text.equals(""))
                continue;

            RecipeIngredient rec_ing = new RecipeIngredient();

            // What to do now...
            // ex1: "    1 large head of broccoli (1¼–1½ pounds), cut into florets, stalk peeled and finely chopped"
            // ex2: "    2 tablespoons extra-virgin olive oil, plus more for drizzling"

            // 0. split into phrases
            String[] phrases = ing_text.split(",|\\(|\\)|\\.");
            for (String phrase : phrases) {
                // Trim leading and trailing whitespace
                phrase = phrase.replaceAll("(^\\s*)(.*)(\\s*$)","$2");

                // Skip empties
                if (phrase.equals(""))
                    continue;

                // No caps
                phrase = phrase.toLowerCase();

                /* ex1: Phrases =
                    1 large head of broccoli
                    1¼–1½ pounds
                    cut into florets
                    stalk peeled and finely chopped */

                /* ex2: Phrases =
                    2 tablespoons extra-virgin olive oil
    	            plus more for drizzling */

                // 1. search for amounts: "1" or "2 tablespoons"
                if(rec_ing.getAmount() == -1)
                    phrase = searchForAmountAndUnit(phrase, rec_ing);

                // 2. search for stored name: "broccoli"
                if(rec_ing.getName() == null)
                    phrase = searchForName(phrase, rec_ing);

                // Add remainder to details:
                rec_ing.addDetail(phrase);
            }

            recipe.addIngredient(rec_ing);
        }

        recipe.printRecipe();
    }

    void parseStepsFromText(String step_text, Recipe recipe){
        // TODO!
    }

    String searchForAmountAndUnit(String phrase, RecipeIngredient rec_ing){
        ///Log.i(TAG,"before: \""+phrase+"\"");
        // Replace fractions with decimals
        phrase = phrase.replaceAll("\\s*¼",".25");
        phrase = phrase.replaceAll("\\s*⅓",".333");
        phrase = phrase.replaceAll("\\s*½",".5");
        phrase = phrase.replaceAll("\\s*⅔",".666");
        phrase = phrase.replaceAll("\\s*¾",".75");

        // Search for measurements:
        String both_pat = "(\\d+(\\.\\d+)?)\\s(\\w+).+";
        String amount_str = phrase.replaceFirst(both_pat,"$1");
        String unit_str = phrase.replaceFirst(both_pat,"$3");

        // Set amount
        if(phrase.matches(both_pat)) {
            if (amount_str != null) {
                rec_ing.setAmount(Float.valueOf(amount_str));
                phrase = phrase.replaceAll("(.*)" + amount_str + "(.*)", "$1$2");
            } else {
                Log.e(TAG, "No amount detected, that's bad!");
                // TODO: enter amount manually
                // ...
            }

            // Search for known measurements:
            if (known_measurements.containsKey(unit_str)) {
                rec_ing.setUnit(known_measurements.get(unit_str));
                phrase = phrase.replaceAll("(.*)" + unit_str + "(.*)", "$1$2");
            } else {
                Log.i(TAG, "No unit detected, leaving as SELF: \""+phrase+"\"");
            }
        } else {
            Log.w(TAG,"Phrase didn't match anything, not adjusting amount or unit: \""+phrase+"\"");
        }

        //Log.i(TAG,"after: \""+phrase+"\"");
        return phrase.trim();
    }

    String searchForName(String phrase, RecipeIngredient rec_ing){
        // TODO: handle names with spaces in them!!!
        // ...

        GenericIngredient gi = null;
        // For every word in the given phrase, search the ingredient_table
        for (String word : phrase.split("\\s")) {
            gi = MainActivity.findGenericIngredient(word);
            if (gi != null) {
                // Store found results
                rec_ing.setGenerics(gi);

                // Remove name from phrase
                phrase = phrase.replaceFirst("(.*)"+gi.name+"(.*)","$1$2");
                return phrase.trim();
            }
        }

        Log.e(TAG,"Could not find any GenericIngredient in given phrase: \""+phrase+"\"");
        // TODO: add new GenericIngredient manually
        // ...

        return null;
    }
}
