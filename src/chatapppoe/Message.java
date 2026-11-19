package chatapppoe;

import java.util.Random;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Khanyi
 */

/**
 * Handles everything to do with messages: checking recipient numbers, message IDs
 * and lengths, creating hashes, and storing messages in the sent, disregarded and
 * stored arrays. It also reads stored messages from messages.json and supports
 * searching, deleting and reporting.
 */
public class Message {

    private int totalMessages = 0;
   
    private ArrayList<String> storedIDs = new ArrayList<>();
private ArrayList<String> storedHashes = new ArrayList<>();
    private ArrayList<String> sentMessages = new ArrayList<>();
private ArrayList<String> disregardedMessages = new ArrayList<>();
private ArrayList<String> storedMessages = new ArrayList<>();
private ArrayList<String> messageHashes = new ArrayList<>();
private ArrayList<String> messageIDs = new ArrayList<>();
private ArrayList<String> storedRecipients = new ArrayList<>();

    private String sentLog = "";

    /** Returns true if the message ID is 10 characters or fewer. */
    public boolean checkMessageID(String messageID) {
        return messageID.length() <= 10;
    }

    /** Checks the number starts with "+" (international code) and is at most 13 characters. */ 
    public String checkRecipientCell(String cellNumber) {
        if (cellNumber.startsWith("+") && cellNumber.length() <= 13) {
            return "Cell phone number successfully captured.";
        } else {
            return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        }
    }

   /** Builds the hash: first 2 digits of the ID, message number, then first and last word, in capitals. */
    public String createMessageHash(String messageID, int messageNumber, String message) {
        String[] words = message.split(" ");
        String firstWord = words[0].replaceAll("[^a-zA-Z]", "");
        String lastWord = words[words.length - 1].replaceAll("[^a-zA-Z]", "");
        String start = messageID.substring(0, 2);

        String hash = start + ":" + messageNumber + ":" + firstWord + lastWord;
        return hash.toUpperCase();
    }

     /** Generates a random 10-digit message ID. */
    public String generateMessageID() {
        Random random = new Random();
        String id = "";
        for (int i = 0; i < 10; i++) {
            id = id + random.nextInt(10);
        }
        return id;
    }

    /** Checks the message is 250 characters or fewer; otherwise says how many characters are too many. */
    public String checkMessageLength(String message) {
        if (message.length() <= 250) {
            return "Message ready to send.";
        } else {
            int extra = message.length() - 250;
            return "Message exceeds 250 characters by " + extra + ", please reduce the size.";
        }
    }

     /** Records a sent message: adds its text, hash and ID to the arrays and adds it to the sent log. */    
    public void recordSent(String messageID, String hash, String recipient, String text) {
        totalMessages++;
        sentMessages.add(text);
        messageHashes.add(hash);
        messageIDs.add(messageID);
        sentLog = sentLog + "Message ID: " + messageID + "\n"
                + "Message Hash: " + hash + "\n"
                + "Recipient: " + recipient + "\n"
                + "Message: " + text + "\n\n";
    }

    /** Returns how many messages have been sent. */
    public int returnTotalMessages() {
        return totalMessages;
    }

   /** Returns the log of all sent messages as text. */    
    public String printMessages() {
        return sentLog;
    }
    
    /** Records a disregarded message by adding its text to the disregarded array. */
    public void recordDisregarded(String messageID, String hash, String text) {
    disregardedMessages.add(text);
}

