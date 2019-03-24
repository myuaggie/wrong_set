package questionset;

import java.rmi.*;
import java.util.ArrayList;
import javax.naming.*;

public class QuestionSetServer {
    public static void main(String[] args) throws RemoteException, NamingException{
        System.out.println("Constructing server implementation...");
        QuestionSetImpl questionSet = new QuestionSetImpl();
        for (int j=0;j<10;j++) {
            ArrayList<Question> ql=new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                Question q = new Question("q" + String.valueOf(i)+"-"+String.valueOf(j)
                        , "c" + String.valueOf(i)+"-"+String.valueOf(j));
                ql.add(q);
            }
            questionSet.addLibrary("l"+String.valueOf(j),
                    Math.random()*100+Math.random(),
                    "c"+String.valueOf(j), ql, (int)(Math.random()*100));
        }

        System.out.println("Binding server implementation to registry...");
        Context namingContext = new InitialContext();
        namingContext.bind("rmi:questionset", questionSet);

        System.out.println("Waiting for invocations from clients...");
    }
}
