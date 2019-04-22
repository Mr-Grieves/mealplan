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
    private Unit recipe_unit;
    //TODO: private float amnt_used = 0;

    RecipeIngredient(){
        super();
        this.amount = -1;
        this.recipe_unit = Unit.NA;
        this.details = null;
    }

    RecipeIngredient(GenericIngredient _ing, float _tot, Unit _ru, String _deets){
        super(_ing);
        this.amount = _tot;
        this.recipe_unit = _ru;
        this.details = _deets;
    }

    void printIngredient(){
        Log.i(TAG,"Recipe Ingredient - "+
                ((amount==-1)?"":amount+" ") +
                ((recipe_unit==Unit.self || amount==-1)?"":recipe_unit+" ") +
                name +
                ((details==null)?"":", "+details));
    }

    boolean checkIfValid(){
        if (this.name == null) {
            Log.e(TAG, "INVALID ING: Has no name");
            return false;
        } else if(this.recipe_unit == Unit.NA) {
            Log.e(TAG, "INVALID ING: Has no recipe unit");
            return false;
        } else if(this.amount == -1) {
            Log.w(TAG,this.name+" has no amount, preoceeding...");
            return true;
        } else {
            Log.v(TAG, "Valid ingredient added: " + this.name+"\n");
            return true;
        }

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
    void setRecipeUnit(Unit _ru){this.recipe_unit = _ru;}
    Unit getRecipeUnit(){return this.recipe_unit;}
}
