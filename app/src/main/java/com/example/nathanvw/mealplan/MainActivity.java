package com.example.nathanvw.mealplan;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "nvw-main";
    private final String INGREDIENT_TABLE_NAME = "ingredient_table";

    private Button btn_LoadIngredients, btn_LoadRecipe;
    private TextView mTextMessage;
    private AssetManager assetManager;

    public static Vector<GenericIngredient> ingredient_list = null;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        assetManager = getAssets();
        btn_LoadIngredients = (Button) findViewById(R.id.loadIngredients);
        btn_LoadRecipe = (Button) findViewById(R.id.loadRecipe);
        mTextMessage = (TextView) findViewById(R.id.message);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        btn_LoadIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ingredient_list = loadAllGenericIngredients(assetManager);
                if(ingredient_list != null){
                    Log.i(TAG, "Success!!");
                    for(GenericIngredient i : ingredient_list){
                        i.printIngredient();
                    }
                } else
                    Log.e(TAG,"Failed to load ingredient table");
            }
        });

        btn_LoadRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecipeParser rp = new RecipeParser();
                rp.parseFromTextFile(assetManager,"broccoli-bolognese.txt").printRecipe();
                rp.parseFromTextFile(assetManager,"lemon-curd-tart.txt").printRecipe();
                rp.parseFromTextFile(assetManager,"strawberry-summer-cake.txt").printRecipe();
                rp.parseFromTextFile(assetManager,"mushroom-larb.txt").printRecipe();
            }
        });

        btn_LoadIngredients.callOnClick();
        btn_LoadRecipe.callOnClick();
    }

    // Loads all ingredients from ingredient_table.csv database
    private Vector<GenericIngredient> loadAllGenericIngredients(AssetManager am){
        String line = "";
        String delimeter = ",";
        int i = 0;

        Vector<GenericIngredient> ingredient_list = new Vector<GenericIngredient>();
        InputStream is = null;
        try {
            is = am.open(INGREDIENT_TABLE_NAME+".csv");
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))){
            while((line = br.readLine()) != null) {
                String[] line_str = line.split(delimeter);
                i++;
                if(line_str.length != GenericIngredient.NUM_INGREDIENT_PROPERTIES){
                    Log.e(TAG,"Ingredient in csv has wrong number of properties! Line: "+i);
                } else {
                    GenericIngredient new_ingredient = new GenericIngredient(
                            line_str[0],                        // name
                            FoodGroup.valueOf(line_str[1]),     // FoodGroup
                            Unit.valueOf(line_str[2]),
                            Integer.parseInt(line_str[3]));     // shelflife

                    ingredient_list.add(new_ingredient);
                }
            }
        } catch(IOException e){
            e.printStackTrace();
            return null;
        }

        return ingredient_list;
    }

    /*public static Vector<GenericIngredient> findGenericIngredient(String _n){
        Vector<GenericIngredient> matching_gis = new Vector<GenericIngredient>();
        for(GenericIngredient gi : ingredient_list){
            if(gi.getName().matches(_n)){
                matching_gis.add(gi);
            }
        }
        return matching_gis;
    }*/

    public static GenericIngredient getGenericIngredientAt(int i){
        return ingredient_list.get(i);
    }

    public static GenericIngredient getMostFrequentGenericIngredient(Vector<Integer> a){
        if(a.size() == 0) {
            Log.e("nvw-ggi", "Given a 0-length vector");
            return null;
        }
        if(a.size() == 1)
            return getGenericIngredientAt(a.firstElement());

        // First make a map of given indices -> frequencies
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        for (int i : a) {
            Integer count = map.get(i);
            map.put(i, count != null ? count+1 : 0);
        }

        //for(Map.Entry<Integer,Integer> i : map.entrySet())
        //    Log.e("nvw-fuck",i.getKey()+" -> "+i.getValue());

        //Now, we find the number with the maximum frequency and return it:
        Integer popular = Collections.max(map.entrySet(),
                new Comparator<Map.Entry<Integer, Integer>>() {
                    @Override
                    public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                        int comp = o1.getValue().compareTo(o2.getValue());
                        if (comp != 0)
                            return comp;
                        else {
                            int diff = ingredient_list.get(o2.getKey()).getName().length() -
                                    ingredient_list.get(o1.getKey()).getName().length();
                            //Log.e("nvw-FUCKME", " equal lengths detected! returning: "+diff);
                            return diff;
                        }
                    }
                }).getKey();

        return ingredient_list.get(popular);
    }

    public static Vector<Integer> findMatchingGIIndices(String _n){
        Vector<Integer> matching_idxs = new Vector<Integer>();
        Integer idx = 0;

        for(GenericIngredient gi : ingredient_list){
            if(gi.getName().matches(".*\\b"+_n+"\\b.*")){
                matching_idxs.add(idx);
            }
            idx++;
        }
        return matching_idxs;
    }

    public void addSpeltBerrieSaladRecipe(){
        Recipe recipe = new Recipe();

        recipe.searchAndAddIngredient("radish",5, Unit.self, "very thinly sliced");
        recipe.searchAndAddIngredient("plum vinegar",2, Unit.tbsp,null);
        recipe.searchAndAddIngredient("apple cider vinegar",2,Unit.tbsp,null);
        recipe.addNewStep("Place radishes in a bowl/glass jar and add vinegars; toss well. Marinate for at least 6 hours and up to 4 days in the fridge");

        recipe.searchAndAddIngredient("spelt berries",1.25f,Unit.cup,"washed and soaked 12 to 24 hrs in 3 cups filtered water");
        recipe.searchAndAddIngredient("olive oil",3,Unit.tbsp,"divided");
        recipe.searchAndAddIngredient("garlic",1,Unit.self,"minced");
        recipe.searchAndAddIngredient("peas",2,Unit.cup,"frozen");
        recipe.searchAndAddIngredient("parsley",0.5f,Unit.cup,"chopped");
        recipe.searchAndAddIngredient("dill",0.25f,Unit.cup,"chopped");
        recipe.searchAndAddIngredient("feta",5,Unit.oz,"drained and crumbled");

        recipe.addNewStep("Drain and rinse spelt berries. Place in a pot and cover with about 4 cups filtered water. Bring to the boil, cover, reduce heat to low, and simmer for 11/2 hrs or until tender. Add extra water as needed to keep spelt berries covered whilst simmering. remove from heat, drain well, and set aside to cool.");
        recipe.addNewStep("If using fresh peas, bring a small pot of water to boil. Add peas, and cook for 2 minutes or until tender. Remove from the heat, drain, and set aside to cool. If using frozen, skip this step.");
        recipe.addNewStep("Warm 2 Tbsp olive oil in a sml frying pan over medium heat. Add garlic, and salute for 1-2 mins or until golden. Stir in peas, add a pinch of salt and pepper, and cook 2 mins longer or until heated trough. Allow to cool.");
        recipe.addNewStep("Place spelt berries, radishes and pickling liquid, remaining olive oil, peas, parsley, dill, and feta in a large bowl; toss to combine. Season to taste with salt and pepper, and serve immediately.");

        recipe.printRecipe();
    }
}
