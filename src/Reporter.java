import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Reporter {

    private int port;
    private OutputStream out;
    private ServerSocket server;

    public Reporter(int port, String file) throws FileNotFoundException {
        this.port = port;
        this.out = new FileOutputStream(file);

        //Zugriff auf Laufzeitobjekt:
    Runtime.getRuntime().addShutdownHook(new Thread(){
        public void run(){
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
                if (server != null) {
                    server.close();
                }
            } catch (IOException e) {
            }
        }
    });
    }

    public void doWork() {

        try {
            server = new ServerSocket(port);
            //Endlosschleife
            while (true) {
                //Listens for a connection to be made to this socket and accepts it.
                Socket client = server.accept();
                //Timeout in Millisekunden
                client.setSoTimeout(3000);
                InputStream in = client.getInputStream();


                try {
                    int c;
                    while ((c = in.read()) != -1) {
                        out.write(c);
                    }
                } catch (SocketTimeoutException e) {
                } finally {

                    try {
                        if (in != null) {
                            in.close();
                        }
                        if (client != null) {
                            client.close();
                        }
                        out.write('\r');
                        out.write('\n');
                        out.flush();
                    } catch (IOException e) {
                    }
                }

            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }


    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("java Reporter <port> <file>");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);
        String file = args[1];

        try {
            new Reporter(port, file).doWork();
        } catch (FileNotFoundException e) {
            System.err.println(e);
        }

    }

}
