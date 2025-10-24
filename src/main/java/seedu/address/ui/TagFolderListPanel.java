package seedu.address.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.Region;
import seedu.address.model.tag.TagFolder;

/**
 * Displays tag folders in the sidebar. Supports multi-select and calls back with the
 * current selection as a {@code List<TagFolder>}.
 */
public class TagFolderListPanel extends UiPart<Region> {
    private static final String FXML = "TagFolderListPanel.fxml";

    @FXML
    private ListView<TagFolder> folderListView;

    /** Constructor here */
    public TagFolderListPanel(ObservableList<TagFolder> folders,
                              Consumer<List<TagFolder>> onSelect) {
        super(FXML);

        folderListView.setItems(folders.filtered(folder -> folder.getCount() >= 0).sorted());
        folderListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        if (onSelect != null) {
            // Listen to changes in the selected items (ObservableList),
            // and pass a snapshot of the selection to the consumer.
            folderListView.getSelectionModel().getSelectedItems().addListener(
                    (ListChangeListener<TagFolder>) change -> {
                        List<TagFolder> snapshot =
                                new ArrayList<>(folderListView.getSelectionModel().getSelectedItems());
                        onSelect.accept(snapshot);
                    }
            );
        }

        folderListView.setCellFactory(list -> new FolderCell());
    }

    /** Custom ListCell for TagFolder rows. */
    static class FolderCell extends ListCell<TagFolder> {
        @Override
        protected void updateItem(TagFolder folder, boolean empty) {
            super.updateItem(folder, empty);
            if (empty || folder == null) {
                setGraphic(null);
                setText(null);
                return;
            }
            try {
                FXMLLoader loader = new FXMLLoader(
                        FolderCell.class.getResource("/view/TagFolderCard.fxml"));
                Node node = loader.load();
                TagFolderCard controller = loader.getController();
                controller.setFolder(folder); // bind name + count
                setGraphic(node);
                setText(null);
            } catch (IOException e) {
                // Fallback text if FXML fails
                setText(folder.getName() + " (" + folder.getCount() + ")");
                setGraphic(null);
            }
        }
    }
}
