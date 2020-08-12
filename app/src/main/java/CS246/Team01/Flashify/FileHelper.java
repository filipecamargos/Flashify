package CS246.Team01.Flashify;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This Class is responsible for reading data from the
 * file that is stored in the phone, This must be instantiated
 * when used. It gives access List of Topics and a Map of flashcards
 * containing key = topic and a value = List of object (FlashCard)
 */
public class FileHelper extends AppCompatActivity {

    /**
     * FlashCardMap = A Map with a key = topic and a value = List of object (FlashCard)
     * topicsMenu = A List of topics or keys
     * filePath = the path to a file where the data is stored
     */
    private Map<String, ArrayList<FlashCard>> flashCardMap = new HashMap<>();
    private ArrayList<String> topicsMenu = new ArrayList<>();
    private File filePath;

    /**
     * This Method is responsible for reading the data from
     * the file and converting them to the FlashCardMap and
     * Topic List
     * @param fileUserClass = Every class that uses the file
     *                      inherit from AppCompatActivity
     *                      so, they make the same path construction
     */
    public FileHelper(AppCompatActivity fileUserClass) {

        //Build the file path
        filePath = fileUserClass.getBaseContext().getFilesDir();

        try {

            //Load Flash Card list from memory
            File file = new File(filePath,  "flashCards.dat");

            // Check whether the file can be read or not
            if (file.canRead()) {
                try {

                    // A fileInputStream is necessary to read the file
                    FileInputStream in = new FileInputStream(file);

                    // In order to read the file an ObjectInputStream is also necessary because the file contents an object (the map).
                    ObjectInputStream ois = new ObjectInputStream(in);

                    flashCardMap = (Map<String, ArrayList<FlashCard>>) ois.readObject();

                    //Set the list of NewFlashcard
                    NewFlashCard.setFlashCardList(flashCardMap);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                //Loop through the keys and create a topic list
                for (Object key : flashCardMap.keySet()) {
                    topicsMenu.add(key.toString());
                }
            }
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /**
     * Gives external access to the flashCardMap
     * @return a Map with a topicKey and a list of FlashCard values
     */
    public Map<String, ArrayList<FlashCard>> getFlashCardMap() {
        return flashCardMap;
    }

    /**
     * Gives external access to a list of topics
     * @return a list of flashcard topics
     */
    public ArrayList<String> getTopicsMenu() {
        return topicsMenu;
    }

    /**
     * This method will be responsible for saving the data
     * to the file
     * @param flashCardList = Is the map that needs to be saved!
     */
    public void saveToFile(Map<String, ArrayList<FlashCard>> flashCardList) {

        /**
         * Creates a file in the application path obtained from
         * the application context Android takes care of the context
         */
        File file = new File(filePath,  "flashCards.dat");

        try {
            FileOutputStream out = new FileOutputStream(file, false);
            ObjectOutputStream oout = new ObjectOutputStream(out);

            // Write the whole flashcard map in the file
            oout.writeObject(flashCardList);
            oout.flush();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * There was a need to convert the map to
     * a list - this method provides that
     * functionality
     * @param receivedMap get the map that needs to be converted to a list
     * @return the list of flashcards
     */
    public ArrayList<FlashCard> convertToList(Map<String, ArrayList<FlashCard>> receivedMap) {

        ArrayList<FlashCard> topicFlashcards = new ArrayList<>();

        // Loop through the map and set the list
        for (Map.Entry<String, ArrayList<FlashCard>> entry : receivedMap.entrySet()) {
            topicFlashcards = entry.getValue();
        }
        return topicFlashcards;
    }
}