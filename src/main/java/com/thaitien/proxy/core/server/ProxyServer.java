package com.thaitien.proxy.core.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.thaitien.proxy.core.intercept.ConnectionInterceptor;
import com.thaitien.proxy.core.intercept.CookiesInterceptor;
import com.thaitien.proxy.core.intercept.RequestInterceptor;
import com.thaitien.proxy.core.intercept.impl.DefaultConnectionInterceptor;
import com.thaitien.proxy.core.intercept.impl.DefaultCookiesInterceptor;
import com.thaitien.proxy.core.intercept.impl.DefaultRequestInterceptor;

/**
 * This class is used to provide a service that can turn on or turn off a web proxy server
 * 
 * @author tien
 *
 */
public final class ProxyServer implements Observer {
	private static Logger logger = LogManager.getLogger(ProxyServer.class);
	public static List<RequestHandler> lstRequestHandlers = Collections.synchronizedList(new ArrayList<>(50));

	private boolean isHalt;
	private ServerSocket serverSocket;
	private ThreadPoolExecutor pool;
	private RequestInterceptor requestInterceptor;
	private CookiesInterceptor cookiesInterceptor;
	private ConnectionInterceptor connectInterceptor;

	public ProxyServer(int port, int poolSizeOfThreads) throws IOException {
		serverSocket = new ServerSocket(port);
		pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(poolSizeOfThreads);
		requestInterceptor = new DefaultRequestInterceptor();
		cookiesInterceptor = new DefaultCookiesInterceptor();
		connectInterceptor = new DefaultConnectionInterceptor();

		logger.info("Web Proxy Server is starting at port [" + port + "]");
	}

	public ProxyServer() throws IOException {
		this(45678, 50);
	}

	public RequestInterceptor getRequestInterceptor() {
		return requestInterceptor;
	}

	public void setRequestInterceptor(RequestInterceptor requestInterceptor) {
		this.requestInterceptor = requestInterceptor;
	}

	public CookiesInterceptor getCookiesInterceptor() {
		return cookiesInterceptor;
	}

	public void setCookiesInterceptor(CookiesInterceptor cookiesInterceptor) {
		this.cookiesInterceptor = cookiesInterceptor;
	}

	public ConnectionInterceptor getConnectInterceptor() {
		return connectInterceptor;
	}

	public void setConnectInterceptor(ConnectionInterceptor connectInterceptor) {
		this.connectInterceptor = connectInterceptor;
	}

	/**
	 * active web proxy server
	 */
	public void start() {
		try {
			while (!isHalt) {
				try {
					RequestHandler requestHandler = new RequestHandler(serverSocket.accept(), requestInterceptor,
							cookiesInterceptor);
					requestHandler.addObserver(this);

					pool.submit(requestHandler);
				} catch (Exception e) {
					logger.debug("", e);
				}
			}
		} catch (Exception e) {
			logger.debug(e);
		} finally {
			closeServer();
			closeThreadPool();
			synchronized (lstRequestHandlers) {
				lstRequestHandlers.forEach(t -> t.closeConnection());
			}
			logger.info("Web Proxy Server was closed");
		}
	}

	/**
	 * Turn off proxy server. This method is not sure about proxy server will immediately close
	 */
	public void close() {
		pool.shutdown();
		isHalt = true;
		closeServer();
	}

	private void closeServer() {
		try {
			if (serverSocket != null && !serverSocket.isClosed())
				serverSocket.close();
		} catch (IOException e) {
			logger.debug(e);
		}
	}

	private void closeThreadPool() {
		try {
			if (pool != null) {
				pool.awaitTermination(100, TimeUnit.MILLISECONDS);
				pool.shutdownNow();
				return;
			}
		} catch (InterruptedException e) {
			logger.debug("", e);
		} finally {
			if (pool != null)
				pool.shutdownNow();
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof RequestHandler) {
			Object[] objs = (Object[]) arg;

			switch ((String) objs[0]) {
				case RequestHandler.BEGIN_HANDLER:
					lstRequestHandlers.add((RequestHandler) o);
					if (connectInterceptor != null && connectInterceptor.isInterceptConnection())
						connectInterceptor.connectIn((String) objs[1], lstRequestHandlers.size());
					break;

				case RequestHandler.FINISH_HANDLER:
					lstRequestHandlers.remove((RequestHandler) o);
					if (connectInterceptor != null && connectInterceptor.isInterceptConnection())
						connectInterceptor.connectOut((String) objs[1], lstRequestHandlers.size());
					break;

				default:
					break;
			}
		}
	}

}
