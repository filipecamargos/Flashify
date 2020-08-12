package CS246.Team01.Flashify;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * This class performs the searching activity once it
 * is called. It takes a string and search for words
 * that matches the users' input
 */
public class WordSearch extends AppCompatActivity {

    /**
     * flashCardMap = the map containing all the flashcards
     * sa = adapter for list view
     * flashCardListFromFile = the list of flashcards from the file
     * flashCardResult = the list of flashcards returned by the search function
     * searchWord = the word searched for by the user
     * itemClicked = int to store the position of the item in the list that was clicked
     */
    private Map<String, ArrayList<FlashCard>> flashCardMap = new HashMap<>();
    private SimpleAdapter sa;
    private ArrayList<FlashCard> flashCardListFromFile;
    private ArrayList<FlashCard> flashCardResult;
    private String searchWord;
    private int itemClicked;

    /**
     * Method onCreate is called on creation
     * sets the main view of the activity
     * @param savedInstanceState = Default passed on creation
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_search);
        //enables back button in top left
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //get the intent
        Intent intent = getIntent();
        //get the map from the intent
        flashCardMap = (HashMap<String, ArrayList<FlashCard>>)intent.getSerializableExtra("MAP");

        // Set the click listener for the list view
        ((ListView)findViewById(R.id.resultList)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Get the object tapped by the user
                Object topicItem = ((ListView)findViewById(R.id.resultList)).getItemAtPosition(position);
                //set itemClicked = to the position in the list that was clicked
                itemClicked = position;
                String result = ((HashMap<String,String>)topicItem).get("RESULT");
                String type = ((HashMap<String,String>)topicItem).get("TYPE");

                // Call the new topic activity creating function passing the element tapped
                assert type != null;
                processSelection(result, type);
            }
        });
    }

    /**
     * This method is called whenever the user chooses to navigate Up within
     * your application's activity hierarchy from the action bar.
     * @return = true
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    /**
     *  This method will compare the word input by the user and iterate
     *  through the map comparing it with the map's content. If it finds
     *  a match it will display it on a clickable list
     */
    public void searchWord(View view){
        //Create an Array
        ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
        HashMap<String,String> item;
        flashCardResult = new ArrayList<>();

        EditText keyWord = findViewById(R.id.searchWord);
        searchWord = keyWord.getText().toString();

        // Go trough all the keys(topics) and values(flashcard lists)
        for (Map.Entry<String, ArrayList<FlashCard>> entry : flashCardMap.entrySet()) {
            String topic = entry.getKey();
            ArrayList<FlashCard> flashcards = entry.getValue();

            if(topic.contains(searchWord)){
                item = new HashMap<>();
                item.put( "RESULT", topic);
                item.put( "TYPE", "Topic");
                list.add( item );
            }

            // Get the front text from all the flashcards in the list and place in the new fronts list.
            for(int i = 0; i < flashcards.size(); i++){
                String front = flashcards.get(i).getFront();
                String back = flashcards.get(i).getBack();

                if(front.contains(searchWord)){
                    item = new HashMap<>();
                    item.put( "RESULT", front);
                    item.put( "TYPE", "Flashcard Front");
                    list.add( item );

                    flashCardResult.add(flashcards.get(i));
                }

                if(back.contains(searchWord)){
                    item = new HashMap<>();
                    item.put( "RESULT", back);
                    item.put( "TYPE", "Flashcard Back");
                    list.add( item );

                    flashCardResult.add(flashcards.get(i));
                }
            }
        }

        sa = new SimpleAdapter(this, list,
                R.layout.twolines,
                new String[] { "RESULT","TYPE" },
                new int[] {R.id.result, R.id.type});

        ((ListView)findViewById(R.id.resultList)).setAdapter(sa);
    }

    /**
     * this method allows the user to click on a result from the list returned by the search
     * if it is a topic, pass the topic data into the intent and pass it to TopicActivity
     * if it is a front or back of the flashcard, pass the info needed to FlashCardDisplay
     * @param result = the result of the search
     * @param type = the type the result was, either front, back, or topic
     */
    public void processSelection(String result, String type){
        int index;
        if(type.equals("Topic")){
            // Get the flashcards list corresponding to the topic
            ArrayList<FlashCard> topicFlashcards = flashCardMap.get(result);

            //Create intent
            Intent intent = new Intent (this, TopicActivity.class);

            //Passes the topic to the new intent
            intent.putExtra("TOPIC", result);

            // Passes the list of flashcards corresponding to that topic to the new intent
            intent.putParcelableArrayListExtra("LIST", topicFlashcards);
            startActivity(intent);
        }
        else{
            // Create the intent
            Intent intent = new Intent (this, FlashCardDisplay.class);

            /* Search for the flashcard where the received front or back is.
               Is the result a flashcard front? */
            if(type.equals("Flashcard Front")){

                /* Search in all fronts found in the flashcards from the
                   list obtained from the search */
                for(int i = 0; i < flashCardResult.size(); i++){
                    String front = flashCardResult.get(i).getFront();

                    if(front.equals(result)){
                        //Instantiate FileHelper
                        FileHelper fileHelper = new FileHelper(this);
                        //set the list of flashcards = to the associated topic's flashcard list from the search result
                        flashCardListFromFile = fileHelper.getFlashCardMap().get(flashCardResult.get(0).getTopic());
                        //use findIndexOfList function to get the index of the card in the updated list of flashcards
                        //this allows you to go straight to the proper card, not just the start of the list of cards
                        index = findIndexOfList(itemClicked);
                        //pass the list and index as an intent to FlashCardDisplay
                        intent.putExtra("MYLIST", flashCardListFromFile);
                        intent.putExtra("INDEX", index);
                        startActivity(intent);
                    }
                }
            } else { //Search in all fronts found in the flashcards from the list obtained from the search

                for(int i = 0; i < flashCardResult.size(); i++){
                    String back = flashCardResult.get(i).getBack();

                    if(back.equals(result)){
                        //Instantiate FileHelper
                        FileHelper fileHelper = new FileHelper(this);
                        //set the list of flashcards = to the associated topic's flashcard list from the search result
                        flashCardListFromFile = fileHelper.getFlashCardMap().get(flashCardResult.get(0).getTopic());
                        //use findIndexOfList function to get the index of the card in the updated list of flashcards
                        //this allows you to go straight to the proper card, not just the start of the list of cards
                        index = findIndexOfList(itemClicked);
                        //pass the list and index as an intent to FlashCardDisplay
                        intent.putExtra("MYLIST", flashCardListFromFile);
                        intent.putExtra("INDEX", index);
                        startActivity(intent);
                    }
                }
            }
        }
    }

    /**
     * This Method find the method uses the index of the
     * clicked item to find the index of that item in the
     * the original list of flashcards
     * @param clickedIndex = The index of the item clicked
     * @return listItemIndex = the index of the item in the list from the file
     */
    private int findIndexOfList(int clickedIndex) {

        //Index to be return the item in the original list
        int listItemIndex = 0;

        //Create a temp flashcard for comparing
        FlashCard tempFlashCard = flashCardResult.get(clickedIndex);

        //loop through the array and find the index in the actual list
        for (int i = 0; i < flashCardListFromFile.size(); ++i) {
            if (tempFlashCard.getTopic().equals(flashCardListFromFile.get(i).getTopic()) &&
                    tempFlashCard.getFront().equals(flashCardListFromFile.get(i).getFront()) &&
                    tempFlashCard.getBack().equals(flashCardListFromFile.get(i).getBack())) {
                listItemIndex= i;
            }
        }

        //Return the actual index of the item in the original list
        return listItemIndex;
    }
}