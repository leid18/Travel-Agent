// This is the test class for barrier events
// All sample images are stored in the "files" folder under the project folder.
// To test unseen images, please put images in the "unseen" folder in the "files" folder
// and run the testUnseen code

public class testBarrier {
	public static void main(String[] args) throws InterruptedException {
		//Create the test object to test images inside "barrier" folder.
		Main test = new Main("barrier");
		test.main();
	}
}
