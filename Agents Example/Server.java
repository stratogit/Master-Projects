


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {
	private Socket s;
	static final int BUFSIZE = 1024;

	public Server(Socket s) {
		this.s = s;
	}

	public static void main(String[] args) {
		try {
			int port = 40001;
			System.out.println("Server Running...");
			ServerSocket listener = new ServerSocket(port);
			System.out.println("Server created on port:" +port);
			for (;;) {
				System.out.println("Waiting for connections...");
				Socket s = listener.accept();
				Thread server = new Server(s);
				server.start();
			}
		} catch (IOException e) {
			return;
		}
	}

	public void run() {
		try {
			handleClient(s);
		} catch (IOException e) {
			return;
		}
	}

	static void handleClient(Socket s) throws IOException {
		
		// print out client's address
		System.out.println(
				"Connection from " + s.getInetAddress().getHostName() + 
				" at : " + s.getInetAddress().getHostAddress() +
				" Port: " + s.getPort());

		DataOutputStream out = new DataOutputStream(s.getOutputStream());
		DataInputStream in = new DataInputStream(s.getInputStream());
		String input = in.readUTF();
		long fib = fibonacci(Integer.parseInt(input));
		out.writeUTF(""+fib);
		

		System.out.println("Connection closed\n");

		s.close();
	}
	public static long fibonacci(int n) {
        if (n <= 1) return n;
        else return fibonacci(n-1) + fibonacci(n-2);
    }
}