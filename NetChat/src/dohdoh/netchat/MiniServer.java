package dohdoh.netchat;

import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

//이 프로그램은 소켓통신의 기본 기능 작동여부 확인을 위해 만든 코드로서 NetChat에서의 기본 기능만을 제공함
//서버에서는 클라이언트를 접속하게 하고 문자열을 받아 이를 출력하는 역할을 함
//실제적인 코드는 Chat으로 시작하는 파일에 있음

public class MiniServer {
	public static void main (String[] args) {
		new MiniServer().go();
	}

	private void go () {
		try {
			ServerSocket serverSock = new ServerSocket(5000);	// 채팅을 위한 소켓 포트 5000 사용

			while(true) {
				Socket clientSocket = serverSock.accept();		// 새로운 클라이언트 접속 대기

				// 클라이언트를 위한 입출력 스트림 및 스레드 생성
				Thread t = new Thread(new ClientHandler(clientSocket));
				t.start();									
				System.out.println("S : 클라이언트 연결 됨");		// 상태를 보기위한 출력 메시지
			}
		} catch(Exception ex) {
			System.out.println("S : 클라이언트  연결 중 이상발생");	// 상태를 보기위한 출력  메시지
			ex.printStackTrace();
		}
	}

	private class ClientHandler implements Runnable {
		Socket sock;					// 클라이언트 연결용 소켓
		ObjectInputStream reader;		// 클라이언트로 부터 수신하기 위한 스트림

		// 구성자. 클라이언트와의 소켓에서 읽기와 쓰기 스트림 만들어 냄
		// 스트림을 만들때 InputStream을 먼저 만들면 Hang함. 그래서 OutputStream먼저 만들었음.
		// 이것은 클라이언트에서 InpitStreams을 먼저 만들기 때문임 안그러면 데드락
		public ClientHandler(Socket clientSocket) {
			try {
				sock = clientSocket;
				reader = new ObjectInputStream(clientSocket.getInputStream());
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}

		// 클라이언트에서 받은 메시지에 따라 상응하는 작업을 수행
		public void run() {
			try {
				while (true) {
					String message = (String) reader.readObject();	  // 클라이언트의 전송 메시지 받음

					System.out.println("Message from a client : " + message);
				}
			} catch(EOFException ex) {
				System.out.println("S : 클라이언트  연결 종료");	// 상태를 보기위한 출력  메시지
			} catch(Exception ex) {
				System.out.println("S : 클라이언트  연결 중 이상발생");	// 상태를 보기위한 출력  메시지
				ex.printStackTrace();
			} finally {
				try {
					reader.close();
					sock.close();
				} catch (Exception ex) {
					System.out.println("S : 연결 닫기 전에 문제 발생");	// 상태를 보기위한 출력  메시지
					ex.printStackTrace();				
				}
			}
		}
	} // close run
} // close inner class
