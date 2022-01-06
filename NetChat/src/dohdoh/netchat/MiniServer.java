package dohdoh.netchat;

import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

//�� ���α׷��� ��������� �⺻ ��� �۵����� Ȯ���� ���� ���� �ڵ�μ� NetChat������ �⺻ ��ɸ��� ������
//���������� Ŭ���̾�Ʈ�� �����ϰ� �ϰ� ���ڿ��� �޾� �̸� ����ϴ� ������ ��
//�������� �ڵ�� Chat���� �����ϴ� ���Ͽ� ����

public class MiniServer {
	public static void main (String[] args) {
		new MiniServer().go();
	}

	private void go () {
		try {
			ServerSocket serverSock = new ServerSocket(5000);	// ä���� ���� ���� ��Ʈ 5000 ���

			while(true) {
				Socket clientSocket = serverSock.accept();		// ���ο� Ŭ���̾�Ʈ ���� ���

				// Ŭ���̾�Ʈ�� ���� ����� ��Ʈ�� �� ������ ����
				Thread t = new Thread(new ClientHandler(clientSocket));
				t.start();									
				System.out.println("S : Ŭ���̾�Ʈ ���� ��");		// ���¸� �������� ��� �޽���
			}
		} catch(Exception ex) {
			System.out.println("S : Ŭ���̾�Ʈ  ���� �� �̻�߻�");	// ���¸� �������� ���  �޽���
			ex.printStackTrace();
		}
	}

	private class ClientHandler implements Runnable {
		Socket sock;					// Ŭ���̾�Ʈ ����� ����
		ObjectInputStream reader;		// Ŭ���̾�Ʈ�� ���� �����ϱ� ���� ��Ʈ��

		// ������. Ŭ���̾�Ʈ���� ���Ͽ��� �б�� ���� ��Ʈ�� ����� ��
		// ��Ʈ���� ���鶧 InputStream�� ���� ����� Hang��. �׷��� OutputStream���� �������.
		// �̰��� Ŭ���̾�Ʈ���� InpitStreams�� ���� ����� ������ �ȱ׷��� �����
		public ClientHandler(Socket clientSocket) {
			try {
				sock = clientSocket;
				reader = new ObjectInputStream(clientSocket.getInputStream());
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}

		// Ŭ���̾�Ʈ���� ���� �޽����� ���� �����ϴ� �۾��� ����
		public void run() {
			try {
				while (true) {
					String message = (String) reader.readObject();	  // Ŭ���̾�Ʈ�� ���� �޽��� ����

					System.out.println("Message from a client : " + message);
				}
			} catch(EOFException ex) {
				System.out.println("S : Ŭ���̾�Ʈ  ���� ����");	// ���¸� �������� ���  �޽���
			} catch(Exception ex) {
				System.out.println("S : Ŭ���̾�Ʈ  ���� �� �̻�߻�");	// ���¸� �������� ���  �޽���
				ex.printStackTrace();
			} finally {
				try {
					reader.close();
					sock.close();
				} catch (Exception ex) {
					System.out.println("S : ���� �ݱ� ���� ���� �߻�");	// ���¸� �������� ���  �޽���
					ex.printStackTrace();				
				}
			}
		}
	} // close run
} // close inner class
