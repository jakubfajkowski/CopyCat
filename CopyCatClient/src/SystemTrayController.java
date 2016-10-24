/*
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

*/

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by FajQa on 21.10.2016.
 */

public class SystemTrayController extends Controller {
    private TrayIcon trayIcon;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        createTrayIcon(Main.primaryStage);
        Platform.setImplicitExit(false);
    }


    public void createTrayIcon(final Stage stage) {
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            java.awt.Image image = null;
            try {
                URL url = new URL("http://icons.iconarchive.com/icons/graphicloads/colorful-long-shadow/256/Home-icon.png");
                image = ImageIO.read(url);
            } catch (IOException ex) {
                System.out.println(ex);
            }


            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent t) {
                    hide(stage);
                }
            });
            final ActionListener closeListener = new ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    System.exit(0);
                }
            };

            ActionListener showListener = new ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            stage.show();
                        }
                    });
                }
            };
            PopupMenu popup = new PopupMenu();

            MenuItem showItem = new MenuItem("Show");
            showItem.addActionListener(showListener);
            popup.add(showItem);

            MenuItem closeItem = new MenuItem("Close");
            closeItem.addActionListener(closeListener);
            popup.add(closeItem);

            trayIcon = new TrayIcon(image, "Title", popup);
            trayIcon.addActionListener(showListener);
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.err.println(e);
            }
        }
    }

    /*public void showProgramIsMinimizedMsg() {
        if (firstTime) {
            trayIcon.displayMessage("Some message.",
                    "Some other message.",
                    TrayIcon.MessageType.INFO);
            firstTime = false;
        }
    }*/

    private void hide(final Stage stage) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (SystemTray.isSupported()) {
                    stage.hide();
                    //showProgramIsMinimizedMsg();
                } else {
                    Platform.exit();
                }
            }
        });
    }
}
