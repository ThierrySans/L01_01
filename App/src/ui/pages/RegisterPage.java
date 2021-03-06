package ui.pages;

import backend.DatabaseManager;
import holders.User;
import ui.UIManager;

import ui.components.*;
import ui.components.Button;
import ui.components.Label;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;

/**
 * Registration page. Allows for registering a user and adding them
 * to the database.
 */
public class RegisterPage extends JPanel implements MouseListener {

    private Button saveButton;
    private Button backButton;
    private InputField[] input;
    private JPasswordField[] password;
    private String[] labelText;
    private RadioButton[] userTypeButtons;
    private String[] userType;
    private String selectedType;
    private Label title;
    private JLabel saveMessage;
    private int WINDOW_WIDTH = 800;


    /**
     * Registration page for users. Contains fields to input the required
     * information.
     */
    public RegisterPage() {
        super();
        backButton = new Button("Return to login page");
        saveButton = new SaveQuestionButton();
        input = new InputField[4]; //name, UTmail, UTORid, course and password
        password = new JPasswordField[2];
        labelText = new String[]{"name", "UTORid", "enrolled course", "UTmail", "password", "confirm password"};
        userType = new String[]{"Instructor", "TA", "Student"};
        selectedType = new String();
        saveMessage = new JLabel("", SwingConstants.CENTER);
        userTypeButtons = new RadioButton[3];
        setPreferredSize(new Dimension(WINDOW_WIDTH, 680));
        setBackground(Color.WHITE);

        addContent();

        add(UIManager.getSpacing(WINDOW_WIDTH, 30));


    }

    /**
     * Sets up content for the user to provide their information.
     */
    private void addContent() {
        // clear everything
        removeAll();

        backButton.id = ClickableObject.BACK_BUTTON;
        backButton.addMouseListener(this);
        add(backButton);
        add(UIManager.getSpacing(580, 1));

        title = new Label("Sign Up ", SwingConstants.CENTER);
        title.setPreferredSize(new Dimension(WINDOW_WIDTH, 50));
        title.setFont(getFont().deriveFont(24f));
        add(title);

        add(UIManager.getSpacing(WINDOW_WIDTH, 20));

        // add "name", "UTORid", "enrolled course", "UTmail", "password", "confirm password"
        int pwCount = 0;
        for (int i = 0; i < labelText.length; i++) {
            if (i % 2 == 0) {
                if (i > 0) {
                    add(UIManager.getSpacing(WINDOW_WIDTH, 20));
                }


                add(UIManager.getSpacing(60, 1));

                for (int j = i; j < i + 2; j++) {
                    if (j % 2 == 1) {
                        add(UIManager.getSpacing(20, 1));
                    }

                    Label text = new Label(labelText[j], SwingConstants.LEFT);
                    text.setFont(getFont().deriveFont(16f));
                    text.setPreferredSize(new Dimension(InputField.WIDTH, 25));
                    add(text);
                }

                add(UIManager.getSpacing(60, 1));
            } else {
                add(UIManager.getSpacing(20, 1));
            }

            if (labelText[i].contains("password")) {
                password[pwCount] = new JPasswordField();
                password[pwCount].setPreferredSize(new Dimension(InputField.WIDTH, InputField.HEIGHT));
                password[pwCount].setBackground(new Color(240, 240, 240));
                password[pwCount].setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                password[pwCount].setFont(getFont().deriveFont(24f));
                add(password[pwCount]);
                pwCount++;
            } else {
                input[i] = new InputField();
                add(input[i]);
            }

        }
        add(UIManager.getSpacing(WINDOW_WIDTH, 40));

        for (int i = 0; i < userTypeButtons.length; i++) {
            userTypeButtons[i] = new RadioButton(ClickableObject.USER_TYPE_OPTIONS[i]);
            userTypeButtons[i].addMouseListener(this);
            add(userTypeButtons[i]);
            Label userTypeText = new Label(userType[i], SwingConstants.LEFT);
            userTypeText.setFont(getFont().deriveFont(16f));
            userTypeText.setPreferredSize(new Dimension(100, 25));

            add(userTypeText);
        }
        userTypeButtons[0].select();
        add(UIManager.getSpacing(WINDOW_WIDTH, 20));

        saveButton.addMouseListener(this);
        add(saveButton);

        saveMessage.setPreferredSize(new Dimension(WINDOW_WIDTH, 30));
        add(saveMessage);

    }


