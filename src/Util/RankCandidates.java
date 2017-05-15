package Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class RankCandidates {
	
	public void callPythonProcess(String path,String pythonPath) throws IOException{
		String s = null;
		String pathToScript = path +"gems.py";		
		String command = "python "+pathToScript;
	
		ProcessBuilder pb = new ProcessBuilder(pythonPath,"gems.py");
		pb.directory(new File(path));
		Process p = pb.start();
		BufferedReader stdInput = new BufferedReader(new 
                InputStreamReader(p.getInputStream()));

       BufferedReader stdError = new BufferedReader(new 
            InputStreamReader(p.getErrorStream()));
       System.out.println("Here is the standard output of the command:\n");
       while ((s = stdInput.readLine()) != null) {
           System.out.println(s);
       }
       
    // read any errors from the attempted command
       System.out.println("Here is the standard error of the command (if any):\n");
       while ((s = stdError.readLine()) != null) {
           System.out.println(s);
       }
	}
}
