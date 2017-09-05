package com.thaitien.proxy.gui.dialog;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.thaitien.proxy.core.model.HttpMethod;
import com.thaitien.proxy.gui.model.BlackUrlModel;

import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;

public class InputBlackUrlDialog extends Dialog<BlackUrlModel> {

	public InputBlackUrlDialog() {
		Label lblUrl = new Label("URL");
		Label lblMethod = new Label("Method");
		TextField txtUrl = new TextField();
		List<CheckBox> lstChkMethod = Arrays.asList(HttpMethod.values()).stream().map(m -> {
			CheckBox chk = new CheckBox(m.getMethod());
			chk.setSelected(true);
			return chk;
		}).collect(Collectors.toList());

		TilePane pnMethod = new TilePane(5, 10);
		pnMethod.setTileAlignment(Pos.TOP_LEFT);
		pnMethod.getChildren().addAll(lstChkMethod);

		GridPane parent = new GridPane();
		parent.setHgap(10);
		parent.setVgap(10);

		parent.add(lblUrl, 0, 0);
		parent.add(lblMethod, 0, 1);
		parent.add(txtUrl, 1, 0);
		parent.add(pnMethod, 1, 1);

		GridPane.setHgrow(txtUrl, Priority.ALWAYS);
		getDialogPane().setContent(parent);
		getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		setTitle("Dialog Enter Black URL");
		setResultConverter(buttonType -> {
			if (buttonType == ButtonType.OK) {
				BlackUrlModel model = new BlackUrlModel(txtUrl.getText());
				for (int i = 0; i < lstChkMethod.size(); i++) {
					model.setVote(i, lstChkMethod.get(i).isSelected());
				}
				return model;
			}
			return null;
		});
	}

}
