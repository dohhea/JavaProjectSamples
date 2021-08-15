package dohdoh.netchat;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JOptionPane;

public class MiniClient {
	Socket sock;
	ObjectOutputStream writer;		// 서버로 송신하기 위한 스트림

	public static void main (String[] args) {
		new MiniClient().go();
	}

	private void go () {
		try {
			sock = new Socket("127.0.0.1", 5000);	// 채팅을 위한 소켓 포트 5000 사용
			writer = new ObjectOutputStream(sock.getOutputStream());

			for (int i=0; i<10; i++) {
				writer.writeObject("Hello From Client");
				writer.flush();
				
				Thread.sleep(500);
			}
			
		} catch(Exception ex) {
			System.out.println("C : 클라이언트  연결 중 이상발생");	// 상태를 보기위한 출력  메시지
			ex.printStackTrace();
		} finally {
			try {
				writer.close();
				sock.close();
			} catch (Exception ex) {
				System.out.println("C : 연결 닫기 전에 문제 발생");	// 상태를 보기위한 출력  메시지
				ex.printStackTrace();				
			}
		}
	}
}
