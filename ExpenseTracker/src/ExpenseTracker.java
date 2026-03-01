import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.*;

public class ExpenseTracker extends MIDlet implements CommandListener {
    private Display display;
    private TextBox pinInput;
    private Form mainForm;
    private TextField expenseField;
    private StringItem balanceDisplay;
    private Command exitCmd, saveCmd, addCmd;
    private int balance = 0;
    private RecordStore rs;

    // PIN for authentication (demo only - not secure for real apps!)
    private static final String CORRECT_PIN = "1234";

    public ExpenseTracker() {
        display = Display.getDisplay(this);
        
        // PIN Screen
        pinInput = new TextBox("Enter PIN", "", 4, TextField.NUMERIC);
        Command submitPin = new Command("OK", Command.OK, 1);
        pinInput.addCommand(submitPin);
        pinInput.setCommandListener(this);

        // Main Expense Form
        mainForm = new Form("Expense Tracker");
        expenseField = new TextField("Amount:", "", 10, TextField.NUMERIC);
        balanceDisplay = new StringItem("Balance:", "0");
        addCmd = new Command("Add", Command.OK, 1);
        exitCmd = new Command("Exit", Command.EXIT, 2);
        
        mainForm.append(expenseField);
        mainForm.append(balanceDisplay);
        mainForm.addCommand(addCmd);
        mainForm.addCommand(exitCmd);
        mainForm.setCommandListener(this);

        // Initialize RecordStore for data persistence
        try {
            rs = RecordStore.openRecordStore("ExpenseDB", true);
            loadBalance();
        } catch (RecordStoreException rse) {
            alert("Error", "Cannot load data: " + rse.getMessage());
        }
    }

    private void loadBalance() {
        try {
            if (rs.getNumRecords() > 0) {
                byte[] data = rs.getRecord(1);
                balance = Integer.parseInt(new String(data));
                balanceDisplay.setText(String.valueOf(balance));
            }
        } catch (Exception e) {
            alert("Error", "Failed to load balance: " + e.getMessage());
        }
    }

    private void saveBalance() {
        try {
            byte[] data = String.valueOf(balance).getBytes();
            if (rs.getNumRecords() == 0) {
                rs.addRecord(data, 0, data.length);
            } else {
                rs.setRecord(1, data, 0, data.length);
            }
        } catch (Exception e) {
            alert("Error", "Failed to save: " + e.getMessage());
        }
    }

    public void startApp() {
        display.setCurrent(pinInput); // Start with PIN screen
    }

    public void pauseApp() {}

    public void destroyApp(boolean unconditional) {
        try {
            if (rs != null) rs.closeRecordStore();
        } catch (RecordStoreException rse) {}
    }

    public void commandAction(Command c, Displayable d) {
        if (d == pinInput && c.getCommandType() == Command.OK) {
            if (pinInput.getString().equals(CORRECT_PIN)) {
                display.setCurrent(mainForm); // PIN correct → show main app
            } else {
                alert("Wrong PIN", "Access Denied");
                notifyDestroyed(); // Quit on wrong PIN
            }
        } 
        else if (d == mainForm) {
            if (c == exitCmd) {
                destroyApp(false);
                notifyDestroyed();
            } 
            else if (c == addCmd) {
                try {
                    int amount = Integer.parseInt(expenseField.getString());
                    balance += amount;
                    balanceDisplay.setText(String.valueOf(balance));
                    expenseField.setString("");
                    saveBalance();
                } catch (Exception e) {
                    alert("Error", "Invalid amount!");
                }
            }
        }
    }

    private void alert(String title, String msg) {
        Alert a = new Alert(title, msg, null, AlertType.ERROR);
        display.setCurrent(a);
    }
}
