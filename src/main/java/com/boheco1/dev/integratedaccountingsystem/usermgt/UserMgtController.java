package com.boheco1.dev.integratedaccountingsystem.usermgt;

import com.boheco1.dev.integratedaccountingsystem.dao.*;
import com.boheco1.dev.integratedaccountingsystem.helpers.AlertDialogBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class UserMgtController extends MenuControllerHandler implements Initializable {
    @FXML JFXComboBox selectUserCombo, selectRoleCombo, selectPermissionCombo,
            selectPermissionForRoleCombo, departmentCombo;

    @FXML JFXTextField userNameField, designationField, phoneNumberField, roleNameField,
            permissionNameField, lastNameField, middleNameField, firstNameField;

    @FXML JFXTextArea roleDescriptionField, permissionDescriptionField, addressField;

    @FXML JFXPasswordField newPasswordField, confirmPasswordField;

    @FXML JFXListView userRolesList, userPermissionsList, rolesList, permissionsList;

    @FXML
    StackPane userMgtStackPane;

    private User currentUser = null;
    private EmployeeInfo currentEmployee = null;
    private Role currentRole = null;
    private Permission currentPermission = null;
    private ObservableList<User> listOfUsers;
    private ObservableList<Department> listOfDepartments;
    private ObservableList<Permission> listOfAvailablePermissions;
    private ObservableList<Permission> listOfUserPermissions;
    private ObservableList<Permission> listOfPermissions;
    private ObservableList<Role> listOfAvailableRoles;
    private ObservableList<Role> listOfUserRoles;
    private ObservableList<Role> listOfRoles;
    private ObservableList<Permission> listOfRolePermissions;

    private static Connection conn;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        try {
            
            conn = DB.getConnection();
            
            listOfUsers = FXCollections.observableArrayList(UserDAO.getAll(conn));
            selectUserCombo.setItems(listOfUsers);

            listOfDepartments = FXCollections.observableArrayList(DepartmentDAO.getAll(conn));
            departmentCombo.setItems(listOfDepartments);

            listOfRoles = FXCollections.observableArrayList(RoleDAO.getAll(conn));
            listOfPermissions = FXCollections.observableArrayList(PermissionDAO.getAll(conn));

            rolesList.setItems(listOfRoles);
            permissionsList.setItems(listOfPermissions);
            selectPermissionForRoleCombo.setItems(listOfPermissions);

            listOfAvailablePermissions = listOfPermissions;
            listOfAvailableRoles = listOfRoles;

            selectPermissionCombo.setItems(listOfAvailablePermissions);
            selectRoleCombo.setItems(listOfAvailableRoles);

        }catch(Exception ex) {
            AlertDialogBuilder.messgeDialog("Exception", ex.getMessage(),userMgtStackPane,AlertDialogBuilder.DANGER_DIALOG);
            ex.printStackTrace();
        }
    }

    public void onSelectUser(ActionEvent ev)
    {
        try {
            this.currentUser = (User)selectUserCombo.getSelectionModel().getSelectedItem();

            if(currentUser==null) return;

            this.currentEmployee = EmployeeDAO.getOne(currentUser.getEmployeeID(), conn);

            renderUser();

        }catch(Exception ex) {
            ex.printStackTrace();
            AlertDialogBuilder.messgeDialog("Exception",ex.getMessage(),userMgtStackPane,AlertDialogBuilder.DANGER_DIALOG);;
        }
    }

    private void renderUser() throws Exception
    {
        userNameField.setText(currentUser.getUserName());
        designationField.setText(currentEmployee.getDesignation());
        phoneNumberField.setText(currentEmployee.getPhone());
        firstNameField.setText(currentEmployee.getEmployeeFirstName());
        middleNameField.setText(currentEmployee.getEmployeeMidName());
        lastNameField.setText(currentEmployee.getEmployeeLastName());
        phoneNumberField.setText(currentEmployee.getPhone());
        addressField.setText(currentEmployee.getEmployeeAddress());
        departmentCombo.getSelectionModel().select(findDepartment(currentEmployee.getDepartmentID()));
        //populate user Permissions
        this.listOfUserPermissions = FXCollections.observableArrayList(PermissionDAO.permissionsOfUser(this.currentUser, conn));
        userPermissionsList.setItems(this.listOfUserPermissions);

        //populate user Roles
        this.listOfUserRoles = FXCollections.observableArrayList(RoleDAO.rolesOfUser(currentUser, conn));
        userRolesList.setItems(listOfUserRoles);

        //populate available permissions and roles
        this.listOfAvailablePermissions = FXCollections.observableArrayList(PermissionDAO.userAvailablePermissions(currentUser, conn));
        this.listOfAvailableRoles = FXCollections.observableArrayList(RoleDAO.userAvailableRoles(currentUser, conn));
        selectPermissionCombo.setItems(listOfAvailablePermissions);
        selectRoleCombo.setItems(listOfAvailableRoles);
    }

    private Department findDepartment(int departmentID)
    {
        for(Department dept: listOfDepartments) {
            if(dept.getDepartmentID()==departmentID) return dept;
        }
        return null;
    }

    public void saveUser()
    {
        try {
            if(currentUser==null) {
                Department dept = (Department) departmentCombo.getSelectionModel().getSelectedItem();

                if(dept==null) {
                    AlertDialogBuilder.messgeDialog("No Department","It is required that an employee be assigned to a department.",userMgtStackPane,AlertDialogBuilder.WARNING_DIALOG);;
                    return;
                }

                currentEmployee = new EmployeeInfo(-1,firstNameField.getText(),middleNameField.getText(),lastNameField.getText(),addressField.getText(),phoneNumberField.getText(),designationField.getText(),dept.getDepartmentID());
                String fullName = currentEmployee.getEmployeeFirstName() + " " + currentEmployee.getEmployeeLastName();
                EmployeeDAO.addEmployee(currentEmployee, conn);

                currentUser = new User(-1, currentEmployee.getId(), userNameField.getText(), fullName);
                currentUser.setPassword("Boheco1");
                UserDAO.addUser(currentUser, conn);

                listOfUsers.add(0, currentUser);
                selectUserCombo.getSelectionModel().select(0);
            }else {
                AlertDialogBuilder.messgeDialog("Unavailable","This feature is not available yet.",userMgtStackPane,AlertDialogBuilder.WARNING_DIALOG);;
            }
        }catch(SQLException ex) {
            ex.printStackTrace();
            AlertDialogBuilder.messgeDialog("Exception",ex.getMessage(),userMgtStackPane,AlertDialogBuilder.DANGER_DIALOG);;
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deleteUser() {}

    public void newUser() {
        try {

            currentUser = null;
            currentEmployee = null;

            selectUserCombo.getSelectionModel().clearSelection();

            userNameField.setText(null);
            firstNameField.setText(null);
            middleNameField.setText(null);
            lastNameField.setText(null);
            addressField.setText(null);
            designationField.setText(null);
            phoneNumberField.setText(null);
            departmentCombo.getSelectionModel().clearSelection();

            listOfAvailablePermissions = FXCollections.observableArrayList(PermissionDAO.getAll(conn));
            listOfAvailableRoles = FXCollections.observableArrayList(RoleDAO.getAll(conn));

            if(listOfUserRoles!=null) listOfUserRoles.clear();
            if(listOfUserPermissions!=null) listOfUserPermissions.clear();

            userNameField.requestFocus();
        }catch(Exception ex) {
            ex.printStackTrace();
            AlertDialogBuilder.messgeDialog("Exception",ex.getMessage(),userMgtStackPane,AlertDialogBuilder.DANGER_DIALOG);;
        }
    }

    public void clearPasswords() {
        newPasswordField.setText(null);
        confirmPasswordField.setText(null);
        newPasswordField.requestFocus();
    }

    public void savePassword() {
        String password = newPasswordField.getText();
        String confirmation = confirmPasswordField.getText();

        if(password.equals(confirmation)) {
            try {
                UserDAO.updatePassword(currentUser, password, DB.getConnection());
                AlertDialogBuilder.messgeDialog("Password Changed","The user's password has been changed.", userMgtStackPane, AlertDialogBuilder.INFO_DIALOG);
            }catch(Exception ex) {
                AlertDialogBuilder.messgeDialog("Exception!", ex.getMessage(), userMgtStackPane, AlertDialogBuilder.DANGER_DIALOG);
            }

        }else {
            AlertDialogBuilder.messgeDialog("Invalid Password!","The passwords did not match!", userMgtStackPane, AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    public void removeUserRole() {}

    public void addUserRole() {}

    public void newRole() {}

    public void saveRole()
    {
        try {
            if(currentRole == null)
            {
                currentRole = new Role(-1, roleNameField.getText(), roleDescriptionField.getText());
                RoleDAO.add(currentRole, conn);
                listOfRoles.add(currentRole);
            }
        }catch(Exception ex) {
            ex.printStackTrace();
            AlertDialogBuilder.messgeDialog("Exception",ex.getMessage(),userMgtStackPane,AlertDialogBuilder.DANGER_DIALOG);;
        }
    }

    @FXML
    public void onSelectRole()
    {
        try
        {
            Role role = (Role)rolesList.getSelectionModel().getSelectedItem();
            if(role!=null) {
                currentRole = role;
                roleNameField.setText(role.getRole());
                roleDescriptionField.setText(role.getDescription());

                listOfRolePermissions = FXCollections.observableArrayList(RoleDAO.getPermissions(role, conn));
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            AlertDialogBuilder.messgeDialog("Exception",ex.getMessage(),userMgtStackPane,AlertDialogBuilder.DANGER_DIALOG);;
        }
    }

    public void deleteRole() {}

    public void removeRolePermission() {}

    public void addRolePermission() {}

    public void removeUserPermission()
    {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Remove Permission?");
            alert.setHeaderText("Remove Permission?");
            alert.setContentText("Are you sure about removing this permission?");

            Optional<ButtonType> opt = alert.showAndWait();

            if(opt.get().getButtonData().isDefaultButton()) {
                Permission p = (Permission)userPermissionsList.getSelectionModel().getSelectedItem();
                UserDAO.removePermission(currentUser,p,conn);
                listOfAvailablePermissions.add(p);
                listOfUserPermissions.remove(p);
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            AlertDialogBuilder.messgeDialog("Exception",ex.getMessage(),userMgtStackPane,AlertDialogBuilder.DANGER_DIALOG);;
        }

    }

    public void addUserPermission()
    {
        try {
            Permission p = (Permission)selectPermissionCombo.getSelectionModel().getSelectedItem();
            if(p!=null) {
                UserDAO.addPermission(currentUser, p, conn);
                listOfAvailablePermissions.remove(p);
                listOfUserPermissions.add(p);
            }
        }catch(Exception ex) {
            ex.printStackTrace();
            AlertDialogBuilder.messgeDialog("Exception",ex.getMessage(),userMgtStackPane,AlertDialogBuilder.DANGER_DIALOG);;
        }
    }

    public void deletePermission()
    {
        if(currentPermission==null) {
            AlertDialogBuilder.messgeDialog("Nothing Selected", "No permission selected.", userMgtStackPane, AlertDialogBuilder.WARNING_DIALOG);
            return;
        }

        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Permission");
            alert.setHeaderText("Delete Permission");
            alert.setContentText("Are you sure about deleting this permission?");

            Optional<ButtonType> response = alert.showAndWait();
            if(response.get().getButtonData().isDefaultButton()) {
                PermissionDAO.removePermission(currentPermission, conn);
                listOfPermissions.remove(currentPermission);
                currentPermission = null;
            }
        }catch(Exception ex) {
            ex.printStackTrace();
            AlertDialogBuilder.messgeDialog("Exception",ex.getMessage(),userMgtStackPane,AlertDialogBuilder.DANGER_DIALOG);;
        }
    }

    public void newPermission() {
        currentPermission = null;
        permissionNameField.setText(null);
        permissionDescriptionField.setText(null);

        permissionNameField.requestFocus();
    }

    public void savePermission() {
        try {
            if(currentPermission==null) {
                //create new permission
                currentPermission = new Permission(-1, permissionNameField.getText(), permissionDescriptionField.getText());
                PermissionDAO.add(currentPermission, conn);
                listOfPermissions.add(currentPermission);
            }else {
                //update existing permission
                currentPermission.setPermission(permissionNameField.getText());
                currentPermission.setRemarks(permissionDescriptionField.getText());
                PermissionDAO.updatePermission(currentPermission, conn);
            }
        }catch(Exception ex) {
            ex.printStackTrace();
            AlertDialogBuilder.messgeDialog("Exception",ex.getMessage(),userMgtStackPane,AlertDialogBuilder.DANGER_DIALOG);;
        }
    }

    public void onSelectPermission() {
        currentPermission = (Permission)permissionsList.getSelectionModel().getSelectedItem();
        permissionNameField.setText(currentPermission.getPermission());
        permissionDescriptionField.setText(currentPermission.getRemarks());
        permissionNameField.requestFocus();
    }

    @Override
    public void setSubMenus(FlowPane flowPane) {
        flowPane.getChildren().removeAll();
        flowPane.getChildren().setAll(new ArrayList<>());
    }
}
