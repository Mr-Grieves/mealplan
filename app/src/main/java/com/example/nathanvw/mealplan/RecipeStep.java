package com.example.nathanvw.mealplan;

import java.util.Vector;

/**
 * Created by nathanvw on 4/1/19.
 *
 * This class represents the steps in a recipe.
 * They contain instructions as well as references to RecipeIngredients
 */
class RecipeStep {
    private static final String TAG = "nvw-recstep";

    private String instruction;
    private int time_sec;
    private Vector<RecipeIngredient> reference_ingredients;
    private Vector amounts_used;

    RecipeStep(String _text){
        this.instruction = _text;
        this.reference_ingredients = new Vector<RecipeIngredient>();
        this.amounts_used = new Vector();
    }

    String getText(){return this.instruction;}

    void printStep(){//TODO:
    }

}
