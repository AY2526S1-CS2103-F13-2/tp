package seedu.address.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import seedu.address.model.tag.TagFolder;

/**
 * UI controller for a single Tag Folder row in the sidebar.
 * Binds a {@link TagFolder} to {@code TagFolderCard.fxml}.
 */
public class TagFolderCard {

    @FXML
    private HBox root;

    @FXML
    private Text nameLabel;

    @FXML
    private TextFlow nameWrapper;

    @FXML
    private Label countBadge;

    /**
     * Populates this card with the given folder's data.
     */
    private TagFolder bound; // remember to unbind if reused

    /**
     * Initializes the UI components after they are loaded from FXML.
     */
    @FXML
    public void initialize() {
        nameLabel.wrappingWidthProperty().bind(nameWrapper.widthProperty());
        // Use the same font as the Label (keeps in sync if CSS/font changes)
        nameLabel.fontProperty().bind(countBadge.fontProperty());
        nameLabel.fillProperty().bind(countBadge.textFillProperty());
    }

    public void setFolder(TagFolder folder) {
        if (bound != null) {
            countBadge.textProperty().unbind();
        }
        bound = folder;

        nameLabel.setText(folder.getName());
        countBadge.textProperty().unbind();
        countBadge.textProperty().bind(folder.countProperty().asString());
    }

    public HBox getRoot() {
        return root;
    }
}
