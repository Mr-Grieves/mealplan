package com.example.nathanvw.mealplan;

import android.util.Log;

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

    private String details;
    private float amount;
    //TODO: private float amnt_used = 0;

    RecipeIngredient(){
        super();
        this.amount = -1;
        details = null;
    }

    RecipeIngredient(GenericIngredient _ing, float _tot, String _deets){
        super(_ing);
        this.details = _deets;
        this.amount = _tot;
    }

    void printIngredient(){
        Log.i(TAG,"Recipe Ingredient - "+
                ((amount==-1)?"":amount+" ") +
                ((unit==Unit.self || amount==-1)?"":unit+" ") +
                name +
                ((details==null)?"":", "+details));
    }

    float getAmount(){return this.amount;}
    String getDetails(){return this.details;}
    void setAmount(float _a){this.amount = _a;}
    void addDetail(String _s){
        if (details == null)
            this.details = _s;
        else
            this.details = this.details + ", " + _s;
    }
}
