package com.thaitien.proxy.core.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.thaitien.proxy.core.intercept.CookiesInterceptor;
import com.thaitien.proxy.core.intercept.RequestInterceptor;
import com.thaitien.proxy.core.utils.HttpUtils;

final class RequestHandler extends Observable implements Runnable {
	public static final String BEGIN_HANDLER = "begin handler";
	public static final String FINISH_HANDLER = "finished handler";
	private static Logger logger = LogManager.getLogger(RequestHandler.class);

	private ExecutorService pool;
	private String url, host, uri, method;
	private int port, length;
	private byte[] data;
	private Socket cSocket, sSocket;
	private InputStream cInput, sInput;
	private OutputStream cOutput, sOutput;
	private RequestInterceptor requestInterceptor;
	private CookiesInterceptor cookiesInterceptor;

	public RequestHandler(Socket cSocket, RequestInterceptor requestInterceptor, CookiesInterceptor cookiesInterceptor)
			throws IOException {
		this.cSocket = cSocket;
		this.cInput = cSocket.getInputStream();
		this.cOutput = cSocket.getOutputStream();
		this.requestInterceptor = requestInterceptor;
		this.cookiesInterceptor = cookiesInterceptor;
	}

	public RequestHandler(Socket cSocket) throws IOException {
		this(cSocket, null, null);
	}

	@Override
	public void run() {
		try {
			// buffer contain maximum 2048 byte of reading data first time to
			// find informations connection method, hostname and then
			// continuously transfer to server
			data = new byte[2048];
			length = cInput.read(data);

			if (length != -1) {
				method = getConnectionType(data, length);
				url = getFullUrl(data, length);
				host = HttpUtils.getHostNameFromUrl(url);
				port = HttpUtils.getPortFromUrl(url);
				uri = HttpUtils.getUriFromUrl(url);

				setChanged();
				notifyObservers(new Object[] { BEGIN_HANDLER, url });

				if (requestInterceptor != null && requestInterceptor.isHandleRequest(method, url, host, port, uri)) {
					requestInterceptor.handleWhenAccepted(method, url, host, port, uri);

					if ("CONNECT".equals(method)) {
						cOutput.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
						cOutput.flush();
					}

					// create socket to server
					sSocket = new Socket(host, port);
					sInput = sSocket.getInputStream();
					sOutput = sSocket.getOutputStream();

					cSocket.setTcpNoDelay(true);
					sSocket.setTcpNoDelay(true);

					pool = Executors.newFixedThreadPool(2);
					// begin transferring data between client and data
					new WebDataTransfer().start();

				} else { // in the case the request is blocked
					cOutput.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
					cOutput.flush();

					if (requestInterceptor != null)
						requestInterceptor.handleWhenBlocked(method, url, host, port, uri);
				}
			} else { // in the case no data to read
				cOutput.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
				cOutput.flush();
				closeConnection();
				return;
			}
		} catch (Exception e) {
			logger.debug(e);
		} finally {
			closeConnection();
			closeThreadPool(pool);

			setChanged();
			notifyObservers(new Object[] { FINISH_HANDLER, url });
		}
	}

