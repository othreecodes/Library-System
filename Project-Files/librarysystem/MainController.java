/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package librarysystem;

import data.Book;
import data.BorrowedBooks;
import data.BorrowedProjects;
import data.Projects;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.AudioClip;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.util.Pair;
import javafx.util.converter.LocalDateStringConverter;
import javax.swing.JOptionPane;
import org.controlsfx.control.Notifications;
import org.controlsfx.dialog.LoginDialog;
import org.controlsfx.dialog.ProgressDialog;

/**
 *
 * @author UCHENNA
 */
public class MainController implements Initializable {

    //Declaration Of Objects Beginning
    @FXML
    private TextField userId;
    @FXML
    private TextField password;
    @FXML
    private TextField isbnTextField;
    @FXML
    private TextField authorTextField;
    @FXML
    private TextField titleTextField;
    @FXML
    private TextField publisherTextField;
    @FXML
    private TextField quantityTextField;
    @FXML
    private TextField categoryTextField;
    @FXML
    private TextField searchTextField;
    @FXML
    private TableView<Book> libraryTable;
    @FXML
    private TableColumn<Book, String> snColumn;
    @FXML
    private TableColumn<Book, String> isbnColumn;
    @FXML
    private TableColumn<Book, String> titleColumn;
    @FXML
    private TableColumn<Book, String> authorColumn;
    @FXML
    private TableColumn<Book, String> publisherColumn;
    @FXML
    private TableColumn<Book, String> quantityColumn;
    @FXML
    private TableColumn<Book, String> c;
    @FXML
    private TableColumn<Book, String> categoryColumn;
    @FXML
    private TextField borrowerName;
    @FXML
    private TextField borrowerHall;
    @FXML
    private TextField borrowerNumber;
    @FXML
    private ComboBox<String> selectBook;
    @FXML
    private DatePicker selectDate;
    @FXML
    private TableColumn<BorrowedBooks, String> bname;
    @FXML
    private TableColumn<BorrowedBooks, String> bhall;
    @FXML
    private TableColumn<BorrowedBooks, String> bnumber;
    @FXML
    private TableColumn<BorrowedBooks, String> bbook;
    @FXML
    private TableColumn<BorrowedBooks, String> bdate;
    @FXML
    private TableColumn<BorrowedBooks, String> bdue;
    @FXML
    private TableColumn<BorrowedBooks, Button> returned;
    @FXML
    private TableView<BorrowedBooks> borrowerTable;
    @FXML
    private ListView<String> duebooksListView;
    //database Parameters
    private Connection con;
    private ResultSet rs;
    private PreparedStatement pt;
    //end of database parameters
    private boolean firstTime;
    private AudioClip pop;
    private ObservableList data;
    String logedInUser;

    @FXML
    private void handleButtonAction(ActionEvent event) {
        String usernameLogin = userId.getText();
        String passwordLogin = password.getText();
        boolean isUser = false;
        Notifications formInfo = Notifications.create();

        formInfo.title("Incomplete Form");
        formInfo.hideAfter(Duration.seconds(3));
        formInfo.position(Pos.TOP_RIGHT);

        if (userId.getText().isEmpty()) {
            formInfo.text("Please Enter A Username");
            pop.play();
            formInfo.showWarning();

        } else if (password.getText().isEmpty()) {
            formInfo.text("Please Input Your Password");
            pop.play();
            formInfo.showWarning();
        } else {

            isUser = doLogin(usernameLogin, passwordLogin);

        }
        if (isUser == true) {

            formInfo.title("SUCCESS");
            formInfo.text("Welcome" + " " + userId.getText());
            pop.play();
            formInfo.showInformation();
            authenticate();
            logedInUser = usernameLogin;
        } else {
            formInfo.title("ERROR");
            formInfo.text("INVALID USER DETAILS");
            pop.play();
            formInfo.showError();

        }

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        pop = new AudioClip(MainController.class.getResource("pop.wav").toString());
        try {

            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }

        checkFirstTime();

    }

    //Checks if This is the first Time That The Admin Uses The App
    private void checkFirstTime() {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);

