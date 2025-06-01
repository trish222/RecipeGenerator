import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RecipeGenerator extends Application {

    private WebEngine engine;

    @Override
    public void start(Stage primaryStage) {
        WebView webView = new WebView();
        engine = webView.getEngine();
        engine.load(getClass().getResource("index.html").toExternalForm());

        // Connect JavaScript to Java
        JSObject window = (JSObject) engine.executeScript("window");
        window.setMember("javaApp", new JavaBridge());

        primaryStage.setScene(new Scene(webView, 800, 600));
        primaryStage.setTitle("Recipe Generator");
        primaryStage.show();
    }

    public class JavaBridge {
        private final String filePath = "files/data.txt";

        public void readFile() {
            try {
                String content = Files.readString(Paths.get(filePath));
                String safe = content.replace("'", "\\'").replace("\n", "\\n").replace("\r", "");
                RecipeGenerator.this.engine.executeScript("displayContent('" + safe + "')");
            } catch (IOException e) {
                RecipeGenerator.this.engine.executeScript("displayContent('Error: " + e.getMessage() + "')");
            }
        }

        public void writeFile(String content) {
            try {
                Files.writeString(Paths.get(filePath), content);
            } catch (IOException e) {
                RecipeGenerator.this.engine.executeScript("alert('Error saving file: " + e.getMessage() + "')");
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
