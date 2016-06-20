
package librarysystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javax.swing.JOptionPane;


public class LibrarySystem extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        
        Scene scene = new Scene(root);
       
        stage.setScene(scene);
        stage.setTitle("Library");
        stage.setWidth(500);
       
       stage.getIcons().add(new Image("librarysystem/icon.png"));

        stage.show();
        
        stage.setOnCloseRequest(e ->{
            e.consume();
           int value = JOptionPane.showConfirmDialog(null, "Are You Sure you Want to Quit?");
            if (value==JOptionPane.YES_OPTION){
                System.exit(0);
               
            }
        
            
        });
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
