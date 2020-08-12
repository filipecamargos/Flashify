
package CS246.Team01.Flashify;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;

/**
 * This class will contain the actual Flash Card. Created with a Topic which will allow the app
 * to sort it, and a front and back of a card.
 *  The FlashCard class needs to implement "Parcelable" so the main activity can pass it
 *  to TopicActivity
 *  Parcelable is used to serialize a class so its properties can be transferred from one activity
 *  to another. Serialization is a mechanism of converting state of object into byte stream. To use it,
 *  Java class should be implemented using parcelable interface.
 */
public class FlashCard implements Parcelable, Serializable {

    /**
     * topic = Holds a string with the topic of the flashcard
     * front = Holds a string with the front of the flashcard
     * back = Holds a string with the back of the flashcard
     */
    private String topic;
    private String front;
    private  String back;

    /**
     * Constructor takes:
     * @param topic a string with the topic of the flashcard
     * @param front a string with the front of the flashcard
     * @param back a string with the back of the flashcard
     */
    FlashCard(String topic, String front, String back) {
        this.topic = topic;
        this.front = front;
        this.back = back;
    }

    /**
     * Get the topic
     * @return topic
     */
    public String getTopic() {
        return topic;
    }

    /**
     * Get front
     * @return return front
     */
    public String getFront() {
        return front;
    }

    /**
     * Get back
     * @return back
     */
    public String getBack() {
        return back;
    }

    /**
     * Convert the the object into a string
     * @return a string of the object data
     */
    @Override
    public String toString(){
        return "[" + topic + "," + front + "," + back + "]";
    }

    /**
     * Inside Constructor used
     * by the Parcel implementation
     * @param in Parcel from the implementations
     */
    private FlashCard(Parcel in) {
        topic = in.readString();
        front = in.readString();
        back = in.readString();
    }

    /**
     * Default implementation of Parcel Interface
     * @param dest
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(topic);
        dest.writeString(front);
        dest.writeString(back);
    }

    /**
     * Default implementation of Parcel Interface
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Default implementation of Parcel Interface
     */
    public static final Creator<FlashCard> CREATOR = new Creator<FlashCard>() {
        @Override
        public FlashCard createFromParcel(Parcel in) {
            return new FlashCard(in);
        }

        @Override
        public FlashCard[] newArray(int size) {
            return new FlashCard[size];
        }
    };
}
