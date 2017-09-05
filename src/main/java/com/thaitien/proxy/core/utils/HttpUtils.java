package com.thaitien.proxy.core.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.thaitien.proxy.core.model.AntPath;
import com.thaitien.proxy.core.model.HttpMethod;
import com.thaitien.proxy.core.model.RequestInterceptorModel;

public class HttpUtils {

	/**
	 * Get hostname from full url
	 * 
	 */
	public static String getHostNameFromUrl(String url) {
		int posHttp = url.indexOf("http://");
		posHttp = (posHttp == -1 ? 0 : 7);
		int posFirstDash = url.indexOf("/", posHttp);

		String hostName;
		if (posFirstDash == -1)
			hostName = url.substring(posHttp, url.length());
		else
			hostName = url.substring(posHttp, posFirstDash);
		return hostName.split(":")[0];
	}

	/**
	 * Get uri from full url
	 */
	public static String getUriFromUrl(String url) {
		int posHttp = url.indexOf("http://");
		posHttp = (posHttp == -1 ? 0 : 7);
		int posFirstDash = url.indexOf("/", posHttp);
		return posFirstDash == -1 ? "/" : url.substring(posFirstDash, url.length());
	}

	/**
	 * Get port from full url
	 */
	public static int getPortFromUrl(String url) {
		int posHttp = url.indexOf("http://");
		posHttp = (posHttp == -1 ? 0 : 7);
		int posFirstDash = url.indexOf("/", posHttp);

		String hostName;
		if (posFirstDash == -1)
			hostName = url.substring(posHttp, url.length());
		else
			hostName = url.substring(posHttp, posFirstDash);

		String[] tokens = hostName.split(":");
		return tokens.length > 1 ? Integer.parseInt(tokens[tokens.length - 1]) : 80;
	}

	/**
	 * close connection
	 */
	public static void closeConnection(InputStream input) {
		try {
			if (input != null)
				input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * close connection
	 */
	public static void closeConnection(OutputStream output) {
		try {
			if (output != null)
				output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * close connection
	 */
	public static void closeConnection(Socket socket) {
		try {
			if (socket != null && socket.isClosed())
				socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String unEscapeString(String s) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++)
			switch (s.charAt(i)) {
				case '\n':
					sb.append("\\n\n");
					break;
				case '\r':
					sb.append("\\r");
					break;
				default:
					sb.append(s.charAt(i));
			}
		return sb.toString();
	}

	public static List<RequestInterceptorModel> getListRequestInterceptorModel(File file) throws Exception {
		return Files.readAllLines(file.toPath()).stream().map(t -> {
			String[] tokens = t.split("[|]");
			AntPath antPath = new AntPath(tokens[0]);
			List<HttpMethod> lstMethods = null;

			if (tokens.length > 1) {
				lstMethods = new ArrayList<>();
				for (int i = 1; i < tokens.length; i++) {
					try {
						HttpMethod method = HttpMethod.valueOf(tokens[i].toUpperCase());
						lstMethods.add(method);
					} catch (Exception e) {
					}
				}
			}
			return new RequestInterceptorModel(antPath, lstMethods);
		}).collect(Collectors.toList());
	}

	public static void writeListRequestInterceptorModelToFile(File file, List<RequestInterceptorModel> lstRequests) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			StringBuilder builder = new StringBuilder();

			if (lstRequests != null) {
				for (RequestInterceptorModel model : lstRequests) {
					builder.append(model.getAntPath().getAntPath());

					List<HttpMethod> lstMethods = model.getLstMethods();
					for (int i = 0, size = lstMethods.size(); i < size; i++) {
						builder.append('|');
						builder.append(lstMethods.get(i).getMethod());
					}
					builder.append('\n');
				}
				writer.write(builder.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