    @Override
    public void mouseClicked(MouseEvent e) {
        int id = ((ClickableObject) e.getSource()).getID();
        switch (id) {
            case ClickableObject.BACK_BUTTON:
                UIManager.switchView(new LoginPage());
                break;
            case ClickableObject.SAVE_QUESTION:
                if (validateInput()) {
                    saveUser();
                } else {
                    showErrorMessage();
                }
                break;

            case ClickableObject.USER_TYPE_1:
            case ClickableObject.USER_TYPE_2:
            case ClickableObject.USER_TYPE_3:
                for (RadioButton radioButton : userTypeButtons) {
                    if (radioButton.getID() == id) {
                        radioButton.select();
                    } else {
                        radioButton.deselect();
                    }
                }
        }
    }

    /**
     * Error message for differing passwords.
     */
    private void showErrorMessage() {

        title.setText("Please make sure no field is empty and passwords match");
        title.setForeground(Color.WHITE);
        title.setBackground(Color.RED);
        title.setOpaque(true);

        Timer t = new Timer(5000, e -> {
            title.setText("Sign Up");
            title.setPreferredSize(new Dimension(WINDOW_WIDTH, 50));
            title.setFont(getFont().deriveFont(24f));
            title.setForeground(Color.BLACK);
            title.setBackground(Color.WHITE);
        });
        t.setRepeats(false);
        t.start();
    }

    /**
     * Checks to see whether the passwords inserted match.
     *
     * @return true for matching passwords, false otherwise
     */
    private boolean validateInput() {

        boolean result = false;
        // get selected user type for (int i = 0; i < userTypeButtons.length; i++){
        for (int i = 0; i < userTypeButtons.length; i++) {
            if (userTypeButtons[i].isSelected()) {
                selectedType = userType[i];
                break;
            }
        }
        // check password
        if (Arrays.equals(password[0].getPassword(), password[1].getPassword())) {
            result = true;
        }

        // check other fields are not empty
        for (int i = 0; i < input.length; i++) {
            if (input[i].getText().trim().equals("") || input[i].getText() == null) {
                result = false;
                break;
            }
        }
        return result;
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        switch (((ClickableObject) e.getSource()).getID()) {
            case ClickableObject.BACK_BUTTON:
                backButton.setBackground(Button.BUTTON_COLOR_PRESSED);
                break;
            case ClickableObject.SAVE_QUESTION:
                saveButton.setBackground(Button.BUTTON_COLOR_PRESSED);
                break;

        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        switch (((ClickableObject) e.getSource()).getID()) {
            case ClickableObject.BACK_BUTTON:
                backButton.setBackground(Button.BUTTON_COLOR_IDLE);
                break;
            case ClickableObject.SAVE_QUESTION:
                saveButton.setBackground(Button.BUTTON_COLOR_IDLE);
                break;
        }
    }

    /**
     * Finishes registration and adds the user to the database.
     */
    private void saveUser() {
        String UTORid = input[1].getText();
        String course = input[2].getText();
        String email = input[3].getText();
        String pw = new String(password[0].getPassword());
        int type;
        switch (selectedType) {
            case "Instructor":
                type = 1;
                break;
            case "TA":
                type = 2;
                break;
            case "Student":
                type = 3;
                break;
            default:
                type = -1;
                break;
        }
        DatabaseManager.addUser(new User(UTORid, email, pw, DatabaseManager.getCourseID(course), type));
        saveMessage.setText("Registered!");
        Timer timer = new Timer(4000, (actionEvent) -> saveMessage.setText(""));
        timer.setRepeats(false);
        timer.setCoalesce(true);
        timer.start();

    }
}
