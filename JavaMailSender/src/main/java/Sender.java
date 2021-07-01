import java.io.IOException;

public class Sender {
	
	public static void main(String[] args) throws IOException {
		
		JavaSendMail objSender = new JavaSendMail();
		objSender.send();
	}

}
