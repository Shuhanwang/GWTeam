package newserver;

import java.io.Closeable;
import java.io.IOException;

public class closeUtility {
public static void closeAll(Closeable...io){
	for(Closeable temp:io){
		try {
			if(null!=temp){
				temp.close();
			}
		} catch (IOException e) {
			//e.printStackTrace();
		}
	}
}
}
