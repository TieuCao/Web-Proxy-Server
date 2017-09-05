package com.thaitien.proxy.gui.panel;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.thaitien.proxy.core.model.AntPath;
import com.thaitien.proxy.core.model.HttpMethod;
import com.thaitien.proxy.core.model.RequestInterceptorModel;
import com.thaitien.proxy.core.utils.HttpUtils;
import com.thaitien.proxy.core.utils.StringUtils;
import com.thaitien.proxy.gui.dialog.InputBlackUrlDialog;
import com.thaitien.proxy.gui.model.BlackUrlModel;
import com.thaitien.proxy.gui.model.ParamConfig;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;

public class ConfigPanel implements Initializable, EventHandler<ActionEvent> {
	private Parent parent;
	private ParamConfig paramConfig;
	private @FXML TitledPane pnUrlList;
	private @FXML TitledPane pnPortList;
	private @FXML TextField txtPort;
	private @FXML TextField txtMaxConnect;
	private @FXML ToggleButton btnLogMode;
	private @FXML ToggleButton btnMonitor;
	private @FXML CheckBox chkCookieRequest;
	private @FXML CheckBox chkCookieResponse;
	private @FXML CheckBox chkUrl;
	private @FXML CheckBox chkDestPort;
	private @FXML Button btnUrlNew;
	private @FXML Button btnUrlImport;
	private @FXML Button btnUrlExport;
	private @FXML Button btnDelUrl;
	private @FXML Button btnPortNew;
	private @FXML Button btnDelPort;
	private @FXML TableView<Integer> tblBlackPort;
	private @FXML TableView<BlackUrlModel> tblBlackUrl;
	private @FXML TableColumn<Integer, Integer> colBlackPort;
	private ObservableList<Integer> lstBlackPorts;
	private ObservableList<BlackUrlModel> lstBlackModels;

	public ConfigPanel() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ConfigPanel.fxml"));
		loader.setController(this);
		parent = loader.load();
		paramConfig = ParamConfig.getInstance();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		txtPort.setText(String.valueOf(ParamConfig.PORT_DEFAULT));
		txtMaxConnect.setText(String.valueOf(ParamConfig.MAX_CONNECT_DEFAULT));

		btnMonitor.setSelected(true);
		btnLogMode.setSelected(true);

		lstBlackPorts = tblBlackPort.getItems();
		lstBlackModels = tblBlackUrl.getItems();

		initialBlackUrlTable();
		initialBlackPortTable();

		chkUrl.setOnAction(this);
		chkDestPort.setOnAction(this);
		btnLogMode.setOnAction(this);
		btnMonitor.setOnAction(this);
		btnUrlNew.setOnAction(this);
		btnDelUrl.setOnAction(this);
		btnPortNew.setOnAction(this);
		btnDelPort.setOnAction(this);
		btnUrlImport.setOnAction(this);
		btnUrlExport.setOnAction(this);

		// add listener when components change value
		btnLogMode.selectedProperty()
				.addListener((observable, oldValue, newValue) -> paramConfig.setEnableLog(newValue.booleanValue()));
		btnMonitor.selectedProperty().addListener(
				(ChangeListener<Boolean>) (observable, oldValue, newValue) -> paramConfig.setMonitorRequest(newValue));
		chkCookieRequest.selectedProperty().addListener(
				(observable, oldValue, newValue) -> paramConfig.setRmRequesCookie(newValue.booleanValue()));
		chkCookieResponse.selectedProperty().addListener(
				(observable, oldValue, newValue) -> paramConfig.setRmResponseCookie(newValue.booleanValue()));
		chkUrl.selectedProperty()
				.addListener((observable, oldValue, newValue) -> paramConfig.setFilterByUrl(newValue.booleanValue()));
		chkDestPort.selectedProperty()
				.addListener((observable, oldValue, newValue) -> paramConfig.setFilterByPort(newValue.booleanValue()));

