package com.boheco1.dev.integratedaccountingsystem.helpers;

import com.boheco1.dev.integratedaccountingsystem.HomeController;
import com.boheco1.dev.integratedaccountingsystem.objects.ActiveUser;
import com.jfoenix.controls.JFXButton;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;

public class DrawerMenuHelper {
    /**
     * Sets the drawer menu button with the following parameters
     * @param button - the JFXButton itself (from the fxml view)
     * @param icon - the FontIcon to be attached to the button via setGraphic()
     * @param drawerMenuList - the List<JFXButton> of drawer menu buttons. All buttons are stored in an ArrayList so that during the resizing of drawer, all the buttons
     *                       inside the list will also be resized.
     * @param tooltipText - the tooltip text of the menu during mouse hover
     */
    public static void setMenuButton(JFXButton button, FontIcon icon, List<JFXButton> drawerMenuList, String tooltipText) {
        try {
            icon.setIconColor(Paint.valueOf(ColorPalette.WHITE));
            button.setGraphic(icon);
            button.setGraphicTextGap(15);
            button.setTextFill(Paint.valueOf(ColorPalette.WHITE));
            button.setTooltip(new Tooltip(tooltipText));
            button.setPadding(new Insets(10, 0, 10, 20));
            drawerMenuList.add(button);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the drawer menu button with the following parameters
     * @param button - the JFXButton itself (from the fxml view)
     * @param icon - the FontIcon to be attached to the button via setGraphic()
     * @param drawerMenuList - the List<JFXButton> of drawer menu buttons. All buttons are stored in an ArrayList so that during the resizing of drawer, all the buttons
     *                       inside the list will also be resized.
     * @param tooltipText - the tooltip text of the menu during mouse hover
     * @param contentPane - the view at which the content fxml layout is attached
     * @param fxml - the fxml layout that is attached to @contentPane when @button is clicked. This fxml layout should have its own controller for event handling
     */
    public static void setMenuButtonWithView(JFXButton button, FontIcon icon, List<JFXButton> drawerMenuList, String tooltipText, AnchorPane contentPane, String fxml) {
        setMenuButtonWithView(button,icon,drawerMenuList,tooltipText,contentPane,fxml,"",null);
    }

    /**
     * Sets the drawer menu button with the following parameters
     * @param button - the JFXButton itself (from the fxml view)
     * @param icon - the FontIcon to be attached to the button via setGraphic()
     * @param drawerMenuList - the List<JFXButton> of drawer menu buttons. All buttons are stored in an ArrayList so that during the resizing of drawer, all the buttons
     *                       inside the list will also be resized.
     * @param tooltipText - the tooltip text of the menu during mouse hover
     * @param contentPane - the view at which the content fxml layout is attached
     * @param fxml - the fxml layout that is attached to @contentPane when @button is clicked. This fxml layout should have its own controller for event handling
     * @param permission - the permission that the user must possess in order to access this view
     * @param stackPane - the StackPane from the invoking controller used for the AlertDialogBuilder
     */
    public static void setMenuButtonWithView(JFXButton button, FontIcon icon, List<JFXButton> drawerMenuList, String tooltipText, AnchorPane contentPane, String fxml, String permission, StackPane stackPane) {
        try {
            icon.setIconColor(Paint.valueOf(ColorPalette.WHITE));
            button.setGraphic(icon);
            button.setGraphicTextGap(15);
            button.setTextFill(Paint.valueOf(ColorPalette.WHITE));
            button.setTooltip(new Tooltip(tooltipText));
            button.setPadding(new Insets(10, 0, 10, 20));
            drawerMenuList.add(button);

            button.setOnAction(actionEvent -> {
                contentPane.getChildren().setAll(ContentHandler.getNodeFromFxml(HomeController.class, fxml));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the drawer menu button with the following parameters
     * @param button - the JFXButton itself (from the fxml view)
     * @param icon - the FontIcon to be attached to the button via setGraphic()
     * @param drawerMenuList - the List<JFXButton> of drawer menu buttons. All buttons are stored in an ArrayList so that during the resizing of drawer, all the buttons
     *                       inside the list will also be resized.
     * @param tooltipText - the tooltip text of the menu during mouse hover
     * @param contentPane - the view at which the content fxml layout is attached
     * @param fxml - the fxml layout that is attached to @contentPane when @button is clicked. This fxml layout should have its own controller for event handling
     * @param titleHolder - the Label destination of the title
     */
    public static void setMenuButtonWithViewAndSubMenu(JFXButton button, FontIcon icon, List<JFXButton> drawerMenuList, String tooltipText, AnchorPane contentPane, String fxml, FlowPane subToolbar, MenuControllerHandler controller, Label titleHolder) {
        setMenuButtonWithViewAndSubMenu(button, icon, drawerMenuList, tooltipText, contentPane,fxml,subToolbar,controller,"",null, titleHolder);
    }

    /**
     * Sets the drawer menu button with the following parameters
     * @param button - the JFXButton itself (from the fxml view)
     * @param icon - the FontIcon to be attached to the button via setGraphic()
     * @param drawerMenuList - the List<JFXButton> of drawer menu buttons. All buttons are stored in an ArrayList so that during the resizing of drawer, all the buttons
     *                       inside the list will also be resized.
     * @param tooltipText - the tooltip text of the menu during mouse hover
     * @param contentPane - the view at which the content fxml layout is attached
     * @param fxml - the fxml layout that is attached to @contentPane when @button is clicked. This fxml layout should have its own controller for event handling
     * @param permission - the permission that the user must possess in order to access this view
     * @param stackPane - the StackPane from the invoking controller used for the AlertDialogBuilder
     * @param titleHolder - the Label destination of the title
     */
    public static void setMenuButtonWithViewAndSubMenu(JFXButton button, FontIcon icon, List<JFXButton> drawerMenuList, String tooltipText, AnchorPane contentPane, String fxml, FlowPane subToolbar, MenuControllerHandler controller, String permission, StackPane stackPane, Label titleHolder) {
        try {
            icon.setIconColor(Paint.valueOf(ColorPalette.WHITE));
            icon.setIconSize(16);
            button.setGraphic(icon);
            button.setGraphicTextGap(15);
            button.setTextFill(Paint.valueOf(ColorPalette.WHITE));
            button.setTooltip(new Tooltip(tooltipText));
            button.setPadding(new Insets(10, 0, 10, 20));

            drawerMenuList.add(button);

            button.setOnAction(actionEvent -> {
                if(!permission.isEmpty() && !ActiveUser.getUser().can(permission)) {
                    AlertDialogBuilder.messgeDialog("Access Denied", "Sorry! Your account is not permitted to access this page.", stackPane, AlertDialogBuilder.WARNING_DIALOG);
                }else {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            titleHolder.setText(tooltipText);
                            //to trigger which account code to save cv or jv since they are using the same UI and controller
                            if(titleHolder.getText().equals("Check Voucher"))
                                Utility.VOUCHER_TYPE = Utility.CV;
                            else if(titleHolder.getText().equals("Journal Voucher"))
                                Utility.VOUCHER_TYPE = Utility.JV;

                            for (int i = 0; i < drawerMenuList.size(); i++) {
                                drawerMenuList.get(i).getStyleClass().remove("active-menu");
                                FontIcon fontIcon = (FontIcon) drawerMenuList.get(i).getGraphic();
                                fontIcon.setIconColor(Paint.valueOf(ColorPalette.WHITE));
                                drawerMenuList.get(i).setGraphic(fontIcon);
                            }
                            button.getStyleClass().add("active-menu");
                            icon.setIconColor(Paint.valueOf(ColorPalette.MAIN_COLOR));
                            contentPane.getChildren().setAll(ContentHandler.getNodeFromFxml(HomeController.class, fxml, contentPane, subToolbar, titleHolder));
                        }
                    }).run();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void setMenuButtonsIconOnly(List<JFXButton> drawerMenuList) {
        try {
            int size = drawerMenuList.size();
            for (int i=0; i<size; i++) {
                drawerMenuList.get(i).setAlignment(Pos.CENTER);
                drawerMenuList.get(i).setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                drawerMenuList.get(i).setPadding(new Insets(10, 0, 10, 0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setMenuButtonsFullDisplay(List<JFXButton> drawerMenuList) {
        try {
            int size = drawerMenuList.size();
            for (int i=0; i<size; i++) {
                drawerMenuList.get(i).setAlignment(Pos.CENTER_LEFT);
                drawerMenuList.get(i).setContentDisplay(ContentDisplay.LEFT);
                drawerMenuList.get(i).setPadding(new Insets(10, 0, 10, 20));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setMenuLabels(Label label, FontIcon fontIcon, List<Label> labelList) {
        try {
            fontIcon.setIconColor(Paint.valueOf(ColorPalette.WHITE));
            label.setGraphicTextGap(10);
            label.setGraphic(fontIcon);
            label.setPadding(new Insets(10, 0, 10, 10));
            labelList.add(label);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setMenuLabelsIconOnly(List<Label> labelList) {
        try {
            int size = labelList.size();
            for (int i=0; i<size; i++) {
                labelList.get(i).setAlignment(Pos.CENTER);
                labelList.get(i).setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                labelList.get(i).setPadding(new Insets(10, 0, 10, 0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setMenuLabelsFullDisplay(List<Label> labelList) {
        try {
            int size = labelList.size();
            for (int i=0; i<size; i++) {
                labelList.get(i).setAlignment(Pos.CENTER_LEFT);
                labelList.get(i).setContentDisplay(ContentDisplay.LEFT);
                labelList.get(i).setPadding(new Insets(10, 0, 10, 10));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
