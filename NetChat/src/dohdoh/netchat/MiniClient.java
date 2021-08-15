package dohdoh.netchat;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JOptionPane;

public class MiniClient {
	Socket sock;
	ObjectOutputStream writer;		// ������ �۽��ϱ� ���� ��Ʈ��

	public static void main (String[] args) {
		new MiniClient().go();
	}

	private void go () {
		try {
			sock = new Socket("127.0.0.1", 5000);	// ä���� ���� ���� ��Ʈ 5000 ���
			writer = new ObjectOutputStream(sock.getOutputStream());

			for (int i=0; i<10; i++) {
				writer.writeObject("Hello From Client");
				writer.flush();
				
				Thread.sleep(500);
			}
			
		} catch(Exception ex) {
			System.out.println("C : Ŭ���̾�Ʈ  ���� �� �̻�߻�");	// ���¸� �������� ���  �޽���
			ex.printStackTrace();
		} finally {
			try {
				writer.close();
				sock.close();
			} catch (Exception ex) {
				System.out.println("C : ���� �ݱ� ���� ���� �߻�");	// ���¸� �������� ���  �޽���
				ex.printStackTrace();				
			}
		}
	}
}
