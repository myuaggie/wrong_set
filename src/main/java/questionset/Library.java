package questionset;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class Library implements Serializable{
    private int id;
    private String name;
    private double price;
    private String category;
    private int count;
    private int sales;
    private ArrayList<Question> questions;

    public Library(int id, String name, double price, String category){
        this.id=id;
        this.name=name;
        this.price=price;
        this.category=category;
        this.sales=0;
        this.count=0;
        this.questions=new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getSales() {
        return sales;
    }

    public void setSales(int sales) {
        this.sales = sales;
    }

    public int getCount() {
        return count;
    }

    public ArrayList<Question> getQuestionsIterator() {
        return questions;
    }

    public void addQuestion(Question question) {
        this.questions.add(question);
        this.count+=1;
    }

    public void addQuestions(ArrayList<Question> questions) {
        Iterator it=questions.iterator();
        while (it.hasNext()){
            Question q=(Question)it.next();
            this.questions.add(q);
            this.count+=1;
        }
    }
}
