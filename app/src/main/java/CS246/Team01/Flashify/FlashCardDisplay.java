package CS246.Team01.Flashify;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

/**
 * This class handles the flashcard display activity
 * where the user can see the flashcards and iterate through
 * them. This class also uses te File Helper to read updated
 * data from the file so the user can always have an updated
 * version of the flashcard list while in this activity
 */
public class FlashCardDisplay extends AppCompatActivity {

    /**
     * frontView = the front of the flashcard
     * backView = the back of the flashcard
     * topicFlashCards = a list of FlashCards
     * index = index to be track as the user iterate through
     *          the list as well keep track as this class interacts
     *          with other activities
     */
    private TextView frontView;
    private TextView backView;
    private ArrayList<FlashCard> topicFlashcards;
    private int index = 0;

    /**
     * Method onCreate is called on creation to set
     * the view of the flashcard and call the proper methods
     * @param savedInstanceState = Default passed on the creation
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set the content view of the flashcard with the passed layout
        setContentView(R.layout.activity_display_flash_card);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //Get the index from the intent
        index = getIntent().getIntExtra("INDEX", 0);

        //Getting the list
        topicFlashcards =  (ArrayList<FlashCard>) getIntent().getSerializableExtra("MYLIST");

        //Call setCardText method to display the appropriate content on the flashcard
        setCardText();

        //Set the backside's text view to be invisible
        backView.setVisibility(View.INVISIBLE);
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
     * Updates the view on Restart
     */
    @Override
    protected void onRestart() {
        super.onRestart();

        //Use updateView method to refresh the page when viewed through back button
        updateView();
    }

    /**
    * Updates the view on Resume
    */
    @Override
    protected void onPostResume() {
        super.onPostResume();

        //Use updateView method to refresh the page when viewed through back button
        updateView();
    }

    /**
     * The Updates method reread the file
     * to get the latest updated information
     * before set the display
     */
    private void updateView() {

        //Since we are on FlashCardDisplay, set the context to this, and pass null for other context values
        FileHelper fileHelper = new FileHelper(this);

        //Load the flashcards from the file
        topicFlashcards = fileHelper.getFlashCardMap().get(topicFlashcards.get(0).getTopic());

        //Call setCardText again to get the right values
        setCardText();
    }

    /**
     * Set the view by changing the content
     * that interacts with the user this
     * method is called every time there is a need
     * to display or the user flashcards
     */
    private void setCardText() {

        // grab all views by ID on create
        frontView = findViewById(R.id.frontTextView);
        backView = findViewById(R.id.backTextView);

        // set the flashcard front to show "front" and the flashcard back to show "back"
        frontView.setText(topicFlashcards.get(index).getFront());
        backView.setText(topicFlashcards.get(index).getBack());
    }

    /**
     * The .xml file is modified to use this view.
     * Used to "flip" the card from front to back/back to front
     * @param view used in android to interact with the UI
     */
    public void performAction(View view)
    {
        //Sets back to invisible and front to visible
        if (View.INVISIBLE == frontView.getVisibility()){

            backView.setVisibility(View.INVISIBLE);
            frontView.setVisibility(View.VISIBLE);

        } else { //Sets front to invisible and back to visible

            frontView.setVisibility(View.INVISIBLE);
            backView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * This method allows the user to iterate through
     * the List of flashcard as they see the front and the
     * back
     * @param view used in android to interact with the UI
     */
    public void goNextCard(View view) {

        /* if = the index is greater than 1 |
           else = you are at the end of the list, loop back to the start */
        if(!(index >= topicFlashcards.size() - 1 )) {

            //increment the index
            ++index;

            //call setCardText with the new incremented value of index to show the next card
            setCardText();

            //set the back invisible and front visible then show the front when you go to next card
            backView.setVisibility(View.INVISIBLE);
            frontView.setVisibility(View.VISIBLE);

        } else {

            //set the index back to 0 to start at the beginning of the list again
            index = 0;

            //setCardText with the new index value to show the first card in the list
            setCardText();

            //set the back invisible and front visible then show the front when you go to next card
            backView.setVisibility(View.INVISIBLE);
            frontView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Method for going to the previous card using the previous button
     * works the same as the goNextCard method except in reverse
     * @param view used in android to interact with the UI
     */
    public void goPreviousCard(View view) {

        /* if = the index is greater than 1 |
           else = you are at the end of the list, loop back to the start */
        if(!(index <= 0 )) {

            //Decrement the index
            --index;

            //setCardText with the new index value to show the first card in the list
            setCardText();

            //set the back invisible and front visible then show the front when you go to next card
            backView.setVisibility(View.INVISIBLE);
            frontView.setVisibility(View.VISIBLE);

        } else {

            //set the index to the last flashcard in the list
            index = topicFlashcards.size() - 1;

            //setCardText with the new index value to show the first card in the list
            setCardText();

            //set the back invisible and front visible then show the front when you go to next card
            backView.setVisibility(View.INVISIBLE);
            frontView.setVisibility(View.VISIBLE);
        }

    }

    /**
     * Delete method for delete button - removes the
     * object from the map for the current index.
     * Check the condition of the flashcard topic set
     * if it is empty it must remove the entire set from
     * the data in the file. If it is not on the last
     * must got the next flashcard on the list
     * @param view used in android to interact with the UI
     */
    @RequiresApi(api = Build.VERSION_CODES.N) //Required API
    public void deleteCard(View view) {

        //Remove the index
        if (topicFlashcards.size() == 1) {

            //Instantiate the reading
            FileHelper fileHelper = new FileHelper(this);

            //Converting the List into a Map
            Map<String, ArrayList<FlashCard>> fileData = fileHelper.getFlashCardMap();

            //remove the whole object
            fileData.remove(topicFlashcards.get(0).getTopic());

            //Update by saving to the file
            fileHelper.saveToFile(fileData);

            //Go back to main activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        } else  {
            topicFlashcards.remove(index);

            //Instantiate the reading
            FileHelper fileHelper = new FileHelper(this);

            //Converting the List into a Map
            Map<String, ArrayList<FlashCard>> fileData = fileHelper.getFlashCardMap();

            //Replace the object to the update one
            fileData.replace(topicFlashcards.get(0).getTopic(), topicFlashcards);
            fileHelper.saveToFile(fileData);

            //Call the goNextCard so that something else shows up after the card is deleted
            goNextCard(view);
        }
    }

    /**
     *  This method allows the user to edit the flash card
     *  by creating an intent and invoking the newFlashCard
     *  activity. However, this methods let the the activity
     *  know that it is coming from a edit so it does not create
     *  a new flashcard instead of editing the existing one.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void editCard(View view) {

        //get the topic, front, and back values
        String topic = topicFlashcards.get(index).getTopic();
        String front = topicFlashcards.get(index).getFront();
        String back = topicFlashcards.get(index).getBack();

        //create a new intent
        Intent intent = new Intent(this, NewFlashCard.class);

        //put the topic, front, back, current list, and index in the intent
        intent.putExtra("TOPIC", topic);
        intent.putExtra("FRONT", front);
        intent.putExtra("BACK", back);
        intent.putExtra("MYLIST", topicFlashcards);
        intent.putExtra("INDEX", index);

        //The boolean that indicates  that the activity is coming from an Edit functionality
        intent.putExtra("EDIT", true);

        //start the new activity
        startActivity(intent);
    }
}
