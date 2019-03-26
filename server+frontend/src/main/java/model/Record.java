package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.sql.Blob;
import java.util.Date;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Record implements Serializable {
    private URKey urKey;
    private Blob answer;
    private Date date;

    public Record(){}

    public URKey getUrKey(){return urKey;}
    public void setUrKey(URKey urKey){this.urKey=urKey;}

    public Blob getAnswer(){return answer;}
    public void setAnswer(Blob answer){this.answer=answer;}

    public Date getDate(){return date;}
    public void setDate(Date date){this.date=date;}
}
