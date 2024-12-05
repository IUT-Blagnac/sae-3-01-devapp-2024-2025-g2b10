package java_iot.classes;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

/**
 * <p>PaneCloner is a utility class that deepcopies container panes.
 * <p>Since JavaFX doesn't provide a way to dynamically clone Nodes, for now all the 
    different types of alerts must be hard-coded so that each type of clonable panes
    will only provide what they are told to provide.
 * <p> This class is expected to work only with : 
 * <ul>
 * <li> The settings pane containers
 * <li> The Alerts pane containers
 * </ul>
 * <p><b>Any extra operations WILL NOT be handled unless specifically needed to</b>
 * <p>This class is not meant to be instantialized. It doesn't store any data and you should only
    use it to call its methods.
 * 
 *  @author ESTIENNE Alban-Moussa, Romy Chauvière, ChatGPT 4o Mini (Thanks for the help)
 */
public interface PaneCloner {

    public static Pane cloneSettingPane(Pane originalPane) {
        Pane newPane = new Pane();

        // Copy the children nodes (Label, TextField, Button)
        for (Node node : originalPane.getChildren()) {
            Node clonedNode = cloneNode(node);
            newPane.getChildren().add(clonedNode);
        }

        // Optionally, copy layout properties, styles, etc.
        newPane.setStyle(originalPane.getStyle());
        newPane.setScaleX(originalPane.getScaleX());
        newPane.setScaleY(originalPane.getScaleY());
        newPane.setPrefWidth(originalPane.getPrefWidth());
        newPane.setPrefHeight(originalPane.getPrefHeight());

        // Return the cloned Pane
        return newPane;
    }

    private static Node cloneNode(Node originalNode) {

        Node clonedNode = null;
        
        if (originalNode != null) {
            // Clone basic properties of each node
            clonedNode = originalNode;

            if (originalNode instanceof Label) {
                Label originalLabel = (Label) originalNode;
                clonedNode = new Label(originalLabel.getText());
                ((Label) clonedNode).setFont(originalLabel.getFont()); // Copy font
                ((Label) clonedNode).setTextFill(originalLabel.getTextFill());
                ((Label) clonedNode).setStyle(originalLabel.getStyle()); // Copy style (e.g., color, size)
                ((Label) clonedNode).setPrefHeight(originalLabel.getPrefHeight());
                ((Label) clonedNode).setPrefWidth(originalLabel.getPrefWidth());

                ((Label) clonedNode).setLayoutX(originalLabel.getLayoutX()); // Copy position (if any)
                ((Label) clonedNode).setLayoutY(originalLabel.getLayoutY());
                ((Label) clonedNode).setScaleX(originalLabel.getScaleX());
                ((Label) clonedNode).setScaleY(originalLabel.getScaleY());


                return clonedNode;
            } 
            else if (originalNode instanceof TextField) {
                // Clone TextField (shallow copy, copying text property here)
                TextField originalTextField = (TextField) originalNode;
                clonedNode = new TextField(originalTextField.getText()); // Copy the text value
                ((TextField) clonedNode).setFont(originalTextField.getFont()); // Copy font
                ((TextField) clonedNode).setStyle(originalTextField.getStyle()); // Copy style (e.g., color, size)
                ((TextField) clonedNode).setPrefHeight(originalTextField.getPrefHeight());
                ((TextField) clonedNode).setPrefWidth(originalTextField.getPrefWidth());

                ((TextField) clonedNode).setLayoutX(originalTextField.getLayoutX()); // Copy position (if any)
                ((TextField) clonedNode).setLayoutY(originalTextField.getLayoutY());
                ((TextField) clonedNode).setScaleX(originalTextField.getScaleX());
                ((TextField) clonedNode).setScaleY(originalTextField.getScaleY());


                return clonedNode;
            }
            else if (originalNode instanceof Button) {
                // Clone Button (for example, you can copy the text or action handler)
                Button originalButton = (Button) originalNode;
                clonedNode = new Button(originalButton.getText()); // Copy the text, you can add more properties if needed
                ((Button) clonedNode).setFont(originalButton.getFont()); // Copy font
                ((Button) clonedNode).setStyle(originalButton.getStyle()); // Copy style

                ((Button) clonedNode).setPrefHeight(originalButton.getPrefHeight());
                ((Button) clonedNode).setPrefWidth(originalButton.getPrefWidth());

                ((Button) clonedNode).setLayoutX(originalButton.getLayoutX()); // Copy position (if any)
                ((Button) clonedNode).setLayoutY(originalButton.getLayoutY());
                ((Button) clonedNode).setScaleX(originalButton.getScaleX());
                ((Button) clonedNode).setScaleY(originalButton.getScaleY());
                
                if (originalButton.getGraphic() instanceof ImageView) {
                    ImageView originalImageView = (ImageView) originalButton.getGraphic();
                    ImageView clonedImageView = new ImageView(originalImageView.getImage()); // Clone the image
                    clonedImageView.setFitWidth(originalImageView.getFitWidth()); // Preserve size
                    clonedImageView.setFitHeight(originalImageView.getFitHeight());
                    clonedImageView.setPreserveRatio(originalImageView.isPreserveRatio());
                    clonedImageView.setSmooth(originalImageView.isSmooth());

                    ((Button) clonedNode).setGraphic(clonedImageView); // Set the cloned image view
                }

                return clonedNode;
            }
        }

        return clonedNode;
    }
}