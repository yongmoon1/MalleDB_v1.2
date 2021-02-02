import MalleDB.src.main.java.db.MalleDB;
import MalleDB.src.main.java.util.Options;

public class test{
	public static void main(String[] args){
		MalleDB malleDB = new MalleDB();
		malleDB.init();
		malleDB.create();

		malleDB.insert("filename", "1");;
	}
}
