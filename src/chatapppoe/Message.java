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

    public boolean checkMessageID(String messageID) {
        return messageID.length() <= 10;
    }

    public String checkRecipientCell(String cellNumber) {
        if (cellNumber.startsWith("+") && cellNumber.length() <= 13) {
            return "Cell phone number successfully captured.";
        } else {
            return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        }
    }

    public String createMessageHash(String messageID, int messageNumber, String message) {
        String[] words = message.split(" ");
        String firstWord = words[0].replaceAll("[^a-zA-Z]", "");
        String lastWord = words[words.length - 1].replaceAll("[^a-zA-Z]", "");
        String start = messageID.substring(0, 2);

        String hash = start + ":" + messageNumber + ":" + firstWord + lastWord;
        return hash.toUpperCase();
    }

    public String generateMessageID() {
        Random random = new Random();
        String id = "";
        for (int i = 0; i < 10; i++) {
            id = id + random.nextInt(10);
        }
        return id;
    }

    public String checkMessageLength(String message) {
        if (message.length() <= 250) {
            return "Message ready to send.";
        } else {
            int extra = message.length() - 250;
            return "Message exceeds 250 characters by " + extra + ", please reduce the size.";
        }
    }

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

    public int returnTotalMessages() {
        return totalMessages;
    }

    public String printMessages() {
        return sentLog;
    }
    public void recordDisregarded(String messageID, String hash, String text) {
    disregardedMessages.add(text);
}

public void recordStored(String messageID, String hash, String recipient, String text) {
    storedMessages.add(text);
    storedRecipients.add(recipient);
    storedIDs.add(messageID);
    storedHashes.add(hash);
    messageHashes.add(hash);
    messageIDs.add(messageID);
}
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
public void displayStoredSenderRecipient(String sender) {
    if (storedMessages.isEmpty()) {
        System.out.println("No stored messages.");
        return;
    }
    for (int i = 0; i < storedMessages.size(); i++) {
        System.out.println("Sender: " + sender + " | Recipient: " + storedRecipients.get(i));
    }
}
public String getLongestMessage() {
    String longest = "";
    for (int i = 0; i < storedMessages.size(); i++) {
        if (storedMessages.get(i).length() > longest.length()) {
            longest = storedMessages.get(i);
        }
    }
    return longest;
} 
 public String searchByMessageID(String id) {
    for (int i = 0; i < storedIDs.size(); i++) {
        if (storedIDs.get(i).equals(id)) {
            return "Recipient: " + storedRecipients.get(i) + " | Message: " + storedMessages.get(i);
        }
    }
    return "Message ID not found.";
}
}