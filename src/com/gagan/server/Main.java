package com.gagan.server;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

	static String filePath = "C:\\wsgagan\\temp\\ddem-atos.war";
	static long length = new File(filePath).length();
	static RandomAccessFile file;
	static MappedByteBuffer mappedByteBuffer;

	static {
		try {
			file = new RandomAccessFile(filePath, "r");
			mappedByteBuffer = file.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, length);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	static long rem = length % 5;
	static int eachPart = (int) (length - rem) / 5;

	static AtomicInteger clientIdCounter = new AtomicInteger(1);

	public static void main(String... args) throws Exception {
		ServerSocket serverSocket = new ServerSocket(8888);
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(50);
		System.out.println("server started");
		while (true) {
			Socket accept = serverSocket.accept();
			CompletableFuture.runAsync(() -> {
				handleNewConnection(accept);
			}, fixedThreadPool);
		}
	}

	private static void handleNewConnection(Socket socket) {
		System.out.println("new connection");
		try {

			byte[] read = new byte[1024];
			int controlValue = socket.getInputStream().read(read);
			if (read[0] == 0) {// new request
				System.out.println("new request");
				String fileName = new String(Arrays.copyOfRange(read, 1, 1023)).trim();
				System.out.println("client asking for file" + fileName);
				int clientId = clientIdCounter.getAndIncrement();
				socket.getOutputStream().write(clientId);
				socket.getOutputStream().write(ByteBuffer.allocate(Long.BYTES).putLong(length).array());
				socket.getOutputStream().flush();
				if (socket.getInputStream().read() == 1) {
					System.out.println("Download completed for client " + clientId);
				}

			} else if (read[0] == 1) {// existing client

				byte clientId = read[1];
				byte filePart = read[2];
				System.out.println("existing client for filepart" + filePart);
				BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
				switch (filePart) {
				case 1: {
					for (int i = 0; i < eachPart; i++) {
						bufferedOutputStream.write(mappedByteBuffer.get(i));

					}
					bufferedOutputStream.flush();
					bufferedOutputStream.close();
					socket.close();
					break;
				}
				case 2: {
					for (int i = 0; i < eachPart; i++) {
						bufferedOutputStream.write(mappedByteBuffer.get(eachPart + i));
					}
					bufferedOutputStream.flush();
					bufferedOutputStream.close();
					socket.close();
					break;
				}
				case 3: {
					for (int i = 0; i < eachPart; i++) {
						bufferedOutputStream.write(mappedByteBuffer.get(eachPart + eachPart + i));
					}
					bufferedOutputStream.flush();
					bufferedOutputStream.close();
					socket.close();
					break;
				}
				case 4: {
					for (int i = 0; i < eachPart; i++) {
						bufferedOutputStream.write(mappedByteBuffer.get(eachPart + eachPart + eachPart + i));
					}
					bufferedOutputStream.flush();
					bufferedOutputStream.close();
					socket.close();
					break;
				}
				case 5: {
					for (int i = 0; i < eachPart + rem; i++) {
						bufferedOutputStream.write(mappedByteBuffer.get(eachPart + eachPart + eachPart + eachPart + i));
					}
					bufferedOutputStream.flush();
					bufferedOutputStream.close();
					socket.close();
					break;
				}
				default:
					throw new RuntimeException("more than 5 streams not supported yet");
				}

				System.out.println("finsihed transfering part " + filePart);

			} else if (read[0] == 2) { // Completion handler
				System.out.println("control arg " + read[0]);

			} else {
				throw new RuntimeException("Invalid control value");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
