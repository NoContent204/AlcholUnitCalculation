package com.example.drinkingcalculation;

import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;

import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity {

    ArrayList<int[]> DrinkList = new ArrayList<>();
    private TextView volume;
    private TextView percentage;
    private TextView drinks;
    private TextView time;
    private TextView units;
    private static SharedPreferences sharedPref = null;

    private static void init(Context context){
        sharedPref = context.getSharedPreferences("Drinks",Context.MODE_PRIVATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init(this);
        volume  = findViewById(R.id.editvolume);
        percentage = findViewById(R.id.editPercentage);
        drinks = findViewById(R.id.drinksList);
        time = findViewById(R.id.editTextTimeOfDrink);
        units = findViewById(R.id.displayunits);
        int size = sharedPref.getInt("drinkslist_size",0);

        for (int i=0; i<size; i++){
            int[] tmp = new int[3];
            for (int x=0; x<3; x++){
                tmp[x]=sharedPref.getInt("["+i+"]["+x+"]",0);
            }
            DrinkList.add(tmp);
        }
        for (int[] drink : DrinkList){
            drinks.append(drink[1]+"ml of "+drink[2]+"% at "+drink[0]+"\n");
        }
    }
    public void CalculateUnits(View view){
        Collections.sort(DrinkList, (a, b) -> Integer.compare(a[2], b[2])); //sorts array based on %
        double totalUnits = 0;
        int volSum = 0;
        for (int i=0;i<DrinkList.size();i++) {
            int ABV = DrinkList.get(i)[2];
            int prevABV = 0;
            if (i!=0) {
                prevABV = DrinkList.get(i - 1)[2];
            }
            if (i!=DrinkList.size()-1) {
                if (ABV != DrinkList.get(i + 1)[2]) {
                    volSum += DrinkList.get(i)[1];
                    totalUnits += (double) (volSum * ABV) / 1000;
                    volSum = 0;
                } else {
                    volSum += DrinkList.get(i)[1];
                }
            }else{
                if (prevABV==ABV){
                    volSum += DrinkList.get(i)[1];
                }else{
                    volSum = DrinkList.get(i)[1];
                }
                totalUnits += (double) (volSum * ABV) / 1000;
                volSum = 0;

            }
        }
        String result = totalUnits+" units";
        units.setText(result);
    }

    public void AddDrink(View view){
        int[] drinkinfo = {parseInt(time.getText().toString().replace(":","")),parseInt(volume.getText().toString()),parseInt(percentage.getText().toString())};
        DrinkList.add(drinkinfo);

        Collections.sort(DrinkList, (a, b) -> Integer.compare(a[0], b[0])); //sorts array based on time
        //clear text view displaying drinks
        drinks.setText("");
        time.setText("");
        volume.setText("");
        percentage.setText("");

        //add each drink in array to text view
        for (int[] drink : DrinkList){
            drinks.append(drink[1]+"ml of "+drink[2]+"% at "+drink[0]+"\n");
        }

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("drinkslist_size",DrinkList.size());
        for (int i=0; i<DrinkList.size(); i++){
            for (int x=0; x<3; x++){
                editor.putInt("["+i+"]["+x+"]",DrinkList.get(i)[x]);
            }
        }
        editor.apply();

    }

    public void ClearList(View view){
        drinks.setText("");
        DrinkList.clear();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
    }
}