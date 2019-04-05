package com.example.nathanvw.mealplan;

import android.util.Log;

import java.util.Vector;

/**
 * Created by nathanvw on 4/1/19.
 *
 * This class represents an entire recipe.
 * It contains two main members:
 *  - ingredients: a list of all the RecipeIngredients used in the recipe.
 *  - steps: a list of all the RecipeSteps used in the recipe
 */
class Recipe {
    private static final String TAG = "nvw-rec";
    private Vector<RecipeIngredient> ingredients;
    private Vector<RecipeStep> steps;

    Recipe(){
        this.ingredients = new Vector<RecipeIngredient>();
        this.steps = new Vector<RecipeStep>();
    }

    void searchAndAddIngredient(String _name, float _amount, Unit _recipe_unit, String _details){
        Vector<Integer> word_matches = MainActivity.findGenericIngredientMatches(_name);
        GenericIngredient gi = MainActivity.getGenericIngredientAt(word_matches);

        if(gi == null){
            // TODO: add new gi
            Log.e(TAG,"GenericIngredient "+_name+" not found in ingredient table!");
            return;
        }

        if(gi.getUnit() != _recipe_unit){
            Log.w(TAG, "Stored unit for "+_name+" ("+gi.getUnit()+") is different than recipe's ("+_recipe_unit+")");
            // TODO: convert _unit to gi.getUnit()

        }
        ingredients.add(new RecipeIngredient(gi, _amount, _details));
    }

    void addNewStep(String raw_text){
        // TODO: automatically detect all amounts/instances of ingredients somehow..

        // For now lets just add the text
        steps.add(new RecipeStep(raw_text));
    }

    void printRecipe(){
        Log.i(TAG,"\n\t--- Ingredients ---");
        for(RecipeIngredient ri : ingredients){
            ri.printIngredient();
        }
        Log.i(TAG,"\n\t--- Steps ---");
        for(RecipeStep rs : steps){
            rs.printStep();
            Log.i(TAG,"\t\t - "+rs.getText());
        }
    }

    void addIngredient(RecipeIngredient ri) {ingredients.add(ri);}
}
