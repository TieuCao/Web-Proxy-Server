package com.thaitien.proxy.gui.panel;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.thaitien.proxy.core.intercept.ConnectionInterceptor;
import com.thaitien.proxy.core.intercept.impl.SavingCookiesInterceptor;
import com.thaitien.proxy.core.intercept.impl.SavingRequestInterceptor;
import com.thaitien.proxy.core.server.ProxyServer;
import com.thaitien.proxy.db.connection.DBConnection;
import com.thaitien.proxy.db.service.DBHelper;
import com.thaitien.proxy.gui.model.ParamConfig;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainPanel extends Application implements Observer, Initializable, EventHandler<ActionEvent> {
	private static Logger logger = LogManager.getLogger(MainPanel.class);

	private @FXML Label lblStatus;
	private @FXML Label lblPort;
	private @FXML Label lblConCount;
	private @FXML ToggleButton btnStart;
	private @FXML Button btnConfigPane;
	private @FXML Button btnReportPane;
	private @FXML StackPane pnDisplay;
	private ConfigPanel configPanel;
	private MonitorPanel monitorPanel;
	private ProxyServer proxyServer;
	private ExecutorService pool;
	private SavingRequestInterceptor requestInterceptor;
	private SavingCookiesInterceptor cookieInterceptor;
	private CustomConnectionInterceptor connectInterceptor;
	private Button btnOld;

	@Override
	public void start(Stage primaryStage) throws Exception {
		configPanel = new ConfigPanel();
		monitorPanel = new MonitorPanel();

		ParamConfig.getInstance().addObserver(this);

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainPanel.fxml"));
		loader.setController(this);

		Parent pnMain = loader.load();
		primaryStage.setScene(new Scene(pnMain));
		primaryStage.setTitle("Web Proxy Server");

		primaryStage.setOnCloseRequest(event -> {
			if (proxyServer != null)
				proxyServer.close();
			if (pool != null) {
				pool.shutdown();
				try {
					pool.awaitTermination(200, TimeUnit.MILLISECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				pool.shutdownNow();
			}
		});
		primaryStage.show();

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initialInterceptor();
		pool = Executors.newSingleThreadExecutor();

		btnStart.setOnAction(this);
		btnConfigPane.setOnAction(this);
		btnReportPane.setOnAction(this);

		pnDisplay.getChildren().setAll(configPanel.getParent());
	}

	@Override
	public void handle(ActionEvent event) {
		Object source = event.getSource();
		if (source == btnConfigPane)
			handleBtnConfigPane();
		else if (source == btnReportPane)
			handleBtnReportPane();
		else if (source == btnStart)
			hanleBtnStartProxyServer();

	}

	private void initialInterceptor() {
		cookieInterceptor = new SavingCookiesInterceptor();
		requestInterceptor = new SavingRequestInterceptor();
		connectInterceptor = new CustomConnectionInterceptor();
	}

	private void hanleBtnStartProxyServer() {
		btnStart.getStyleClass().remove(btnStart.getStyleClass().size() - 1);
		lblStatus.getStyleClass().remove(lblStatus.getStyleClass().size() - 1);

		if (btnStart.isSelected()) {
			ParamConfig paramConfig = ParamConfig.getInstance();
			int port = paramConfig.getPort();
			int maxConnect = paramConfig.getMaxConnect();

			btnStart.setText("STOP");
			btnStart.getStyleClass().add("btn-danger");
			lblPort.setText(String.valueOf(port));

			if (port <= 0) {
				buildAlertDialog(AlertType.WARNING, "Warning", "Port's range is [1 - 65535]").show();
				switchProxyServerToStop();
				return;
			} else if (maxConnect <= 0) {
				buildAlertDialog(AlertType.WARNING, "Warning", "Max connection must be greater than 0").show();
				switchProxyServerToStop();
				return;
			}

			lblStatus.setText("ACTIVE");
			lblStatus.getStyleClass().add("text-success");

			pool.submit(() -> {
				try {
					proxyServer = new ProxyServer(port, maxConnect);
					proxyServer.setConnectInterceptor(connectInterceptor);
					proxyServer.setRequestInterceptor(requestInterceptor);
					proxyServer.setCookiesInterceptor(cookieInterceptor);

					proxyServer.start();
				} catch (Exception e) {
					Platform.runLater(() -> {
						switchProxyServerToStop();
						buildAlertDialog(AlertType.WARNING, "Warning",
								"Port is used by another process\nPlease use another port").show();
					});
				}
			});
		} else {
			switchProxyServerToStop();
			proxyServer.close();
		}
	}

	private void handleBtnConfigPane() {
		if (checkChangeClickButton(btnConfigPane)) {
			pnDisplay.getChildren().setAll(configPanel.getParent());
			btnConfigPane.getStyleClass().add("configPaneBright");
			btnReportPane.getStyleClass().remove("configReportBright");
		}
	}

	private void handleBtnReportPane() {
		if (checkChangeClickButton(btnReportPane)) {
			pnDisplay.getChildren().setAll(monitorPanel.getParent());
			btnReportPane.getStyleClass().add("configReportBright");
			btnConfigPane.getStyleClass().remove("configPaneBright");
		}
	}

	private void switchProxyServerToStop() {
		btnStart.setSelected(false);
		btnStart.setText("START");
		btnStart.getStyleClass().add("btn-success");

		lblStatus.setText("INACTIVE");
		lblStatus.getStyleClass().add("text-danger");

		lblPort.setText("N/A");
	}

	private Alert buildAlertDialog(AlertType type, String title, String message) {
		Alert alert = new Alert(type);
		alert.setAlertType(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		return alert;
	}

	@Override
	public void update(Observable o, Object arg) {
		ParamConfig paramConfig = ParamConfig.getInstance();

		String typeChange = (String) arg;

		switch (typeChange) {

			case ParamConfig.MONITOR_CURRENT_REQUEST:
				connectInterceptor.setInterceptConnection(paramConfig.isMonitorRequest());
				if (!paramConfig.isEnableLog()) {
					monitorPanel.removeAllUrl();
					lblConCount.setText("N/A");
				}
				break;

			case ParamConfig.LOG_MODE:
				requestInterceptor.setWriteLog(paramConfig.isEnableLog());
				break;

			case ParamConfig.REMOVE_COOKIE_IN_REQUEST:
				cookieInterceptor.setRemoveInRequest(paramConfig.isRmRequestCookie());
				break;

			case ParamConfig.REMOVE_COOKIE_IN_RESPONSE:
				cookieInterceptor.setRemoveInResponse(paramConfig.isRmResponseCookie());
				break;

			case ParamConfig.FILTER_BY_URL:
				if (paramConfig.isFilterByUrl())
					requestInterceptor.setLstRequests(paramConfig.getLstSkipRequests());
				else
					requestInterceptor.setLstRequests(null);
				break;

			case ParamConfig.FILTER_BY_PORT:
				if (paramConfig.isFilterByPort())
					requestInterceptor.setLstPorts(paramConfig.getLstSkipPorts());
				else
					requestInterceptor.setLstPorts(null);
				break;

			default:
				break;
		}
	}

	private boolean checkChangeClickButton(Button btnNew) {
		if (btnNew != btnOld) {
			btnOld = btnNew;
			return true;
		}
		return false;
	}

	private class CustomConnectionInterceptor implements ConnectionInterceptor {
		private boolean isEnable = true;

		public void setInterceptConnection(boolean isEnable) {
			this.isEnable = isEnable;
		}

		@Override
		public boolean isInterceptConnection() {
			return isEnable;
		}

		@Override
		public void connectIn(String url, int countConnect) {
			Platform.runLater(() -> {
				lblConCount.setText(String.valueOf(countConnect));
				monitorPanel.addUrlToMonitor(url);
			});
		}

		@Override
		public void connectOut(String url, int countConnect) {
			Platform.runLater(() -> {
				lblConCount.setText(String.valueOf(countConnect));
				monitorPanel.removeUrlFromMonitor(url);
			});
		}

	}

	public static void main(String[] args) {
		File logFile = new File(DBConnection.DEFAULT_DATABASE_NAME);
		if (!logFile.exists()) {
			try {
				Files.createFile(logFile.toPath());
				DBHelper.createDatabase(MainPanel.class.getResourceAsStream("/db/database.sql"));
			} catch (Exception e) {
				logger.debug("", e);
			}
		}

		launch(args);
	}
}
