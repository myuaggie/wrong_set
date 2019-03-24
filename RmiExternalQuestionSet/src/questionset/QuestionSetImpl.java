package questionset;

import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

public class QuestionSetImpl extends UnicastRemoteObject implements QuestionSet{
    private ArrayList<Library> libraries;
    private int count;
    private int idx;

    public QuestionSetImpl() throws RemoteException{
        this.libraries=new ArrayList<>();
        this.count=0;
        this.idx=0;
    }

    public void addLibrary(String name, double price, String category, ArrayList<Question> questions, int sales){
        Library l = new Library(this.idx,name,price,category);
        l.setSales(sales);
        l.addQuestions(questions);
        this.libraries.add(l);
        this.count+=1;
        this.idx+=1;
    }

    public void changePrice(int idx, double price){
        Iterator it=libraries.iterator();
        while (it.hasNext()){
            if (((Library)it.next()).getId()==idx){
                ((Library)it.next()).setPrice(price);
                return;
            }
        }
    }

    public ArrayList<Library> getTopKSaleLibraries(int k) throws RemoteException {
        Collections.sort(this.libraries, new Comparator<Library>() {
            public int compare(Library user1, Library user2) {
                return user2.getSales()-user1.getSales();
            }});
        ArrayList<Library> ranked=new ArrayList<>();
        int i=0;
        Iterator it=this.libraries.iterator();
        while (it.hasNext()&&i<k){
            Library lib=(Library)it.next();
                ranked.add(lib);
                i++;

        }
        return ranked;
    }

    public Library buyLibrary(int id) throws RemoteException{
        Iterator it=this.libraries.iterator();
        while (it.hasNext()){
            Library lib=(Library)it.next();
            System.out.println(lib.getId());
            if (lib.getId()==id){
                lib.setSales(lib.getSales()+1);
                return lib;
            }
        }
        return null;
    }


}