            createStorage();
        } finally {
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    //Creates the DataBase for First time use
    private void createStorage() {
        try {
            try {
                try {
                    try {
                        try {
                            try {
                                try {
                                    //Creates The database named library
                                    con = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "");
                                    pt = con.prepareStatement("CREATE DATABASE library");
                                    pt.execute();

                                } catch (SQLException ex) {
                                    Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                                } finally {
                                    try {
                                        pt.close();
                                        con.close();
                                        //close connections to database when not in use to prevent Program Lagging
                                    } catch (SQLException ex) {
                                        Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                                //creates Tables In DataBase
                                con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
                                pt = con.prepareStatement("CREATE TABLE `user` ( `id` INT NOT NULL AUTO_INCREMENT , `username` VARCHAR(1000) NOT NULL , `password` VARCHAR(1000) NOT NULL , PRIMARY KEY (`id`))");
                                pt.execute();

                            } catch (SQLException ex) {
                                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                            } finally {
                                try {
                                    pt.close();
                                    con.close();
                                } catch (SQLException ex) {
                                    Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }

                            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
                            pt = con.prepareStatement("INSERT INTO `user` (`username`, `password`) VALUES ('user', 'password')");
                            pt.execute();

                        } catch (SQLException ex) {
                            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                        } finally {
                            try {
                                pt.close();
                                con.close();
                            } catch (SQLException ex) {
                                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
                        pt = con.prepareStatement("CREATE TABLE `books` ( `id` INT(11) NOT NULL AUTO_INCREMENT , `isbn` VARCHAR(1000) NOT NULL , `title` VARCHAR(1000) NOT NULL , `author` VARCHAR(1000) NOT NULL , `publisher` VARCHAR(1000) NOT NULL , `quantity` VARCHAR(1000) NOT NULL , `category` VARCHAR(1000) NOT NULL , `catalouge` VARCHAR(1000) NOT NULL , PRIMARY KEY (`id`))");
                        pt.execute();

                    } catch (SQLException ex) {
                        Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        try {
                            pt.close();
                            con.close();
                        } catch (SQLException ex) {
                            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                    con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
                    pt = con.prepareStatement("CREATE TABLE `borrowedbooks` ( `id` INT(11) NOT NULL AUTO_INCREMENT , `name` VARCHAR(1000) NOT NULL , `hall` VARCHAR(1000) NOT NULL , `number` VARCHAR(1000) NOT NULL , `book` VARCHAR(1000) NOT NULL , `borrow_date` VARCHAR(1000) NOT NULL , `return_date` VARCHAR(1000) NOT NULL , PRIMARY KEY (`id`))");
                    pt.execute();

                } catch (SQLException ex) {
                    Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        pt.close();
                        con.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
                pt = con.prepareStatement("CREATE TABLE `library`.`projects` ( `id` INT(11) NOT NULL AUTO_INCREMENT , `matric` VARCHAR(1000) NOT NULL , `title` VARCHAR(1000) NOT NULL , `owner` VARCHAR(1000) NOT NULL , `supervisor` VARCHAR(1000) NOT NULL , `year` VARCHAR(1000) NOT NULL , PRIMARY KEY (`id`))");
                pt.execute();

            } catch (SQLException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    pt.close();
                    con.close();
                } catch (SQLException ex) {
                    Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
            pt = con.prepareStatement("CREATE TABLE `borrowedprojects` ( `id` INT(11) NOT NULL AUTO_INCREMENT , `name` VARCHAR(1000) NOT NULL , `hall` VARCHAR(1000) NOT NULL , `number` VARCHAR(1000) NOT NULL , `project` VARCHAR(1000) NOT NULL , `borrow_date` VARCHAR(1000) NOT NULL , `return_date` VARCHAR(1000) NOT NULL , PRIMARY KEY (`id`))");
            pt.execute();

        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                pt.close();
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    //Performing The Login Authentication
    private boolean doLogin(String usernameLogin, String passwordLogin) {
        boolean isUser = false;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
            pt = con.prepareStatement("SELECT * FROM user WHERE username = ? AND password = ?");
            pt.setString(1, usernameLogin);
            pt.setString(2, passwordLogin);
            rs = pt.executeQuery();

            if (rs.next()) {
                isUser = true;
            } else {
                isUser = false;
            }

        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return isUser;

    }

    private void authenticate() {
        try {
            Stage stage;
            Parent root;

            stage = (Stage) password.getScene().getWindow();

            root = FXMLLoader.load(getClass().getResource("select.fxml"));
            Scene scene = new Scene(root);

            stage.hide();
            stage.setHeight(400);
            stage.setWidth(600);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.setResizable(false);

            stage.show();

        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void backSelect(ActionEvent e) {

        try {
            Stage stage;
            Parent root;

            stage = (Stage) libraryTable.getScene().getWindow();

            root = FXMLLoader.load(getClass().getResource("select.fxml"));
            Scene scene = new Scene(root);

            stage.hide();
            stage.setHeight(400);
            stage.setWidth(600);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.setResizable(false);

            stage.show();

        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    @FXML
    private Button but;

    @FXML
    private void authenticateBook(ActionEvent e) {

        try {
            Stage stage;
            Parent root;

            stage = (Stage) but.getScene().getWindow();

            root = FXMLLoader.load(getClass().getResource("Home.fxml"));
            Scene scene = new Scene(root);

            stage.hide();
            stage.setHeight(Screen.getPrimary().getVisualBounds().getHeight());
            stage.setWidth(Screen.getPrimary().getVisualBounds().getWidth());
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.setResizable(false);

            stage.show();

        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    void init(ActionEvent e) {

        libraryTable.setEffect(new InnerShadow());

        snColumn.setCellValueFactory(new PropertyValueFactory<>("posOnTAB"));
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("ISBN"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        c.setCellValueFactory(new PropertyValueFactory<>("c"));
        c.setEditable(true);

        loadRecordOnTable();

    }

    private void loadRecordOnTable() {
        try {
            int o = 1;
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
            pt = con.prepareStatement("select * from books");
            data = FXCollections.observableArrayList();

            rs = pt.executeQuery();

            while (rs.next()) {

                Book row = new Book();

                row.setSn(rs.getInt(1));
                row.setPosOnTAB(o);
                row.setISBN(rs.getString(2));
                row.setTitle(rs.getString(3));

                row.setAuthor(rs.getString(4));
                row.setPublisher(rs.getString(5));
                row.setQuantity(rs.getString(6));
                row.setCategory(rs.getString(7));

                o++;

                data.add(row);

            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        libraryTable.setItems(data);
        loadCombobox();

    }
    int starter = 0;

    public void begin(MouseEvent e) {

        if (starter == 0) {
            init(null);
            starter++;
            searchTextField.textProperty().addListener(t -> {

                filter(searchTextField.getText());
            });
        }
    }

    @FXML
    public void addBook(ActionEvent e) {
        String isbn = isbnTextField.getText();
        String author = authorTextField.getText();
        String title = titleTextField.getText();
        String publisher = publisherTextField.getText();
        String quantity = quantityTextField.getText();
        String category = categoryTextField.getText();

        new Thread(new Runnable() {

            @Override
            public void run() {
                newBook(isbn, title, author, publisher, quantity, category);

            }
        }).start();

        isbnTextField.clear();
        authorTextField.clear();
        titleTextField.clear();
        publisherTextField.clear();
        quantityTextField.clear();
        categoryTextField.clear();

    }

    public void newBook(String isbn, String title, String author, String publisher, String quantity, String category) {
        boolean exist = false;
        try {
            String previous = quantity;
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
            pt = con.prepareStatement("select * from books where isbn = ?");
            pt.setString(1, isbn);
            rs = pt.executeQuery();
            if (rs.next()) {
                String next = rs.getString("quantity");
                // increaseBookNumber(isbn, previous, next);

            }
        } catch (SQLException ex) {

        } finally {
            try {
                rs.close();
                pt.close();
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
            pt = con.prepareStatement("INSERT INTO `books` (`isbn`, `title`, `author`, `publisher`, `quantity`, `category`, `catalouge`) VALUES ( ?, ?, ?, ?, ?, ?, ?)");
            pt.setString(1, isbn);
            pt.setString(2, title);
            pt.setString(3, author);
            pt.setString(4, publisher);
            pt.setString(5, quantity);
            pt.setString(6, category);
            pt.setString(7, "-");
            int value = 0;
            if (exist == false) {
                value = pt.executeUpdate();
            }

            if (value >= 1) {
                

            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        init(null);
    }

    public void importData(ActionEvent e) {
        FileChooser f = new FileChooser();
        f.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV (MS-DOS)", "*.csv"));
        f.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("CSV (MS-DOS)", "*.csv"));
        f.setInitialFileName("CSV (MS-DOS) (*.csv)");
        f.setTitle("Choose a CSV (MS-DOS) Document (*.csv)");
        File choose = f.showOpenDialog(null);

        if (!choose.getName().contains(".csv")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please choose a .csv file");
            alert.showAndWait();
            importData(null);
        }
        Task<Object> worker = new Task<Object>() {
            @Override
            protected Object call() throws Exception {

                ArrayList<String> list = new ArrayList<>();
                Scanner input = new Scanner(choose);

                while (input.hasNextLine()) {

                    list.add(input.nextLine());
                }

                for (int l = 1; l < list.size(); l++) {

                    String[] data = list.get(l).split(",");

                    newBook(data[1], data[2], data[3], data[4], data[5], data[6]);

                    System.out.println(l);
                    updateProgress(l, list.size());
                }
                return null;
            }
        };

        ProgressDialog dlg = new ProgressDialog(worker);
        dlg.setContentText("Importing Please Wait");
        Thread th = new Thread(worker);
        th.setDaemon(true);
        th.start();

    }

    private ObservableList<Book> filter;

    private void filter(String text) {

        int o = 1;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
            pt = con.prepareStatement("SELECT * FROM `books` WHERE CONCAT(`isbn`, `title`, `author`, `publisher`, `quantity`, `category`) LIKE '%" + text + "%' ");
            filter = FXCollections.observableArrayList();
            rs = pt.executeQuery();

            while (rs.next()) {

                Book row = new Book();

                row.setSn(rs.getInt(1));
                row.setPosOnTAB(o);
                row.setISBN(rs.getString(2));
                row.setTitle(rs.getString(3));
                row.setAuthor(rs.getString(4));
                row.setPublisher(rs.getString(5));
                row.setQuantity(rs.getString(6));
                row.setCategory(rs.getString(7));

                o++;

                filter.add(row);

            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                rs.close();
                pt.close();
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        libraryTable.setItems(filter);

    }

    public void increaseBookNumber(String isbn, String previous, String next) {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
            pt = con.prepareStatement("UPDATE `books` SET `quantity` = ? WHERE `isbn` = ?");
            pt.setString(1, String.valueOf(Integer.parseInt(previous) + Integer.parseInt(next)));
            pt.setString(2, isbn);

            int value = pt.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        init(null);
    }

    @FXML
    public void addBorrower(ActionEvent e) {
        String name = borrowerName.getText();
        String hall = borrowerHall.getText();
        String number = borrowerNumber.getText();
        String book = selectBook.getValue();
        LocalDate date = selectDate.valueProperty().getValue();
        String realDate = date.toString();
        LocalDate returnDate = date.plusWeeks(2);
        String realReturnDate = returnDate.toString();

        borrowerName.clear();
        borrowerHall.clear();
        borrowerNumber.clear();

        addABorrower(name, hall, number, book, realDate, realReturnDate);
    }

    public void addABorrower(String name, String hall, String number, String book, String realDate, String realReturndate) {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
            pt = con.prepareStatement("INSERT INTO borrowedbooks (`name`, `hall`, `number`, `book`, `borrow_date`, `return_date`) VALUES (?, ?, ? ,? ,? ,?)");
            pt.setString(1, name);
            pt.setString(2, hall);
            pt.setString(3, number);
            pt.setString(4, book);
            pt.setString(5, realDate);
            pt.setString(6, realReturndate);

            int value = pt.executeUpdate();

            if (value >= 1) {
                Notifications formInfo = Notifications.create();
                formInfo.title("Success");
                formInfo.hideAfter(Duration.seconds(3));
                formInfo.position(Pos.TOP_RIGHT);
                formInfo.text("Data for " + name + " Successfully Inputed");
                pop.play();
                formInfo.showInformation();

            }

        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                pt.close();
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        loadBorrowerTable();

    }
    
    ObservableList<String> books;

    private void loadCombobox() {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
            pt = con.prepareStatement("SELECT * FROM `books` ORDER BY `title`");
            books = FXCollections.observableArrayList();

            rs = pt.executeQuery();
            while (rs.next()) {
                books.add(rs.getString("title") + " by " + rs.getString("author"));

            }

        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                rs.close();
                pt.close();
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        selectBook.setItems(books);
    }

    ObservableList<BorrowedBooks> borrowedBooks;

    public void loadBorrowerRecord() {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
            pt = con.prepareStatement("select * from borrowedbooks");
            borrowedBooks = FXCollections.observableArrayList();

            rs = pt.executeQuery();
            while (rs.next()) {
                BorrowedBooks book = new BorrowedBooks();
                book.setName(rs.getString("name"));
                book.setHall(rs.getString("hall"));
                book.setNumber(rs.getString("number"));
                book.setBook(rs.getString("book"));
                book.setBorrowedDate(rs.getString("borrow_date"));
                book.setDueDate(rs.getString("return_date"));
                book.setReturned(new Button("Returned"));
                book.getReturned().setOnAction(e -> remove(book.getName(), book.getBook()));
                borrowedBooks.add(book);

            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                rs.close();
                pt.close();
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        borrowerTable.setItems(borrowedBooks);

    }

    private void loadBorrowerTable() {

        bname.setCellValueFactory(new PropertyValueFactory<>("name"));
        bhall.setCellValueFactory(new PropertyValueFactory<>("hall"));
        bnumber.setCellValueFactory(new PropertyValueFactory<>("number"));
        bbook.setCellValueFactory(new PropertyValueFactory<>("book"));
        bdate.setCellValueFactory(new PropertyValueFactory<>("borrowedDate"));
        bdue.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        returned.setCellValueFactory(new PropertyValueFactory<>("returned"));

        loadBorrowerRecord();
    }
    int genin = 0;

    @FXML
    public void init1(MouseEvent e) {
        if (genin == 0) {

            loadBorrowerTable();
            genin++;
        }

    }

    private void remove(String name, String book) {

        Dialog n = new Alert(Alert.AlertType.CONFIRMATION);

        n.initModality(Modality.APPLICATION_MODAL);
        n.initStyle(StageStyle.UNDECORATED);
        n.setHeaderText("Confirmation");
        n.setContentText("Confirm that " + name + " has returned " + book);
        n.show();
        n.resultProperty().addListener(f -> {
            if (n.getResult().toString().equals("ButtonType [text=OK, buttonData=OK_DONE]")) {
                removeBorrower(name, book);

            }

        });

    }

    private void removeBorrower(String name, String book) {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
            pt = con.prepareStatement("delete from borrowedbooks where name = ? AND book = ?");
            pt.setString(1, name);
            pt.setString(2, book);
            int value = pt.executeUpdate();
            if (value >= 1) {
                Notifications formInfo = Notifications.create();
                formInfo.title("Success");
                formInfo.hideAfter(Duration.seconds(3));
                formInfo.position(Pos.TOP_RIGHT);
                formInfo.text(name + " Has Returned Book ");
                pop.play();
                formInfo.showInformation();

            }

        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                pt.close();
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        loadBorrowerRecord();
    }

    int start = 0;

    @FXML
    public void init2(MouseEvent e) {
        if (start == 0) {

            loadBorrowerList();
            start++;
        }

    }
    ObservableList<String> dueDetails;

    private void loadBorrowerList() {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
            pt = con.prepareStatement("select * from borrowedbooks ");
            rs = pt.executeQuery();
            dueDetails = FXCollections.observableArrayList();
            String duedate = null;
            Date today = new Date();

            int month = today.getMonth() + 1;

            Date toda = new Date(today.getYear() + 1900, month, today.getDate());

            while (rs.next()) {
                duedate = rs.getString("return_date");
                String[] dateArray = duedate.split("-");
                Date dueDate = new Date(Integer.parseInt(dateArray[0]), Integer.parseInt(dateArray[1]), Integer.parseInt(dateArray[2]));
                if (dueDate.before(toda)) {
                    String details = rs.getString("book") + " was due for return on " + rs.getString("return_date") + " call " + rs.getString("name") + " on " + rs.getString("number");
                    dueDetails.add(details);

                }

            }

        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }

        duebooksListView.setItems(dueDetails);

    }

    @FXML
    void showAbout(ActionEvent event) {
        Alert about = new Alert(Alert.AlertType.INFORMATION);
        about.setTitle("About Library System");
        about.initStyle(StageStyle.UNDECORATED);
        ImageView image = new ImageView("librarysystem/splash.jpg");

        about.setGraphic(image);

        about.setHeaderText("\n");
        about.setContentText(""
                + ""
                + "(c)2015 All Rights Reserved");
        about.showAndWait();
    }

    Stage window;

    @FXML
    void showSettings(ActionEvent event) throws IOException {
        window = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("settings.fxml"));
        Scene scene = new Scene(root);

        window.setScene(scene);
        window.setTitle("Settings");

        window.getIcons().add(new Image("librarysystem/icon.png"));

        window.show();

    }

    @FXML
    void showHelp(ActionEvent event) {
        Alert contact = new Alert(Alert.AlertType.INFORMATION);
        contact.setTitle("HELP");
        contact.initStyle(StageStyle.UNDECORATED);
        contact.initModality(Modality.APPLICATION_MODAL);
        contact.setGraphic(new ImageView("librarysystem/splash.jpg"));
        contact.setHeaderText("Contact the developer\n"
                + "Email:uchennaobi97@yahoo.com"
                + "\nPhone:08180971309"
                + "\n");
        contact.showAndWait();
    }

    @FXML
    public void addUser(ActionEvent e) {
        LoginDialog login = new LoginDialog(null, null);
        login.setHeaderText("CREATE ACCOUNT");
        login.setContentText("Create a new user account");
        login.resultProperty().addListener(t -> {
            Pair<String, String> loginDetail = login.getResult();

            String uname = loginDetail.getKey();
            String pass = loginDetail.getValue();
            System.out.println(uname + " " + pass);
            createNewAccount(uname, pass);
        }
        );

        login.show();

    }

    @FXML
    public void editAccount(ActionEvent e) {
        LoginDialog login = new LoginDialog(null, null);
        login.setHeaderText("Enter Previous Account Details");
        login.setTitle("EDIT ACCOUNT");
        login.setContentText("Enter Previous Account Details");
        login.resultProperty().addListener(t -> {
            try {
                Pair<String, String> loginDetail = login.getResult();

                String uname = loginDetail.getKey();
                String pass = loginDetail.getValue();
                con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
                pt = con.prepareStatement("SELECT * FROM user WHERE username = ? AND password = ?");
                pt.setString(1, uname);
                pt.setString(2, pass);
                rs = pt.executeQuery();

                if (rs.next()) {
                    int id = rs.getInt("id");
                    LoginDialog log = new LoginDialog(null, null);
                    login.setTitle("EDIT ACCOUNT");
                    log.setHeaderText("Enter New Account Details");

                    log.setContentText("Enter New Account Details");
                    log.resultProperty().addListener(r -> {
                        Pair<String, String> loginDetail1 = log.getResult();

                        String uname1 = loginDetail1.getKey();
                        String pass1 = loginDetail1.getValue();

                        editrecord(id, uname1, pass1);
                    });

                    log.show();
                } else {
                }
            } catch (SQLException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    rs.close();
                    pt.close();
                    con.close();
                } catch (SQLException ex) {
                    Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
        );

        login.show();

    }

    private void createNewAccount(String uname, String pass) {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
            pt = con.prepareStatement("INSERT INTO `user` (`username`, `password`) VALUES (?, ?)");
            pt.setString(1, uname);
            pt.setString(2, pass);
            int value = pt.executeUpdate();

            if (value >= 1) {
                Notifications formInfo = Notifications.create();
                formInfo.title("Success");
                formInfo.hideAfter(Duration.seconds(3));
                formInfo.position(Pos.TOP_RIGHT);
                formInfo.text("Operation Completed");
                pop.play();
                formInfo.showInformation();

            }

        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                pt.close();
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    @FXML
    public void deleteAll(ActionEvent e) {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "");
            pt = con.prepareStatement("drop database library");
            pt.execute();
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                pt.close();
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        System.exit(0);

    }

    public void backUp(ActionEvent p) {

        Thread Th = new Thread(new Runnable() {

            @Override
            public void run() {
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        FileWriter write = null;
                        try {
                            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
                            pt = con.prepareStatement("select * from books ");
                            rs = pt.executeQuery();
                            int i = 1;
                            FileChooser choose = new FileChooser();
                            choose.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV (MS-DOS)", "*.csv"));

                            File save = choose.showSaveDialog(null);
                            write = new FileWriter(save);
                            write.write("S/N" + "," + "ISBN" + "," + "Book" + "," + "Author" + "," + "Publisher" + "," + "Quantity" + "," + "Category");
                            write.write("\n");
                            while (rs.next()) {

                                String a = rs.getString(2);
                                String b = rs.getString(3);
                                String c = rs.getString(4);
                                String d = rs.getString(5);
                                String e = rs.getString(6);
                                String f = rs.getString(7);
                                write.write(i + "," + a + "," + b + "," + c + "," + d + "," + e + "," + f);
                                write.write("\n");
                                i++;
                            }

                        } catch (SQLException ex) {
                            Logger.getLogger(LibrarySystem.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(LibrarySystem.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(LibrarySystem.class.getName()).log(Level.SEVERE, null, ex);
                        } finally {
                            try {
                                rs.close();
                                pt.close();
                                con.close();
                                write.flush();
                                write.close();
                            } catch (IOException ex) {
                                Logger.getLogger(LibrarySystem.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (SQLException ex) {
                                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        }

                    }
                });
            }
        });
        // Th.start();
        window = new Stage();
        WebView n = new WebView();
        n.getEngine().load("http://localhost/phpmyadmin/db_export.php?db=library");
        n.setVisible(true);
        Scene scene = new Scene(n);

        window.setScene(scene);
        window.setTitle("Settings");

        window.getIcons().add(new Image("librarysystem/icon.png"));

        window.show();

    }

    private void editrecord(int id, String uname1, String pass1) {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
            pt = con.prepareStatement("delete from user where id = ? ");

            pt.setInt(1, id);

            int value = pt.executeUpdate();

            if (value >= 1) {

            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                pt.close();
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        createNewAccount(uname1, pass1);

    }

    /**
     * ************************************************************************\
     * * * * LIBRARY SECTION FOR PROJECTS * * * * *
     * \************************************************************************
     */
    @FXML
    private void authenticateProjects(ActionEvent e) {

        try {
            Stage stage;
            Parent root;

            stage = (Stage) but.getScene().getWindow();

            root = FXMLLoader.load(getClass().getResource("Project.fxml"));
            Scene scene = new Scene(root);

            stage.hide();
            stage.setHeight(Screen.getPrimary().getVisualBounds().getHeight());
            stage.setWidth(Screen.getPrimary().getVisualBounds().getWidth());
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.setResizable(false);

            stage.show();

        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    int loader = 0;

    @FXML
    private void start(MouseEvent e) {
        if (loader == 0) {

            init();
            loader++;
            searchTextField.textProperty().addListener(t -> {

                filter2(searchTextField.getText());
            });

        }

    }

    public void init() {

        libraryTable.setEffect(new InnerShadow());

        snColumn.setCellValueFactory(new PropertyValueFactory<>("pos"));
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("matric"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("titl"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("owner"));
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("supervisor"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("year"));

        loadDataOnTable();
    }

    @FXML
    private void addProject(ActionEvent e) {
        String matric = isbnTextField.getText();
        String name = authorTextField.getText();
        String title = titleTextField.getText();
        String supervisor = publisherTextField.getText();
        String year = quantityTextField.getText();

        newProject(matric, name, title, supervisor, year);

    }

    public void newProject(String matric, String name, String title, String supervisor, String year) {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");

            pt = con.prepareStatement("INSERT INTO `library`.`projects` (`matric`, `title`, `owner`, `supervisor`, `year`) VALUES (?, ?, ?, ?, ?)");
            pt.setString(1, matric);
            pt.setString(2, title);
            pt.setString(3, name);
            pt.setString(4, supervisor);
            pt.setString(5, year);

            int value = pt.executeUpdate();

            if (value == 1) {
                pt.close();
                con.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                pt.close();
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        loadDataOnTable();

    }

    @FXML
    private void importProject(ActionEvent e) {
        FileChooser f = new FileChooser();
        f.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV (MS-DOS)", "*.csv"));
        f.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("CSV (MS-DOS)", "*.csv"));
        f.setInitialFileName("CSV (MS-DOS) (*.csv)");
        f.setTitle("Choose a CSV (MS-DOS) Document (*.csv)");
        File choose = f.showOpenDialog(null);

        if (!choose.getName().contains(".csv")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please choose a .csv file");
            alert.showAndWait();

            importData(null);
        }
        Task<Object> worker = new Task<Object>() {

            @Override
            protected Object call() throws Exception {

                ArrayList<String> list = new ArrayList<>();
                Scanner input = new Scanner(choose);

                while (input.hasNextLine()) {

                    list.add(input.nextLine());
                }

                for (int l = 1; l < list.size(); l++) {

                    String[] data = list.get(l).split(",");

                    newProject(data[1], data[2], data[3], data[4], data[5]);

                    System.out.println(l);
                    updateProgress(l, list.size());

                }

                return null;
            }
        };

        ProgressDialog dlg = new ProgressDialog(worker);
        dlg.setContentText("Importing Please Wait");
        Thread th = new Thread(worker);
        th.setDaemon(true);
        th.start();

    }

    @FXML
    private void addLib(ActionEvent e) {
        String name = borrowerName.getText();
        String hall = borrowerHall.getText();
        String number = borrowerNumber.getText();
        String book = selectBook.getValue();
        LocalDate date = selectDate.valueProperty().getValue();
        String realDate = date.toString();
        LocalDate returnDate = date.plusWeeks(2);
        String realReturnDate = returnDate.toString();

        borrowerName.clear();
        borrowerHall.clear();
        borrowerNumber.clear();

        addAB(name, hall, number, book, realDate, realReturnDate);

    }

    public void addAB(String name, String hall, String number, String book, String realDate, String realReturndate) {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
            pt = con.prepareStatement("INSERT INTO borrowedprojects (`name`, `hall`, `number`, `project`, `borrow_date`, `return_date`) VALUES (?, ?, ? ,? ,? ,?)");
            pt.setString(1, name);
            pt.setString(2, hall);
            pt.setString(3, number);
            pt.setString(4, book);
            pt.setString(5, realDate);
            pt.setString(6, realReturndate);

            int value = pt.executeUpdate();

            if (value >= 1) {
                Notifications formInfo = Notifications.create();
                formInfo.title("Success");
                formInfo.hideAfter(Duration.seconds(3));
                formInfo.position(Pos.TOP_RIGHT);
                formInfo.text("Data for " + name + " Successfully Inputed");
                pop.play();
                formInfo.showInformation();

            }

        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                pt.close();
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        loadBorrowerData();

    }

    private void loadBTable() {

        bname.setCellValueFactory(new PropertyValueFactory<>("name"));
        bhall.setCellValueFactory(new PropertyValueFactory<>("hall"));
        bnumber.setCellValueFactory(new PropertyValueFactory<>("number"));
        bbook.setCellValueFactory(new PropertyValueFactory<>("project"));
        bdate.setCellValueFactory(new PropertyValueFactory<>("borrowedDate"));
        bdue.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        returned.setCellValueFactory(new PropertyValueFactory<>("returned"));

        loadBorrowerData();
    }

    int hol = 0;

    @FXML
    public void init3(MouseEvent e) {
        if (hol == 0) {
            loadBTable();
            hol++;

        }

    }

    ObservableList BorrowedProjects;

    public void loadBorrowerData() {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
            pt = con.prepareStatement("select * from borrowedprojects");
            BorrowedProjects = FXCollections.observableArrayList();

            rs = pt.executeQuery();
            while (rs.next()) {
                BorrowedProjects book = new BorrowedProjects();
                book.setName(rs.getString("name"));
                book.setHall(rs.getString("hall"));
                book.setNumber(rs.getString("number"));
                book.setProjects(rs.getString("project"));
                book.setBorrowedDate(rs.getString("borrow_date"));
                book.setDueDate(rs.getString("return_date"));
                book.setReturned(new Button("Returned"));
                book.getReturned().setOnAction(e -> removeA(book.getName(), book.getProjects()));
                BorrowedProjects.add(book);

            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                rs.close();
                pt.close();
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        borrowerTable.setItems(BorrowedProjects);
        loadBPList();
    }

    ObservableList filter1;
    private void filter2(String text) {
        int o = 1;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
            pt = con.prepareStatement("SELECT * FROM `projects` WHERE CONCAT(`matric`, `title`, `owner`, `supervisor`, `year`) LIKE '%" + text + "%' ");
            filter1 = FXCollections.observableArrayList();
            rs = pt.executeQuery();

            while (rs.next()) {
                Projects row = new Projects();

                row.setSn(rs.getInt(1));
                row.setPos(o);
                row.setMatric(rs.getString(2));
                row.setTitl(rs.getString(3));
                row.setOwner(rs.getString(4));
                row.setSupervisor(rs.getString(5));
                row.setYear(rs.getString(6));

                o++;

                filter1.add(row);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                rs.close();
                pt.close();
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        libraryTable.setItems(filter1);
    }

    private void loadDataOnTable() {
        try {
            int o = 1;
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
            pt = con.prepareStatement("select * from projects");
            data = FXCollections.observableArrayList();

            rs = pt.executeQuery();

            while (rs.next()) {

                Projects row = new Projects();

                row.setSn(rs.getInt(1));
                row.setPos(o);
                row.setMatric(rs.getString(2));
                row.setTitl(rs.getString(3));

                row.setOwner(rs.getString(4));
                row.setSupervisor(rs.getString(5));
                row.setYear(rs.getString(6));

                o++;

                data.add(row);

            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        libraryTable.setItems(data);
        loadComboboxData();

    }

    private void loadComboboxData() {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
            pt = con.prepareStatement("SELECT * FROM `projects` ORDER BY `title`");
            books = FXCollections.observableArrayList();

            rs = pt.executeQuery();
            while (rs.next()) {
                books.add(rs.getString("title") + " by " + rs.getString("matric"));

            }

        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                rs.close();
                pt.close();
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        selectBook.setItems(books);
    }

    private void removeA(String name, String projects) {
        Dialog n = new Alert(Alert.AlertType.CONFIRMATION);

        n.initModality(Modality.APPLICATION_MODAL);
        n.initStyle(StageStyle.UNDECORATED);
        n.setHeaderText("Confirmation");
        n.setContentText("Confirm that " + name + " has returned " + projects);
        n.show();
        n.resultProperty().addListener(f -> {
            if (n.getResult().toString().equals("ButtonType [text=OK, buttonData=OK_DONE]")) {
                removeBorro(name, projects);

            }

        });

    }

    private void removeBorro(String name, String projects) {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
            pt = con.prepareStatement("delete from borrowedprojects where name = ? AND project = ?");
            pt.setString(1, name);
            pt.setString(2, projects);
            int value = pt.executeUpdate();
            if (value >= 1) {
                Notifications formInfo = Notifications.create();
                formInfo.title("Success");
                formInfo.hideAfter(Duration.seconds(3));
                formInfo.position(Pos.TOP_RIGHT);
                formInfo.text(name + " Has Returned Project ");
                pop.play();
                formInfo.showInformation();

            }

        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                pt.close();
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        loadBorrowerData();

    }

    int cat = 0;

    @FXML
    public void init4(MouseEvent e) {
        if (cat == 0) {
            JOptionPane.showMessageDialog(null, "load");
            loadBPList();

            cat++;
        }

    }

    private void loadBPList() {

        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
            pt = con.prepareStatement("select * from borrowedprojects ");
            rs = pt.executeQuery();
            dueDetails = FXCollections.observableArrayList();
            String duedate = null;
            Date today = new Date();

            int month = today.getMonth() + 1;

            Date toda = new Date(today.getYear() + 1900, month, today.getDate());

            while (rs.next()) {
                duedate = rs.getString("return_date");
                String[] dateArray = duedate.split("-");
                Date dueDate = new Date(Integer.parseInt(dateArray[0]), Integer.parseInt(dateArray[1]), Integer.parseInt(dateArray[2]));
                if (dueDate.before(toda)) {
                    String details = rs.getString("project") + " was due for return on " + rs.getString("return_date") + " call " + rs.getString("name") + " on " + rs.getString("number");
                    dueDetails.add(details);

                }

            }

        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }

        duebooksListView.setItems(dueDetails);

    }

}