    /** Records a stored message: adds its text, recipient, ID and hash to the stored arrays. */
    public void recordStored(String messageID, String hash, String recipient, String text) {
    storedMessages.add(text);
    storedRecipients.add(recipient);
    storedIDs.add(messageID);
    storedHashes.add(hash);
    messageHashes.add(hash);
    messageIDs.add(messageID);
}
     /**
 * Reads messages.json one line at a time and fills the stored arrays
 * (ID, hash, recipient and message). Each line holds one message.
 *
 * Attribution: the BufferedReader/FileReader approach for reading a text file
 * line by line follows the standard Java I/O documentation (Oracle Java Docs).
 * This method was written with help from Claude (Anthropic's AI assistant),
 * November 2026, and adapted to this project's file format.
 */
    public void loadStoredFromJson(String filePath) {
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) {
                continue;
            }
            messageIDs.add(getValue(line, "messageID"));
            messageHashes.add(getValue(line, "messageHash"));
            storedRecipients.add(getValue(line, "recipient"));
            storedMessages.add(getValue(line, "message"));
            storedIDs.add(getValue(line, "messageID"));
            storedHashes.add(getValue(line, "messageHash"));
        }
    } catch (IOException e) {
        System.out.println("Could not read messages file: " + e.getMessage());
    }
}

   /** Finds the value for the given key in one JSON line, e.g. the text after "recipient": " up to the closing quote. */
 private String getValue(String line, String key) {
    String marker = "\"" + key + "\": \"";
    int start = line.indexOf(marker);
    if (start == -1) {
        return "";
    }
    start += marker.length();
    int end = line.indexOf("\"", start);
    return line.substring(start, end);
}
   /** Displays the sender and recipient of every stored message. */
public void displayStoredSenderRecipient(String sender) {
    if (storedMessages.isEmpty()) {
        System.out.println("No stored messages.");
        return;
    }
    for (int i = 0; i < storedMessages.size(); i++) {
        System.out.println("Sender: " + sender + " | Recipient: " + storedRecipients.get(i));
    }
}
     /** Returns the longest stored message, or an empty string if there are none. */
public String getLongestMessage() {
    String longest = "";
    for (int i = 0; i < storedMessages.size(); i++) {
        if (storedMessages.get(i).length() > longest.length()) {
            longest = storedMessages.get(i);
        }
    }
    return longest;
} 
   /** Searches the stored IDs and returns the matching recipient and message, or "Message ID not found." */ 
public String searchByMessageID(String id) {
    for (int i = 0; i < storedIDs.size(); i++) {
        if (storedIDs.get(i).equals(id)) {
            return "Recipient: " + storedRecipients.get(i) + " | Message: " + storedMessages.get(i);
        }
    }
    return "Message ID not found.";
}
  
   /** Returns a list of all stored messages sent to the given recipient. */
public ArrayList<String> searchByRecipient(String recipient) {
    ArrayList<String> results = new ArrayList<>();
    for (int i = 0; i < storedRecipients.size(); i++) {
        if (storedRecipients.get(i).equals(recipient)) {
            results.add(storedMessages.get(i));
        }
    }
    return results;
}
     /** Finds the stored message with the given hash and removes it from the stored arrays. */    
public String deleteByHash(String hash) {
    for (int i = 0; i < storedHashes.size(); i++) {
        if (storedHashes.get(i).equals(hash)) {
            String deleted = storedMessages.get(i);
            storedMessages.remove(i);
            storedRecipients.remove(i);
            storedIDs.remove(i);
            storedHashes.remove(i);
            return "Message: \"" + deleted + "\" successfully deleted.";
        }
    }
    return "Message hash not found.";
}
         /** Builds a report with the hash, ID, recipient and message of every stored message. */
   public String getStoredReport() {
    if (storedMessages.isEmpty()) {
        return "No stored messages.";
    }
    String report = "";
    for (int i = 0; i < storedMessages.size(); i++) {
        report = report + "Message Hash: " + storedHashes.get(i) + "\n"
                + "Message ID: " + storedIDs.get(i) + "\n"
                + "Recipient: " + storedRecipients.get(i) + "\n"
                + "Message: " + storedMessages.get(i) + "\n\n";
    }
    return report;
}
       /** Returns the sent messages array (used by the unit tests). */
       public ArrayList<String> getSentMessages() {
    return sentMessages;
}
}