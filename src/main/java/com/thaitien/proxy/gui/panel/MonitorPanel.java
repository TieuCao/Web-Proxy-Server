package com.thaitien.proxy.gui.panel;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

public class MonitorPanel {
	private Parent parent;
	private ObservableList<String> lstUrlConnects;

	public MonitorPanel() {
		lstUrlConnects = FXCollections.synchronizedObservableList(FXCollections.<String>observableArrayList());

		TableColumn<String, String> colNo = new TableColumn<>("#");
		TableColumn<String, String> colUrls = new TableColumn<>("URL");

		TableView<String> tblUrlConnect = new TableView<>(lstUrlConnects);
		tblUrlConnect.getColumns().add(colNo);
		tblUrlConnect.getColumns().add(colUrls);

		colNo.setCellFactory(new Callback<TableColumn<String, String>, TableCell<String, String>>() {

			@Override
			public TableCell<String, String> call(TableColumn<String, String> param) {
				return new TableCell<String, String>() {
					@Override
					protected void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						setGraphic(null);
						setText(empty ? null : getIndex() + 1 + "");
					}
				};
			}
		});
		colUrls.setCellValueFactory(param -> new SimpleStringProperty(param.getValue()));
		colUrls.prefWidthProperty().bind(tblUrlConnect.widthProperty());

		parent = new StackPane(tblUrlConnect);
	}

	public Parent getParent() {
		return parent;
	}

	public void addUrlToMonitor(String url) {
		lstUrlConnects.add(url);
	}

	public void removeUrlFromMonitor(String url) {
		lstUrlConnects.remove(url);
	}

	public void removeAllUrl() {
		lstUrlConnects.clear();
	}

}
