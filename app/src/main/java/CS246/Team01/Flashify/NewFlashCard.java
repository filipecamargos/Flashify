package CS246.Team01.Flashify;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This class handles the ability to add a new
 * flashcard into the map -  This activity is
 * started in Main or in when a flashcard is called
 * to be edit in FlashCardDisplay
 */
public class NewFlashCard extends AppCompatActivity {

    /***
     * flashCardList = Map with key = topic and value = list of FlashCards
     * edit = A boolean to control if the activity is been called from a edit functionality
     * topicFlashCards = A list containing the topics
     * index = Track the index inside the list
     * saveMessage = A successful saving Message
     * fileHelper = a FileHelper variable to manipulate the data in the file
     */
    private static Map<String, ArrayList<FlashCard>> flashCardList = new HashMap<>();
    private Boolean edit;
    private ArrayList<FlashCard> topicFlashcards;
    private int index;
    private String saveMessage;
    private FileHelper fileHelper;

    /**
     * On create set the content for this activity
     * @param savedInstanceState default from the android Studio
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_flash_card);

        final Button saveButton = findViewById(R.id.saveButton);

        //instantiate FileHelper with context this for newFlashCard and null for the rest of the parameters
        fileHelper = new FileHelper(this);

        //get the text from the EditText's and assign to variables
        EditText topicText = findViewById(R.id.topicText);
        EditText frontText = findViewById(R.id.frontText);
        EditText backText = findViewById(R.id.backText);

        //get values from intent
        Intent intent = getIntent();
        String _topic = intent.getStringExtra("TOPIC");
        String _front = intent.getStringExtra("FRONT");
        String _back = intent.getStringExtra("BACK");

        //Get the intent of MYLIST
        topicFlashcards =  (ArrayList<FlashCard>) getIntent().getSerializableExtra("MYLIST");

        //get teh index if no index is passed it is set to zero
        index = getIntent().getIntExtra("INDEX", 0);

        //this value comes as TRUE from FlashCardDisplay and FALSE from MainActivity
        edit = Objects.requireNonNull(intent.getExtras()).getBoolean("EDIT");

        //set the EditText's with the topic, front, and back values received from FlashCardDisplay
        topicText.setText(_topic);
        frontText.setText(_front);
        backText.setText(_back);

        //if edit is true, meaning we are here to edit the card, not create a new one, and
        //if the topic text equals the current topic, enable the save button, otherwise leave it disabled
        if(edit){
            if(topicText.getText().toString().equals(_topic)){
                saveButton.setEnabled(true);
            }
        }

        /**
         * A TextWatcher is created in onCreate. This text watcher will listen to the
         * text input and will enable or disable the save button if the text input has
         * text or not.
         * --Save overrided methods from the implementation
         */
        topicText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().trim().length()==0){
                    saveButton.setEnabled(false);
                } else {
                    saveButton.setEnabled(true);
                }
            }

            /**
             * Default implementation to allow the onTextChange to work
             */
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            /**
             * Default implementation to allow the onTextChange to work
             */
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }


    /**
     * This method will be called in the create flash Card screen
     * when the user taps on the SAVE button and will store the
     * user's data input in the device as a Json file
     * */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void saveFlashCard(View view){

        // Get the Information from the front view and assign them
        EditText topicText = findViewById(R.id.topicText);
        String topic = topicText.getText().toString();
        EditText frontText = findViewById(R.id.frontText);
        String front = frontText.getText().toString();
        EditText backText = findViewById(R.id.backText);
        String back = backText.getText().toString();

        //Create new FlashCard
        FlashCard flashCard = new FlashCard(topic, front, back);

         /* If there is already a list with the topic then the flash Card will be added to that list,
        if not, a new list with that topic's name will be created and the current flash card
        will be added to the new list
        */
        if (flashCardList.containsKey(topic)){

            //if _edit is true we are coming from FlashCardDisplay and want to edit the current flashcard
            if (edit) {

                //Converting the List into a Map
                Map<String, ArrayList<FlashCard>> fileData = fileHelper.getFlashCardMap();
                topicFlashcards.set(index, flashCard);

                //Replace the object to the update one
                fileData.replace(topic, topicFlashcards);
                fileHelper.saveToFile(flashCardList);
                saveMessage = "Flashcard Updated!";
            } else { //else we are creating a brand new flashcard

                Objects.requireNonNull(flashCardList.get(topic)).add(flashCard);
                fileHelper.saveToFile(flashCardList);
                saveMessage = "Saved Successfully!";
            }
        } else { //In case we are not coming from edit and it is a new topic to be created

            //Set the content of the new flashcard
            ArrayList<FlashCard> list = new ArrayList<>();
            flashCardList.put(topic, list);
            Objects.requireNonNull(flashCardList.get(topic)).add(flashCard);

            //save it to the file
            fileHelper.saveToFile(flashCardList);
            saveMessage = "Saved Successfully!";
        }


        // The toast will let you know whether the saved was successful or not.
        Toast toast = Toast.makeText(getApplicationContext(),saveMessage , Toast.LENGTH_SHORT);
        toast.show();

        //Clear Flash Card fields once the data is saved.
        topicText.setText("");
        frontText.setText("");
        backText.setText("");
    }

    /**
     * This method is used in main to updated changes in the list.
     * @param list Take the map with the key = topic and Value = List of FlashCards
     */
    static void setFlashCardList(Map<String, ArrayList<FlashCard>> list){
        flashCardList = list;
    }
}
