package com.example.nathanvw.mealplan;

import java.util.Vector;

/**
 * Created by nathanvw on 4/1/19.
 *
 * This class represents ingredients IN a recipe context
 * It inherits the generic ingredient class, but contains additional info such as:
 *  - amnt_tot: the total amount of UNIT used in the recipe
 *  - amnt_used: a running total of the amount used throughout the recipe
 *  - details: any additional details about the ingredient
 *  -
 */
class RecipeIngredient extends GenericIngredient {
    private static final String TAG = "nvw-recing";

    private String details = null;
    private float amnt_tot = 0;
    private float amnt_used = 0;

    RecipeIngredient(GenericIngredient _ing, float _tot, String _deets){
        super(_ing);
        this.details = _deets;
        this.amnt_tot = _tot;
        this.amnt_used = 0;
    }

    float getAmount(){return this.amnt_tot;}
    String getDetails(){return this.details;}
}
