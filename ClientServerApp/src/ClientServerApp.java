import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.io.*;
import java.io.*;

public class ClientServerApp extends MIDlet implements CommandListener {
    private Display display;
    private Form form;
    private StringItem responseItem;
    private Command exitCommand, sendCommand;
    private String serverUrl = "http://localhost:8080/message"; // Local server URL

    public ClientServerApp() {
        display = Display.getDisplay(this);
        form = new Form("Client-Server Demo");
        responseItem = new StringItem("Server Response:", "Press 'Send' to connect");
        exitCommand = new Command("Exit", Command.EXIT, 1);
        sendCommand = new Command("Send", Command.SCREEN, 2);
        
        form.append(responseItem);
        form.addCommand(sendCommand);
        form.addCommand(exitCommand);
        form.setCommandListener(this);
    }

    protected void startApp() {
        display.setCurrent(form);
    }

    private void sendRequest() {
        new Thread(new Runnable() {
            public void run() {
                final StringBuffer sb = new StringBuffer(); // Declare as final
                try {
                    HttpConnection conn = (HttpConnection) Connector.open(serverUrl);
                    conn.setRequestMethod(HttpConnection.GET);
                    
                    // Read response
                    InputStream is = conn.openInputStream();
                    int ch;
                    while ((ch = is.read()) != -1) sb.append((char) ch);
                    
                    // Update UI on the EDT
                    display.callSerially(new Runnable() {
                        public void run() {
                            responseItem.setText("Response: " + sb.toString());
                        }
                    });
                    
                    is.close();
                    conn.close();
                } catch (Exception e) {
                    final Exception finalException = e; // Declare as final
                    display.callSerially(new Runnable() {
                        public void run() {
                            responseItem.setText("Error: " + finalException.getMessage());
                        }
                    });
                }
            }
        }).start();
    }

    public void commandAction(Command c, Displayable d) {
        if (c == exitCommand) {
            destroyApp(false);
            notifyDestroyed();
        } else if (c == sendCommand) {
            sendRequest();
        }
    }

    protected void pauseApp() {}
    protected void destroyApp(boolean unconditional) {}
}
