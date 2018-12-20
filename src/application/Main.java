package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Created by ehay@naver.com on 2018-12-15
 * Blog : http://ehay.tistory.com
 * Github : http://github.com/ehayand
 */

public class Main extends Application {

    private static TextArea textArea;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Controller controller = new Controller();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(5));

        textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("나눔고딕", 15));
        root.setCenter(textArea);

        Button toggleButton = new Button("시작하기");
        toggleButton.setMaxWidth(Double.MAX_VALUE);
        BorderPane.setMargin(toggleButton, new Insets(1, 0, 0, 0));
        root.setBottom(toggleButton);

        String IP = "127.0.0.1";
        int port = 9876;

        toggleButton.setOnAction(event -> {
            if ("시작하기".equals(toggleButton.getText())) {
                controller.startServer(IP, port);
                Platform.runLater(() -> {
                    String message = String.format("[서버 시작]\n", IP, port);
                    textArea.appendText(message);
                    toggleButton.setText("종료하기");
                });
            } else {
                controller.stopServer();
                Platform.runLater(() -> {
                    String message = String.format("[서버 종료]\n", IP, port);
                    textArea.appendText(message);
                    toggleButton.setText("시작하기");
                });
            }
        });

        Scene scene = new Scene(root, 400, 400);
        primaryStage.setTitle("[채팅 서버]");
        primaryStage.setOnCloseRequest(event -> controller.stopServer());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void receive(String message) {
        try {
            Platform.runLater(() -> {
                textArea.appendText(message);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
