
public class EarExtractorTest {

	public static void main(String[] args) {

		String regex = "^_{2}.*\\.class$";
		String target = "C:/Users/ST20018615/Desktop/ship";
		String configLocation = "C:/Users/ST20018615/eclipse-workspace3/Ear2/src/earLocation";
		EarExtractor ear = new EarExtractor(false, regex, target, configLocation);
		
		/* //init method peforms the same, flag false if you dont want to unzip directory every time
		ear.locateEar(); //load ear location from txt file
		ear.setEarDestination("C:/Users/ST20018615/Desktop/ship"); //to-do: store destination in txt file... convert to xml
		ear.createDestination();
		//ear.extractArchive();
		ear.setWEBINF();
		ear.setJSP();
		ear.countFiles();
		*/
	}
}