		lstBlackModels.addListener((ListChangeListener<BlackUrlModel>) c -> {
			if (c.next()) {
				// when item is replaced by another item
				if (c.wasReplaced()) {
					int position = c.getFrom();
					BlackUrlModel model = lstBlackModels.get(position);
					paramConfig.getLstSkipRequests().set(position,
							new RequestInterceptorModel(new AntPath(model.getUrl()), model.getBlackMethodList()));
				}

				// when item is added by another item
				else if (c.wasAdded()) {
					paramConfig.getLstSkipRequests().addAll(c.getAddedSubList().stream().filter(t -> {
						t.setChangeListener((observable, oldValue, newValue) -> {
							int index = indexOfListBlackUrlModel(t);

							// to enforce it to replacing case to update list
							if (index != -1)
								lstBlackModels.set(index, lstBlackModels.get(index));
						});
						return true;
					}).map(model -> new RequestInterceptorModel(new AntPath(model.getUrl()),
							model.getBlackMethodList())).collect(Collectors.toList()));
				}

				// when item is remove from list
				else if (c.wasRemoved())
					paramConfig.getLstSkipRequests().remove(c.getFrom());
			}
		});

		lstBlackPorts.addListener((ListChangeListener<Integer>) c -> {
			if (c.next()) {
				if (c.wasAdded())
					paramConfig.getLstSkipPorts().addAll(c.getAddedSubList());
				else if (c.wasRemoved())
					paramConfig.getLstSkipPorts().remove(c.getFrom());
			}
		});

		txtPort.textProperty().addListener((observable, oldValue, newValue) -> {
			try {
				paramConfig.setPort(Integer.parseInt(newValue));
			} catch (Exception e) {
				paramConfig.setPort(-1);
			}
		});

