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
        //self
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

        //quarts
        put("qt",Unit.qt);
        put("qts",Unit.qt);
        put("quart",Unit.qt);
        put("quarts",Unit.qt);

        //inch
        put("\"",Unit.inch);
        put("inch",Unit.inch);
        put("inches",Unit.inch);

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

        // TODO: detect section headers
        // ...

        //Log.i(TAG,"--------------------------------- ingr_text -------------------------------");
        String pattern = "(?si)(Ingredients)(.*)(Recipe Preparation)(.*)";
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

        return recipe;
    }

    void parseIngredientsFromText(String raw_text, Recipe recipe){
        String[] ings_text = raw_text.split("\n");
        for (String ing_text: ings_text) {
            // Skip empties
            if(ing_text.equals(""))
                continue;

            Log.v(TAG,"Creating a new ingredient for: \""+ing_text+"\"");
            RecipeIngredient rec_ing = new RecipeIngredient();

            // 0. split into phrases
            String[] phrases = ing_text.split(",|\\(|\\)|\\.");
            for (String phrase : phrases) {
                // Trim leading and trailing whitespace
                phrase = phrase.replaceAll("(^\\s*)(.*)(\\s*$)","$2");

                // Skip empties
                if (phrase.isEmpty())
                    continue;

                // No caps
                phrase = phrase.toLowerCase();

                // 1. search for amounts: "1" or "2 tablespoons"
                if(rec_ing.getAmount() == -1 && !isEmpty(phrase))
                    phrase = searchForAmountAndUnit(phrase, rec_ing);

                // 2. search for stored name: "broccoli"
                if(rec_ing.getName() == null && !isEmpty(phrase))
                    phrase = searchForName(phrase, rec_ing);

                // 3. Add remainder to details:
                if(!isEmpty(phrase)) rec_ing.addDetail(phrase.trim());
            }

            if(rec_ing.checkIfValid())
                recipe.addIngredient(rec_ing);
            else {
                Log.e(TAG, "Could not create a valid ingredient from: \"" + ing_text + "\"");
                //return;
            }
        }
    }

    boolean isEmpty(String s){ return s == null || s.isEmpty(); }

    void parseStepsFromText(String raw_text, Recipe recipe){
        // TODO!
        String[] steps_text = raw_text.split("\n");
        for (String step_text: steps_text){
            // Skip empties
            if(step_text.matches("^\\s*$")) {
                //Log.i(TAG, "phrase: \""+step_text+"\"");
                continue;
            }

            recipe.addNewStep(step_text.trim());
        }
    }

    String searchForAmountAndUnit(String phrase, RecipeIngredient rec_ing){
        Log.v(TAG,"Searching for amount/unit in: \""+phrase+"\"");
        // Replace fractions with decimals
        //TODO: only do this once on the whole recipe text
        phrase = phrase.replaceAll("\\s*¼",".25");
        phrase = phrase.replaceAll("\\s*1/4",".25");
        phrase = phrase.replaceAll("\\s*⅓",".333");
        phrase = phrase.replaceAll("\\s*1/3",".333");
        phrase = phrase.replaceAll("\\s*½",".5");
        phrase = phrase.replaceAll("\\s*1/2",".5");
        phrase = phrase.replaceAll("\\s*⅔",".666");
        phrase = phrase.replaceAll("\\s*2/3",".666");
        phrase = phrase.replaceAll("\\s*¾",".75");
        phrase = phrase.replaceAll("\\s*3/4",".75");

        // Search for measurements:
        String both_pat = "(\\d*(\\.\\d+)?)\\s(\\w+).*";
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
                rec_ing.setRecipeUnit(known_measurements.get(unit_str));
                phrase = phrase.replaceAll("(.*)" + unit_str + "(.*)", "$1$2");
                Log.v(TAG,"Setting unit as: "+known_measurements.get(unit_str));
            } else {
                Log.w(TAG, "No unit detected, leaving as SELF: \""+phrase+"\"");
                rec_ing.setRecipeUnit(Unit.self);
            }
        } else {
            Log.w(TAG,"Phrase didn't match anything, not adjusting amount or unit: \""+phrase+"\"");
            rec_ing.setRecipeUnit(Unit.self);
        }

        return phrase.trim();
    }

    String searchForName(String phrase, RecipeIngredient rec_ing){
        // TODO: handle names with spaces in them!!!
        // ...

        Vector<Integer> all_matches = new Vector<Integer>();
        GenericIngredient gi = null;
        String last_plural_word = null;

        // For every word in the given phrase, search the ingredient_table
        Log.v(TAG,"Searching for name in: \""+phrase+"\"");
        for (String word : phrase.split("\\s")) {
            // Trim plurals?
            if(word.matches(".*s$")) {
                last_plural_word = word;
                word = word.replaceAll("(.*)(ies$)", "$1y");
                word = word.replaceAll("(.*)(s$)", "$1");
            }

            // Single-word match
            /*if(!word_matches.isEmpty()) {
                Log.i(TAG, "word matches:");
                for (Integer i : word_matches) {
                    Log.i(TAG, "\ti = " + i);
                }
            }*/

            // multi-word match
            Vector<Integer> word_matches = MainActivity.findMatchingGIIndices(word);
            all_matches.addAll(word_matches);
            //Log.v(TAG,"Match found for \""+word+"\" at idx:"+word_matches.toString());
        }

        if(all_matches.isEmpty()) {
            Log.w(TAG, "No GenericIngredient name found in: \"" + phrase + "\", adding it to details");
            return phrase;
        } else {
            // Store found results
            gi = MainActivity.getMostFrequentGenericIngredient(all_matches);
            Log.v(TAG,"Most frequent match is: "+gi.getName());
            rec_ing.setGenerics(gi);

            // Remove name from phrase
            if (phrase.matches(".*\\b" + gi.name + "\\b.*")) {
                return phrase.replaceFirst("(.*)" + gi.name + "\\s?(.*)", "$1$2").trim();
            }else {// Damn plurals...
                // replace the last word in gi.name with last_plural_word
                String pluralled = gi.name.replaceAll("(.*)\\b(\\w+)", "$1"+last_plural_word);
                //Log.i(TAG,"replacing with "+pluralled);
                return phrase.replaceFirst("(.*)" + pluralled + "\\s?(.*)", "$1$2").trim();
            }
        }
    }
}
