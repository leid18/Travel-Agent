// This is the test class for all image files
// All sample images are stored in the "files" folder under the project folder.
// To test unseen images, please put images in the "unseen" folder in the "files" folder
// and run the testUnseen code

public class test {
	public static void main(String[] args) throws InterruptedException {
		//Create the test object to test images inside "ontrack" folder.
		Main test = new Main("all");
		test.main();
	}
}