		txtMaxConnect.textProperty().addListener((observable, oldValue, newValue) -> {
			try {
				paramConfig.setMaxConnect(Integer.parseInt(txtMaxConnect.getText()));
			} catch (Exception e) {
				paramConfig.setMaxConnect(-1);
			}
		});
	}

	public Parent getParent() {
		return parent;
	}

	/**
	 * return configuration parameter
	 */
	public ParamConfig getParamConfig() {
		if (parent == null)
			return null;

		try {
			paramConfig.setPort(Integer.parseInt(txtPort.getText()));
		} catch (Exception e) {
			paramConfig.setPort(-1);
		}
		try {
			paramConfig.setMaxConnect(Integer.parseInt(txtMaxConnect.getText()));
		} catch (Exception e) {
			paramConfig.setMaxConnect(-1);
		}

		paramConfig.setEnableLog(btnLogMode.isSelected());
		paramConfig.setRmRequesCookie(chkCookieRequest.isSelected());
		paramConfig.setRmResponseCookie(chkCookieResponse.isSelected());
		paramConfig.setFilterByPort(chkDestPort.isSelected());
		paramConfig.setFilterByUrl(chkUrl.isSelected());

		paramConfig.setLstSkipPorts(lstBlackPorts);
		paramConfig.setLstSkipRequests(lstBlackModels.stream()
				.map(model -> new RequestInterceptorModel(new AntPath(model.getUrl()), model.getBlackMethodList()))
				.collect(Collectors.toList()));

		return paramConfig;
	}

	/**
	 * initial black url table
	 */
	private void initialBlackUrlTable() {
		TableColumn<BlackUrlModel, String> colBlackUrl = new TableColumn<>("URL");
		List<TableColumn<BlackUrlModel, Boolean>> colMethod = new ArrayList<>();

		// create all HTTP method column
		for (HttpMethod method : HttpMethod.values()) {
			TableColumn<BlackUrlModel, Boolean> col = new TableColumn<>(method.getMethod());
			colMethod.add(col);
		}

		// add columns to table
		tblBlackUrl.getColumns().add(colBlackUrl);
		tblBlackUrl.getColumns().addAll(colMethod);

		// indicate the way to display value in table cell
		colBlackUrl.setCellValueFactory(param -> param.getValue().getUrlProperty());
		// set text field for cell when it is double clicked
		colBlackUrl.setCellFactory(param -> new TextFieldTableCell<>(new DefaultStringConverter()));
		// handle event when successful edit cell
		colBlackUrl.setOnEditCommit(event -> {
			String newValue = event.getNewValue();
			String oldValue = event.getOldValue();
			int pos = event.getTablePosition().getRow();

			if (!newValue.equals(oldValue)) {
				for (BlackUrlModel model : lstBlackModels) {
					if (model.getUrl().equals(newValue)) {
						// in case of URL that modification existed because when value is committed, it always keeps
						// value before that so set newValue and setOld to update ui
						lstBlackModels.get(pos).setUrl(newValue);
						lstBlackModels.get(pos).setUrl(oldValue);
						return;
					}
				}
				lstBlackModels.get(pos).setUrl(newValue);
				lstBlackModels.set(pos, lstBlackModels.get(pos));
			}
		});

		for (int i = 0, size = colMethod.size(); i < size; i++) {
			TableColumn<BlackUrlModel, Boolean> col = colMethod.get(i);
			col.setCellValueFactory(
					new Callback<TableColumn.CellDataFeatures<BlackUrlModel, Boolean>, ObservableValue<Boolean>>() {
						int index;

						@Override
						public ObservableValue<Boolean> call(CellDataFeatures<BlackUrlModel, Boolean> param) {
							return param.getValue().getVotes()[index];
						}

						public Callback<TableColumn.CellDataFeatures<BlackUrlModel, Boolean>, ObservableValue<Boolean>> bind(
								int index) {
							this.index = index;
							return this;
						}
					}.bind(i));
			col.setCellFactory(param -> new CheckBoxTableCell<>());
		}
	}

	/**
	 * initial black port table
	 */
	private void initialBlackPortTable() {
		colBlackPort.setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue()).asObject());
		colBlackPort.setCellFactory(new Callback<TableColumn<Integer, Integer>, TableCell<Integer, Integer>>() {

			@Override
			public TableCell<Integer, Integer> call(TableColumn<Integer, Integer> param) {
				return new TextFieldTableCell<>(new StringConverter<Integer>() {

					@Override
					public String toString(Integer object) {
						return object.toString();
					}

					@Override
					public Integer fromString(String string) {
						try {
							return Integer.valueOf(string);
						} catch (Exception e) {
							return new Integer(-1);
						}
					}
				});
			}
		});
		colBlackPort.setOnEditCommit(event -> {
			Integer oldValue = event.getOldValue();
			Integer newValue = event.getNewValue();

			int pos = event.getTablePosition().getRow();
			if (oldValue.intValue() != newValue.intValue()) {
				if (newValue.intValue() < 0 || lstBlackPorts.contains(newValue))
					lstBlackPorts.set(pos, oldValue);
				else
					lstBlackPorts.set(pos, newValue);
			}
		});
	}

	@Override
	public void handle(ActionEvent e) {
		Object source = e.getSource();
		if (source == btnDelUrl)
			handleBtnDeleteUrl();
		else if (source == btnUrlImport)
			handleBtnImportUrl();
		else if (source == btnUrlExport)
			handleBtnExportUrl();
		else if (source == btnUrlNew)
			handleBtnAddUrl();
		else if (source == btnDelPort)
			handleBtnDeletePort();
		else if (source == btnPortNew)
			handleBtnAddPort();
		else if (source == btnLogMode)
			handleBtnLogMode();
		else if (source == btnMonitor)
			handleBtnMonitor();
		else if (source == chkUrl)
			handleChkUrl();
		else if (source == chkDestPort)
			handleChkDestPort();
	}

	/**
	 * handle when destination port checkbox is clicked
	 */
	private void handleChkDestPort() {
		if (chkDestPort.isSelected()) {
			pnPortList.setExpanded(true);
			pnPortList.setDisable(false);
		} else {
			pnPortList.setExpanded(false);
			pnPortList.setDisable(true);
		}
	}

	/**
	 * handle when URL checkbox is clicked
	 */
	private void handleChkUrl() {
		if (chkUrl.isSelected()) {
			pnUrlList.setExpanded(true);
			pnUrlList.setDisable(false);
		} else {
			pnUrlList.setExpanded(false);
			pnUrlList.setDisable(true);
		}
	}

	private void handleBtnMonitor() {
		btnMonitor.getStyleClass().remove(btnLogMode.getStyleClass().size() - 1);

		if (btnMonitor.isSelected()) {
			btnMonitor.setText("ON");
			btnMonitor.getStyleClass().add("btn-success");
		} else {
			btnMonitor.setText("OFF");
			btnMonitor.getStyleClass().add("btn-danger");
		}
	}

	/**
	 * handle when Log Model button is clicked
	 */
	private void handleBtnLogMode() {
		btnLogMode.getStyleClass().remove(btnLogMode.getStyleClass().size() - 1);

		if (btnLogMode.isSelected()) {
			btnLogMode.setText("ON");
			btnLogMode.getStyleClass().add("btn-success");
		} else {
			btnLogMode.setText("OFF");
			btnLogMode.getStyleClass().add("btn-danger");
		}
	}

	/**
	 * handle when Import URL button is clicked
	 */
	private void handleBtnImportUrl() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new ExtensionFilter("TXT files (*.txt)", "*.txt"));
		File file = fileChooser.showOpenDialog(parent.getScene().getWindow());

		if (file != null) {
			try {
				List<RequestInterceptorModel> lstRequest = HttpUtils.getListRequestInterceptorModel(file);
				lstBlackModels.addAll(lstRequest.stream().map(t -> {
					if (t.getLstMethods() == null)
						return new BlackUrlModel(t.getAntPath().getAntPath(), true);

					BlackUrlModel model = new BlackUrlModel(t.getAntPath().getAntPath(), false);
					for (HttpMethod method : t.getLstMethods()) {
						model.setVote(method, true);
					}
					return model;
				}).collect(Collectors.toList()));
			} catch (Exception e) {
				buildAlertDialog(AlertType.ERROR, "Error", e.getMessage()).show();
				return;
			}
		}
	}

	/**
	 * handle when export URL button is clicked
	 */
	private void handleBtnExportUrl() {
		if(lstBlackModels.isEmpty()) {
			buildAlertDialog(AlertType.INFORMATION, "Information", "No data to export").show();
			return;
		}
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new ExtensionFilter("TXT files (*.txt)", "*.txt"));
		File file = fileChooser.showSaveDialog(parent.getScene().getWindow());

		if (file != null) {
			List<RequestInterceptorModel> lstRequests = lstBlackModels.stream()
					.map(model -> new RequestInterceptorModel(new AntPath(model.getUrl()), model.getBlackMethodList()))
					.collect(Collectors.toList());
			HttpUtils.writeListRequestInterceptorModelToFile(file, lstRequests);
		}
	}

	/**
	 * handle when Add URL button is clicked
	 */
	private void handleBtnAddUrl() {
		InputBlackUrlDialog dialog = new InputBlackUrlDialog();
		Optional<BlackUrlModel> model = dialog.showAndWait();

		model.ifPresent(blackUrl -> {
			String url = blackUrl.getUrl();
			if (!StringUtils.isEmpty(url)) {
				for (BlackUrlModel model1 : lstBlackModels) {
					if (model1.getUrl().equals(url))
						return;
				}
				lstBlackModels.add(blackUrl);
			}
		});
	}

	/**
	 * handle when Delete URL button is clicked
	 */
	private void handleBtnDeleteUrl() {
		int index = tblBlackUrl.getSelectionModel().getSelectedIndex();
		if (index != -1)
			lstBlackModels.remove(index);
	}

	/**
	 * handle when Add Port button is clicked
	 */
	private void handleBtnAddPort() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Input Port Dialog");
		dialog.setHeaderText(null);
		dialog.setContentText("Please enter the port that want to filter: ");

		Optional<String> result = dialog.showAndWait();
		result.ifPresent(p -> {
			try {
				Integer port = Integer.valueOf(p);
				if (port.intValue() <= 0) {
					buildAlertDialog(AlertType.WARNING, "Warning", "Not valid number's format").show();
					return;
				}
				if (!lstBlackPorts.contains(port))
					lstBlackPorts.add(port);
			} catch (Exception e) {
				buildAlertDialog(AlertType.WARNING, "Warning", "Not valid number's format").show();
			}
		});
	}

	/**
	 * handle when Delete Port button is clicked
	 */
	private void handleBtnDeletePort() {
		int index = tblBlackPort.getSelectionModel().getSelectedIndex();
		if (index != -1)
			lstBlackPorts.remove(index);
	}

	/**
	 * build alert dialog
	 */
	private Alert buildAlertDialog(AlertType type, String title, String message) {
		Alert alert = new Alert(type);
		alert.setAlertType(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		return alert;
	}

	/**
	 * find index of black url model in list of black url model
	 * 
	 * @return -1 if no exist
	 */
	private int indexOfListBlackUrlModel(BlackUrlModel model) {
		for (int i = 0, size = lstBlackModels.size(); i < size; i++) {
			if (lstBlackModels.get(i).getUrl().equals(model.getUrl()))
				return i;
		}
		return -1;
	}

}
