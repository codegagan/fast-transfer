package com.gagan.client;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

	public static void main(String[] args) throws Exception, IOException {

		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
		// TODO Auto-generated method stub

		String filePath = "C:\\wsgagan\\temp\\fast-transfer-client\\download.zip";

		Socket socket = new Socket("localhost", 8888);
		byte[] write = new byte[1024];
		write[0] = 0;
		byte[] fileName = "ddem-atos.war".getBytes();
		for (int i = 0; i < fileName.length; i++) {
			write[i + 1] = fileName[i];
		}
		for (int j = fileName.length + 1; j < 1024; j++) {
			write[j] = (byte) ' ';
		}
		socket.getOutputStream().write(write);
		socket.getOutputStream().flush();
		System.out.println("written");

		Arrays.fill(write, (byte) 0);
		int clientId = socket.getInputStream().read();
		System.out.println("got client id" + clientId);
		socket.getInputStream().read(write, 0, Long.BYTES);
		ByteBuffer bytebuffer = ByteBuffer.allocate(Long.BYTES).put(write, 0, Long.BYTES);
		bytebuffer.flip();
		long length = bytebuffer.getLong();

		System.out.println("file length " + length);

//		socket.close();

		RandomAccessFile memoryMappedFile = new RandomAccessFile(filePath, "rw");

		// Mapping a file into memory
		MappedByteBuffer out = memoryMappedFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, length);
		// open fixed 5 connections

		long rem = length % 5;
		long eachPart = (length - rem) / 5;

		CompletableFuture allTasks = CompletableFuture.allOf(CompletableFuture.runAsync(() -> {
			byte[] write1 = new byte[1024];
			Arrays.fill(write1, (byte) 0);
			write1[0] = 1;
			write1[1] = (byte) clientId;
			write1[2] = 1;
			Socket socket1;
			try {
				socket1 = new Socket("localhost", 8888);
				socket1.getOutputStream().write(write1);
				socket1.getOutputStream().flush();
				System.out.println("reading part 1");
				long i;
				for (i = 0; i < eachPart; i++) {
					out.put((byte) socket1.getInputStream().read());
				}
				System.out.println("finished reading 1");
				socket1.close();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}, fixedThreadPool),

				CompletableFuture.runAsync(() -> {
					byte[] write1 = new byte[1024];
					Arrays.fill(write1, (byte) 0);
					write1[0] = 1;
					write1[1] = (byte) clientId;
					write1[2] = 2;
					Socket socket1;
					try {
						socket1 = new Socket("localhost", 8888);
						socket1.getOutputStream().write(write1);
						socket1.getOutputStream().flush();
						System.out.println("reading part 2");
						long i;
						for (i = 0; i < eachPart; i++) {
							out.put((int) (eachPart + i), (byte) socket1.getInputStream().read());
						}
						System.out.println("finished reading 2");
						socket1.close();
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}, fixedThreadPool),

				CompletableFuture.runAsync(() -> {
					byte[] write1 = new byte[1024];
					Arrays.fill(write1, (byte) 0);
					write1[0] = 1;
					write1[1] = (byte) clientId;
					write1[2] = 3;
					Socket socket1;
					try {
						socket1 = new Socket("localhost", 8888);
						socket1.getOutputStream().write(write1);
						socket1.getOutputStream().flush();
						System.out.println("reading part 3");
						long i;
						for (i = 0; i < eachPart; i++) {
							out.put((int) (eachPart + eachPart + i), (byte) socket1.getInputStream().read());
						}
						System.out.println("finished reading 3");
						socket1.close();
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}, fixedThreadPool),

				CompletableFuture.runAsync(() -> {
					byte[] write1 = new byte[1024];
					Arrays.fill(write1, (byte) 0);
					write1[0] = 1;
					write1[1] = (byte) clientId;
					write1[2] = 4;
					Socket socket1;
					try {
						socket1 = new Socket("localhost", 8888);
						socket1.getOutputStream().write(write1);
						socket1.getOutputStream().flush();
						System.out.println("reading part 4");
						for (long i = 0; i < eachPart; i++) {
							out.put((int) (eachPart + eachPart + eachPart + i), (byte) socket1.getInputStream().read());
						}
						System.out.println("finished reading 4");
						socket1.close();
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}, fixedThreadPool),

				CompletableFuture.runAsync(() -> {
					byte[] write1 = new byte[1024];
					Arrays.fill(write1, (byte) 0);
					write1[0] = 1;
					write1[1] = (byte) clientId;
					write1[2] = 5;
					Socket socket1;
					try {
						socket1 = new Socket("localhost", 8888);
						socket1.getOutputStream().write(write1);
						socket1.getOutputStream().flush();
						System.out.println("reading part 5");
						long i;
						for (i = 0; i < eachPart + rem; i++) {
							out.put((int) (eachPart + eachPart + eachPart + eachPart + i),
									(byte) socket1.getInputStream().read());
						}
						System.out.println("finished reading 5");
						socket1.close();
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}, fixedThreadPool)

		).thenAccept(x -> {
			try {
				socket.getOutputStream().write(1);
				socket.getOutputStream().flush();
				socket.close();
				memoryMappedFile.close();
				System.out.println("Download completed");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		allTasks.join();
		allTasks.complete(null);
		fixedThreadPool.shutdownNow();

	}

}
