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
    UNKNOWN,
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
    CONDIMENT,
    BAKING
}

enum Unit {
    self,
    g,
    oz,
    lb,
    ml,
    tsp,
    tbsp,
    cup,
    quart
}

class GenericIngredient {
    private static final String TAG = "nvw-ing";
    public static final int NUM_INGREDIENT_PROPERTIES = 4;

    protected String name;
    protected FoodGroup group;
    protected Unit unit;
    protected int shelflife; // days

    // Potential optimizers
    private boolean optimize, primary;
    private float min_amount;

    GenericIngredient(){
        this.name = null;
        this.group = FoodGroup.UNKNOWN;
        this.unit = Unit.self;
        this.shelflife = -1;
    }

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

    void setGenerics(GenericIngredient _gi){
        this.name = _gi.name;
        this.group = _gi.group;
        this.unit = _gi.unit;
        this.shelflife = _gi.shelflife;
    }

    void printIngredient(){
        Log.i(TAG,"Ingredient - Name: "+String.format("%-20s",name)+"Group: "+String.format("%-12s",group)+"Unit: "+String.format("%-7s",unit)+"Shelflife: "+shelflife+" days");
    }

    String getName(){return this.name;}
    Unit getUnit(){return this.unit;}
    void setUnit(Unit _u){this.unit = _u;}
}

