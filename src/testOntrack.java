// This is the test class for ontrack events
// All sample images are stored in the "files" folder under the project folder.
// To test unseen images, please put images in the "unseen" folder in the "files" folder
// and run the testUnseen code

public class testOntrack {
	public static void main(String[] args) throws InterruptedException {
		//Create the test object to test images inside "ontrack" folder.
		Main test = new Main("ontrack");
		test.main();
	}
}