	/**
	 * Get connection type in header request. Example: GET, POST, CONNECTION ...
	 * 
	 * @param data
	 *            bytes data of header request
	 * @param length
	 *            length data that is readed in header request
	 * @return type of connection
	 */
	private String getConnectionType(byte[] data, int length) {
		if (length > data.length)
			throw new IllegalArgumentException("Number of byte reading exceed data's length");

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < length && data[i] != ' '; i++) {
			builder.append((char) data[i]);
		}
		return builder.toString();
	}

	/**
	 * Get full URL from header request
	 */
	private String getFullUrl(byte[] data, int length) {
		if (length > data.length)
			throw new IllegalArgumentException("Number of byte reading exceed data's length");

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (data[i] == ' ') {
				for (int j = i + 1; j < length && data[j] != ' '; j++) {
					builder.append((char) data[j]);
				}
				break;
			}
		}
		return builder.toString();
	}

	public void closeConnection() {
		HttpUtils.closeConnection(sInput);
		HttpUtils.closeConnection(sOutput);
		HttpUtils.closeConnection(sSocket);
		HttpUtils.closeConnection(cInput);
		HttpUtils.closeConnection(cOutput);
		HttpUtils.closeConnection(cSocket);
	}

	/**
	 * closing thread pool
	 * 
	 * @param pool
	 */
	private void closeThreadPool(ExecutorService pool) {
		if (pool != null)
			pool.shutdownNow();
	}

	private class WebDataTransfer {

		/**
		 * read data from browser and transfer to server. Replacing row that contain "Proxy-Connection: ..." to
		 * "Connection: close" and still replace information to suite header in HTTP request
		 */
		private void transferDataFromClientToServer() {
			try {
				if (!"CONNECT".equals(method)) {
					int[][] posExcept = new int[3][2];
					posExcept[0] = getPositionOfRowContainKeyWord(data, length, method);
					posExcept[1] = getPositionOfRowContainKeyWord(data, length, "Proxy-Connection:");
					posExcept[2] = getPositionOfRowContainKeyWord(data, length, "Cookie:");

					for (int i = 0; i < length; i++) {
						if (posExcept[0][0] <= i && i <= posExcept[0][1]) {
							sOutput.write(String.format("%s %s HTTP/1.1\r\n", method, uri).getBytes());
							i = posExcept[0][1];
						} else if (posExcept[1][0] <= i && i <= posExcept[1][1]) {
							sOutput.write("Connection: close\r\n".getBytes());
							i = posExcept[1][1];
						} else if (posExcept[2][0] <= i && i <= posExcept[2][1] && cookiesInterceptor != null
								&& cookiesInterceptor.isRemoveCookiesInRequest()) {
							cookiesInterceptor.handleCookieRemoveInRequest(
									new String(data, posExcept[2][0] + 8, posExcept[2][1] - posExcept[2][0] + 1));
							i = posExcept[2][1];
						} else {
							sOutput.write(data[i]);
						}
					}
				}

				byte[] buffer = new byte[8192];
				for (int n = -1; (n = cInput.read(buffer)) > 0;) {
					sOutput.write(buffer, 0, n);
				}
				sOutput.flush();
			} catch (Exception e) {
			} finally {
				HttpUtils.closeConnection(sOutput);
				HttpUtils.closeConnection(cInput);
			}
		}

		/**
		 * read data from server and transfer again to browser
		 */
		private void transferDataFromServerToClient() {
			try {
				byte[] buffer = new byte[8192];
				if (!"CONNECT".equals(method) && cookiesInterceptor.isRemoveCookiesInResponse()) {
					int length = sInput.read(buffer);

					List<int[]> lstSkips = getMultiPositionOfRowContainKeyWord(buffer, length, "Set-Cookie:");
					L1: for (int i = 0; i < length; i++) {
						for (int[] p : lstSkips) {
							if (p[0] <= i && i <= p[1]) {
								i = p[1];
								// p[0] + 12 = p[0] + 11 + 1. In there, 11 is characters length of "Set-Cookie:" word
								cookiesInterceptor.handleCookieRemoveInResponse(
										new String(buffer, p[0] + 12, p[1] - p[0] - 12 + 1));
								continue L1;
							}
						}
						cOutput.write(buffer[i]);
					}
				}

				for (int nRead = -1; (nRead = sInput.read(buffer)) > 0;) {
					cOutput.write(buffer, 0, nRead);
				}
				cOutput.flush();
			} catch (Exception e) {
			} finally {
				HttpUtils.closeConnection(sInput);
				HttpUtils.closeConnection(cOutput);
			}
		}

		/**
		 * initial thread pool for transformation between browser and server
		 * 
		 * @throws Exception
		 */
		public void start() {
			Future<?> tranfer2Server = pool.submit(() -> transferDataFromClientToServer());
			Future<?> tranfer2Client = pool.submit(() -> transferDataFromServerToClient());

			try {
				tranfer2Server.get();
			} catch (Exception e) {
			}

			try {
				tranfer2Client.get();
			} catch (Exception e) {
			}

		}

		/**
		 * find first index of keyword in data
		 * 
		 * @param data
		 * @param length
		 * @param keyword
		 * @return an array contain position pair [from index, to index]
		 */
		private int[] getPositionOfRowContainKeyWord(byte[] data, int length, String keyword) {
			int[] position = null;

			L1: for (int i = 0; i < length; i++) {
				if (data[i] == keyword.charAt(0)) {
					for (int j = 0; j < keyword.length(); j++) {
						if (data[i + j] != keyword.charAt(j))
							continue L1;
					}

					int k = i;
					for (; k < length && data[k] != '\n'; k++) {
					}
					position = new int[2];
					position[0] = i;
					position[1] = k;
					break;
				}
			}
			return position == null ? new int[] { -1, -1 } : position;
		}

		/**
		 * find all position contain key in data
		 * 
		 * @param data
		 * @param length
		 * @param keyword
		 * @return an array contain multiple position pair [from index, to index]
		 */
		private List<int[]> getMultiPositionOfRowContainKeyWord(byte[] data, int length, String keyword) {
			ArrayList<int[]> lstSkips = new ArrayList<>();

			L1: for (int i = 0; i < length; i++) {
				if (data[i] == keyword.charAt(0)) {
					for (int j = 0; j < keyword.length(); j++) {
						if (data[i + j] != keyword.charAt(j))
							continue L1;
					}

					int k = i;
					for (; k < length && data[k] != '\n'; k++) {
					}

					lstSkips.add(new int[] { i, k });
					i = k;
				}
			}
			return lstSkips;
		}
	}
}
