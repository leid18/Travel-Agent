// This is the test class for train image files
// All sample images are stored in the "files" folder under the project folder.
// To test unseen images, please put images in the "unseen" folder in the "files" folder
// and run the testUnseen code

public class testTrain {
	public static void main(String[] args) throws InterruptedException {
		//Create the test object to test images inside "train" folder.
		Main test = new Main("train");
		test.main();
	}
}
