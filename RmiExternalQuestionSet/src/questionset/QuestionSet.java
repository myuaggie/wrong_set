package questionset;

import java.rmi.*;
import java.util.*;

public interface QuestionSet extends Remote{
     ArrayList<Library> getTopKSaleLibraries(int k) throws RemoteException;
     Library buyLibrary(int id) throws RemoteException;
}
