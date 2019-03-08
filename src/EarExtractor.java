

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

public class EarExtractor {
	
	private String earLocation;
	private String earDestination;
	private String webINFpath = "";
	private String jspPath = "";
	
	public EarExtractor() {
		earLocation = "";
	}
	

	public void init(boolean extract) {
		locateEar(); //load ear location from txt file
		setEarDestination("C:/Users/ST20018615/Desktop/ship"); //to-do: store destination in txt file... convert to xml
		createDestination();
		if(extract) {extractArchive();}		
		setWEBINF();
		setJSP();
		countFiles();
	}
	
	/*
	 * If destination folder doesn't exist make it, extract method was not finding folder. Ear destination needs to be set first.
	 */
	public void createDestination() {
		File f = new File(earDestination);
		if (!f.exists()){
			f.mkdir();
		}
		System.out.println("earDestination: " + earDestination);
	}
	public void setEarDestination(String path) {
		earDestination = path;
		File f = new File(earDestination);
		if (!f.exists()){
			f.mkdir();
		}
		System.out.println("earDestination: " + earDestination);
	}

	public void setWebINFpath(String s) {
		webINFpath = s;
	}
	public String locateEar() {
		BufferedReader reader;
		try {
			//add some iteration logic to hold multiple locations in config file.
			reader = new BufferedReader(new FileReader("C:/Users/ST20018615/eclipse-workspace3/Ear2/src/earLocation"));
			String earPath = reader.readLine();
			System.out.println("Path to Ear: " + earPath);
			earLocation = earPath;
		}
		catch (IOException e ){
			e.printStackTrace();
		}
		return earLocation;
		
	}
	
	public void extractArchive() {
		try {
		JarFile jar = new JarFile(earLocation);
		Enumeration<JarEntry> enumEntries = jar.entries();

		
		while (enumEntries.hasMoreElements()) {
		    JarEntry file = (JarEntry) enumEntries.nextElement();
		    //System.out.println(file.getName());

		    File f = new File(earDestination + File.separator + file.getName());
		    if (file.isDirectory()) { // if its a directory, create it
		        f.mkdir();
		        continue;
		    }
		    InputStream is = jar.getInputStream(file); // get the input stream
		    FileOutputStream fos = new FileOutputStream(f);
		    while (is.available() > 0) {  // write contents of 'is' to 'fos'
		        fos.write(is.read());
		    }
		    fos.close();
		    is.close();
		}
		jar.close();
		}
		catch (IOException e ){
			e.printStackTrace();
		}
		System.out.println("done extracting");
	}
	
	
	
	
	public String findFolder(File source, String targetDir) {
		String found = null;
		String temp = null;
		try {
			File[] files = source.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					//System.out.println(file.getName());
					if(file.getName().equals(targetDir)) { //folder name matches target
						//System.out.println("found target: " + file.getName());
						 found = file.getCanonicalPath();					 
						//System.out.println("found at base case: " + found);
					}
					else {
						temp = findFolder(file, targetDir);
						if(temp != null) {
							found = temp;
						}
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println("target not found");
		//System.out.println("found end iter: " + found);
		return found;
	}
	
	public int findFiles(File source, String regex) {
		int count = 0;
		File[] files = source.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				 count += findFiles(file, regex);
			}
			else {
				if(Pattern.matches(regex, file.getName())) {
					count++;
					//System.out.println(count);
					//System.out.println("Matches");
				}
				else {
					//System.out.println("No match");
				}
			}
			
		}
		return count;
	}
	/*
	public String findJSPFolder(File source, String targetDir) {
		String found = "";
		try {
			File[] files = source.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					//System.out.println(file.getName());
					if(file.getName().equals(targetDir)) { //folder name matches target

						System.out.println("found target: " + file.getName());
						 jspPath = file.getCanonicalPath();
						 
						System.out.println("found at base case: " + found);

					
					}
					else {
					//System.out.println("directory:" + file.getCanonicalPath()); //folder name does not match target, search subdirs
					findJSPFolder(file, targetDir);
					}

				} else {
					; //file obj is a file and not a folder
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println("target not found");
		System.out.println("found end iter: " + found);
		return found;
	}
	*/
	public void setWEBINF() {
		webINFpath = findFolder(new File(earDestination), "WEB-INF");
		System.out.println("webINFpath: " + webINFpath);
	}
	
	public void setJSP() {
		jspPath = findFolder(new File(webINFpath), "jsp_servlet");
		System.out.println("jspFolder: " + jspPath);
	}
	
	public int countFiles() {
		String regex = "^_{2}.*\\.class$";
		System.out.println("Regex sent to count Files: " + regex);
		int i =  findFiles(new File(jspPath), regex);
		System.out.println("Number of Files: " + i);
		return i;
	}


}
