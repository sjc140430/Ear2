

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

public class EarExtractor {
	
	private String earLocation;
	private String earTarget;
	private String webINFpath;
	private String jspPath;
	private String regex;
	private String configLocation;
	private String extract;
	
	public EarExtractor(String configLocation) throws InvalidPropertiesFormatException, FileNotFoundException, IOException {
		this.configLocation=configLocation;
		Properties prop = new Properties();
		prop.loadFromXML(new FileInputStream(configLocation));
		
		this.earTarget = prop.getProperty("earTarget");
		this.regex = prop.getProperty("regex");
		this.earLocation = prop.getProperty("earLocation");
		this.extract =prop.getProperty("extract");
		
		System.out.println("Properties:");
		System.out.println(earTarget);
		System.out.println(regex);
		System.out.println(earLocation);
		System.out.println(extract);
		System.out.println();
		
	}
	
	public void init() {
		//setEarLocation(locateEar()); //load ear location from txt file
		createTargetDirectory(); //not sure if neccessary or if extractArchive just takes long time... //Creates target dir b4 extract
		if(extract.equals("true")) {extractArchive();}		
		webINFpath = findFolder(new File(earTarget), "WEB-INF");
		jspPath = findFolder(new File(webINFpath), "jsp_servlet");
		
		System.out.println("webINFpath: " + webINFpath);
		System.out.println("jspPath: " + jspPath);
		System.out.println();
		
		countFiles(jspPath, regex);
		System.out.println();
		countFiles(earTarget, ".*\\.class");
		System.out.println();
	}
	
	/*
	public void setEarLocation(String location) {
		earLocation = location;
	}
	*/
	public String locateEar() {
		BufferedReader reader;
		try {
			//add some iteration logic to hold multiple locations in config file.
			reader = new BufferedReader(new FileReader(configLocation));
			String earPath = reader.readLine();
			System.out.println("Path to Ear: " + earPath);
		}
		catch (IOException e ){
			e.printStackTrace();
		}
		return earLocation;		
	}
	
	/*
	 * If destination folder doesn't exist make it, extract method was not finding folder. Ear destination needs to be set first.
	 */
	public void createTargetDirectory() {
		File f = new File(earTarget);
		if (!f.exists()){
			f.mkdir();
		}
		System.out.println("earTarget: " + earTarget);
	}
	
	public void extractArchive() {
		try {
		JarFile jar = new JarFile(earLocation);
		Enumeration<JarEntry> enumEntries = jar.entries();
		
		while (enumEntries.hasMoreElements()) {
		    JarEntry file = (JarEntry) enumEntries.nextElement();
		    //System.out.println(file.getName());

		    File f = new File(earTarget + File.separator + file.getName());
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
		//System.out.println("source inside findFiles call: " + source.getName());
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
	
	//creates file object out of name passed, and regex passed, Printer handling to console
	public int countFiles(String source, String regex) {
		System.out.println("Regex sent to count Files: " + regex);
		System.out.println("Source sent to count Files: " + source);
		int i =  findFiles(new File(source), regex);
		System.out.println("Number of Files: " + i);
		return i;
	}
}
