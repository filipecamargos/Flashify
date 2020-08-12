package CS246.Team01.Flashify;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This MainActivity Class will be responsible for setting
 * the start content of the application. It will read the necessary
 * data from a file inside the device and call the proper methods according
 * with the user interaction
 */
public class MainActivity extends AppCompatActivity {

    /**
     * menu = The menu set on the client screen
     * flashCardMap = A map holding the topic=key and object=value of a flash card set
     * topicFlashCard = a object holding a single set of flashcards with a specific topic
     */
    private ListView menu;
    private Map<String, ArrayList<FlashCard>> flashCardMap = new HashMap<>();
    private ArrayList<FlashCard> topicFlashcards = new ArrayList<>();
    final String myPrefsFile = "MyPrefsFile";


    /**
     * Call the set content on the Creation
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set the content of the activity
        setContent();
    }

    /**
     * This method will set an updated content with the
     * the activity is restarted
     */
    @Override
    protected void onRestart() {
        super.onRestart();

        //Set an updated content of the activity
        setContent();
    }

    /**
     * This method will reset the updated
     * content when main is resumed after
     * any changes on other parts of the application
     */
    @Override
    protected void onPostResume() {
        super.onPostResume();

        //Set an updated content of the activity
        setContent();
    }

    /**
     * This method is responsible for setting the content
     * of the main page - it is called when the Main activity
     * is created or restarted
     */
    private void setContent() {
        setContentView(R.layout.activity_main);
        SharedPreferences settings = getSharedPreferences(myPrefsFile, 0);

        //Set conditions when the app is firs loaded
        if (settings.getBoolean("MY_FIRST_TIME", true)) {

            //preload flashcards
            preloadFlashCards();

             // record the fact that the app has been started at least once
            settings.edit().putBoolean("MY_FIRST_TIME", false).apply();
        }

        ArrayList<String> topicsMenu;

        // find the menu, set it to variable named menu
        menu = findViewById(R.id.menu);

        //Instantiate the reading file class
        FileHelper fileHelper = new FileHelper(this);

        //Set the flashCardMap
        flashCardMap = fileHelper.getFlashCardMap();

        //Set topic Menu
        topicsMenu = fileHelper.getTopicsMenu();

        topicFlashcards = fileHelper.convertToList(flashCardMap);

        // Create a simple adapter to put the list into the list view
        menu.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, topicsMenu));

        // Set the click listener for the list view
        menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Get the object tapped by the user
                Object topicItem = menu.getItemAtPosition(position);

                // Call the new topic activity creating function passing the element tapped
                setTopicView(topicItem.toString());
            }
        });
    }

    /**
     *  This method will call the createFlashCard activity when
     *  the user taps the "Add Flash Card" button
     */
    public void createNewFlashCard(View view){

        //Create intent and start a new intent
        Intent intent = new Intent (this, NewFlashCard.class);
        intent.putExtra("EDIT", false);
        startActivity(intent);
    }

    /**
     * This method will call the next activity that contains a specific
     * topic and a set of flashcards that belongs to it
     * @param topic  A String that holds the topic to be presented on the next activity
     */
    private void setTopicView(String topic){

        // Get the flashcards list corresponding to the topic
        ArrayList<FlashCard> topicFlashcards = flashCardMap.get(topic);

        //Create intent
        Intent intent = new Intent (this, TopicActivity.class);

        //Passes the topic to the new intent
        intent.putExtra("TOPIC", topic);

        // Passes the list of flashcards corresponding to that topic to the new intent
        intent.putParcelableArrayListExtra("LIST", topicFlashcards);
        startActivity(intent);
    }

    /**
     * PreloadFlashCards will take care of the preloading some
     * flashcard when the user is using the flashcard for the
     * first time | Once they delete them. They will be not showed again
     */
    private void preloadFlashCards() {
        FlashCard demo1 = new FlashCard("English", "Quiz", "Quick test of knowledge");
        FlashCard demo2 = new FlashCard("English", "Afraid", "Impressed with fear or apprehension");
        FlashCard demo3 = new FlashCard("English", "Dead", "No longer living");
        FlashCard demo4 = new FlashCard("English", "Film", "A medium used to capture images in a camera");
        FlashCard demo5 = new FlashCard("Biology", "Abdomen", "the region between the pelvis (pelvic brim) and the thorax (thoracic diaphragm) in vertebrates, including humans");
        FlashCard demo6 = new FlashCard("Biology", "Brain", "an organ that coordinates nervous system function in vertebrate and most invertebrate animals");
        FlashCard demo7 = new FlashCard("Biology", "Gene", "an extremely specific sequence of nucleotide monomers that has the ability to completely or partially control the expression of one or more traits in every type of living organism");
        FlashCard demo8 = new FlashCard("Biology", "Metamorphosis", "a process by which animals undergo extreme, rapid physical changes some time after birth");
        ArrayList<FlashCard> list1 = new ArrayList<>();
        list1.add(0, demo1);
        list1.add(1, demo2);
        list1.add(2, demo3);
        list1.add(3, demo4);
        ArrayList<FlashCard> list2 = new ArrayList<>();
        list2.add(0, demo5);
        list2.add(1, demo6);
        list2.add(2, demo7);
        list2.add(3, demo8);
        Map<String, ArrayList<FlashCard>> defaultMap = new HashMap<>();
        defaultMap.put("English", list1);
        defaultMap.put("Biology", list2);
        FileHelper fileHelper = new FileHelper(this);
        fileHelper.saveToFile(defaultMap);
        Toast toast = Toast.makeText(this,"Welcome to Flashify - to get started, add some flashcards", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * On the click of the search button this method
     * will call the WordSearch activity and pass
     * the Map =  which is a map with key =  topic and value = list of FlashCard objects
     * @param view used in android to interact with the UI
     */
    public void search(View view){

        //Create and start a new intent with the Search functionality
        Intent intent = new Intent(this, WordSearch.class);
        intent.putExtra("MAP", (Serializable) flashCardMap);
        startActivity(intent);
    }
}
