package com.drzed.pathofoil;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.io.*;
import java.util.HashMap;

public class POO extends Application {
    public static final String[] oils = new String[]{"Clear Oil", "Sepia Oil", "Amber Oil", "Verdant Oil", "Teal Oil", "Azure Oil", "Indigo Oil", "Violet Oil", "Crimson Oil", "Black Oil", "Opalescent Oil", "Silver Oil", "Golden Oil"};
    public static Image[] oilImages;
    public static HashMap<String, String> anointments_masterlist = new HashMap<>();
    public static String CurrentItem;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(POO.class.getResource("scene.fxml"));
        Parent root = fxmlLoader.load();
        MainController controller = fxmlLoader.getController();
        controller.stage = stage;
        Scene scene = new Scene(root, 214, 100);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Path Of Oil");
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        // Pre-loads images and keeps them in RAM for instant responsiveness (not really necessary because size but who cares)
        oilImages = new Image[]{
                new Image("file:data/imgs/Clear Oil.png"),
                new Image("file:data/imgs/Sepia Oil.png"),
                new Image("file:data/imgs/Amber Oil.png"),
                new Image("file:data/imgs/Verdant Oil.png"),
                new Image("file:data/imgs/Teal Oil.png"),
                new Image("file:data/imgs/Azure Oil.png"),
                new Image("file:data/imgs/Indigo Oil.png"),
                new Image("file:data/imgs/Violet Oil.png"),
                new Image("file:data/imgs/Crimson Oil.png"),
                new Image("file:data/imgs/Black Oil.png"),
                new Image("file:data/imgs/Opalescent Oil.png"),
                new Image("file:data/imgs/Silver Oil.png"),
                new Image("file:data/imgs/Golden Oil.png")
        };
        stage.show();
        new Thread(() -> {
            while (true) {
                try {
                    // Grab Clipboard Contents
                    String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);

                    // Only do performant work when clipboard contains enchanted item
                    // Not Null before data.contains to avoid NPE
                    if (data != null && data.contains("(enchant)")) {
                        // Split New Lines to specifically grab the enchant tagged line
                        for (String s : data.split("\\r?\\n|\\r")) {
                            if (s.contains("(enchant)")) {
                                // Remove (enchant) and Allocates so it's easier to grab from automatically populated data
                                String nstr = s.replace("(enchant)", "").trim().replace("Allocates", "").trim();
                                // Only update the window when a different item is copied
                                if (!nstr.equals(CurrentItem)) {
                                    CurrentItem = nstr;
                                    Platform.runLater(controller::updatePictures);
                                }
                            }
                        }
                    }
                    // Sleep exactly 10 frames (60 fps is 16.7ms per frame)
                    // In all reality this is to save CPU and has no noticeable delay of CTRL+C -> display oils
                    Thread.sleep(167);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void main(String[] args) throws IOException {
        initializeData();
        launch();
    }

    private static void initializeData() throws IOException {
        // Loads pre-populated and trimmed spreadsheets of oils and their associated anoint
        File anoints = new File("./data/Amulets.csv");

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(anoints)));
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            String[] parts = line.split(",");
            // Ignore the header line of the spreadsheet IE (Oil #1, Oil #2, Oil #3, Enchantment, Description)
            if (!line.contains("#")) {
                // 3 oils of amulet anoints
                String ze_oils = parts[0] + ", " + parts[1] + ", " + parts[2];
                String name = parts[3].replace("Allocates", "").trim();
                // Was going to have it display what the anoint does but the window would need to be larger than I'd like
//                String descriptor = line.split(name)[1].replaceFirst(",", "");
                anointments_masterlist.put(name, ze_oils /*+ " || " + descriptor*/);
            }
        }
        reader.close();
        anoints = new File("./data/Rings.csv");

        reader = new BufferedReader(new InputStreamReader(new FileInputStream(anoints)));
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            String[] parts = line.split(",");
            if (!line.contains("#")) {
                // 2 oils of ring anoints
                String ze_oils = parts[0] + ", " + parts[1];
                String name = parts[2];
                anointments_masterlist.put(name, ze_oils /*+ " || " + descriptor*/);
            }
        }
        reader.close();
    }

    public static Image getOilImage(String oil) {
        oil = oil.trim();
        for (int i = 0; i < oils.length; i++) {
            if (oils[i].equals(oil)) {
                return oilImages[i];
            }
        }
        return oilImages[0];
    }
}