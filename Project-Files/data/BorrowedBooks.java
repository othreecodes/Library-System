
package data;

import javafx.scene.control.Button;


public class BorrowedBooks {
    String name;
    String hall;
    String number;
    String book;
    String borrowedDate;
    String dueDate;
    Button returned;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHall() {
        return hall;
    }

    public void setHall(String hall) {
        this.hall = hall;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public String getBorrowedDate() {
        return borrowedDate;
    }

    public void setBorrowedDate(String borrowedDate) {
        this.borrowedDate = borrowedDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public Button getReturned() {
        return returned;
    }

    public void setReturned(Button returned) {
        this.returned = returned;
    }
    
    
}
