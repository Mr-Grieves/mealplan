package com.example.nathanvw.mealplan;

import android.util.Log;

/**
 * Created by nathanvw on 4/1/19.
 *
 * This class represents generic ingredients used in recipes.
 * It holds all the information specific to the ingredient itself, but nothing about how it is used in recipes
 *
 * The ingredient table .csv stores the database of all known ingredients
 */

enum FoodGroup {
    FRUIT,
    VEGETABLE,
    HERB,
    SPICE,
    GRAIN,
    NUT,
    LEGUME,
    MEAT,
    FISH,
    DAIRY,
    CONDIMENT
}

enum Unit {
    self,
    g,
    oz,
    lb,
    ml,
    tsp,
    tbsp,
    cup
}

class GenericIngredient {
    private static final String TAG = "nvw-ing";
    public static final int NUM_INGREDIENT_PROPERTIES = 4;

    private String name;
    private FoodGroup group;
    private Unit unit;
    private int shelflife; // days

    // Potential optimizers
    private boolean optimize, primary;
    private float min_amount;

    GenericIngredient(GenericIngredient _ing){
        this.name = _ing.name;
        this.group = _ing.group;
        this.unit = _ing.unit;
        this.shelflife = _ing.shelflife;
    }

    GenericIngredient(String _name, FoodGroup _fg, Unit _u, int _sl){
        this.name = _name;
        this.group = _fg;
        this.unit = _u;
        this.shelflife = _sl;
    }

    void printIngredient(){
        Log.i(TAG,"Ingredient - Name: "+String.format("%-20s",name)+"Group: "+String.format("%-12s",group)+"Unit: "+String.format("%-7s",unit)+"Shelflife: "+shelflife+" days");
    }

    String getName(){return this.name;}
    Unit getUnit(){return this.unit;}
}

