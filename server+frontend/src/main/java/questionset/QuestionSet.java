package questionset;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface QuestionSet extends Remote{
     ArrayList<Library> getTopKSaleLibraries(int k) throws RemoteException;
     Library buyLibrary(int id) throws RemoteException;
}
