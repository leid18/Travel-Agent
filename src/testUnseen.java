// This is the test class for all unseen image files
// All sample images are stored in the "files" folder under the project folder.
// To test unseen images, please put images in the "unseen" folder in the "files" folder
// and run this code

public class testUnseen {
	public static void main(String[] args) throws InterruptedException {
		//Create the test object to test images inside "unseen" folder.
		Main test = new Main("unseen");
		test.main();
	}
}
