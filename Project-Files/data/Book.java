
package data;

import javafx.scene.control.TextField;


public class Book {

    int sn;
    int posOnTAB;
    String ISBN;
    String title;
    String author;
    String publisher;
    String quantity;
    String category;

    public TextField getC() {
        return c;
    }

    public void setC(TextField c) {
        this.c = c;
    }

    TextField c ;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }

    public int getPosOnTAB() {
        return posOnTAB;
    }

    public void setPosOnTAB(int posOnTAB) {
        this.posOnTAB = posOnTAB;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}
