package ui;

import backend.DatabaseManager;
import holders.Question;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


public class AddAssignmentPage extends JPanel implements MouseListener {

    private Button saveButton;
    /*
    private EditQuestionButton[] editButton;
    private DeleteQuestionButton[] deleteButton;
    */
    private InputField assignmentInput;
    private Question[] questionList;
    private JLabel[] questionLabels;
    private CheckBox[] questionCheckBoxes;



    public AddAssignmentPage(Question[] questions) {
        super();
        int question_num = questions.length;
        saveButton = new SaveQuestionButton();
        /*
        editButton = new EditQuestionButton[question_num];
        deleteButton = new DeleteQuestionButton[question_num];
        */
        assignmentInput = new InputField();
        questionList = questions;
        questionLabels = new JLabel[question_num]
        ;
        questionCheckBoxes = new CheckBox[question_num];

        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Available Questions", SwingConstants.CENTER);
        title.setPreferredSize(new Dimension(800, 50));
        title.setFont(getFont().deriveFont(24f));
        add(title);

        add(UIManager.getSpacing(800, 40));

        JLabel typeQuestion = new JLabel("Create an Assignment:", SwingConstants.RIGHT);
        typeQuestion.setFont(getFont().deriveFont(18f));
        add(typeQuestion);

        add(assignmentInput);

        add(UIManager.getSpacing(800, 30));

        for (int i = 0; i < questionList.length; i++) {
            questionCheckBoxes[i] = new CheckBox(ClickableObject.MULTIPLE_CHOICE_OPTIONS[i]);
            questionCheckBoxes[i].addMouseListener(this);
            add(questionCheckBoxes[i]);

            questionLabels[i] = new JLabel(questionList[i].question, SwingConstants.LEFT);
            questionLabels[i].setPreferredSize(new Dimension(InputField.WIDTH, 25));
            questionLabels[i].setFont(getFont().deriveFont(16f));

            add(questionLabels[i]);
            /*
            editButton[i] = new EditQuestionButton(i);
            deleteButton[i] = new DeleteQuestionButton(i);
            editButton[i].addMouseListener(this);
            deleteButton[i].addMouseListener(this);

            add(editButton[i]);
            add(deleteButton[i]);
            */
            add(UIManager.getSpacing(800, 1));
        }

        questionCheckBoxes[0].select();

        add(UIManager.getSpacing(800, 40));

        saveButton.addMouseListener(this);
        add(saveButton);

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int id = ((ClickableObject) e.getSource()).getID();
        switch (id) {
            case ClickableObject.SAVE_QUESTION:
                createAssignment();
                break;
            case ClickableObject.MULTIPLE_CHOICE_OPTION_1:
            case ClickableObject.MULTIPLE_CHOICE_OPTION_2:
            case ClickableObject.MULTIPLE_CHOICE_OPTION_3:
            case ClickableObject.MULTIPLE_CHOICE_OPTION_4:
                for (CheckBox checkbox : questionCheckBoxes) {
                    if (id == checkbox.getID()) {
                        if (!checkbox.isSelected())
                            checkbox.select();
                        else
                            checkbox.deselect();
                    }
                }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        int id = ((ClickableObject) e.getSource()).getID();
        switch (id) {
            case ClickableObject.SAVE_QUESTION:
                saveButton.setBackground(Button.BUTTON_COLOR_PRESSED);
                break;
            /*case ClickableObject.EDIT_QUESTION:
                int editId = ((EditQuestionButton) e.getSource()).getEditButtonId();
                for (EditQuestionButton button : editButton) {
                    if (editId == button.getEditButtonId())
                        button.setBackground(Button.BUTTON_COLOR_PRESSED);
                }
                break;
            case ClickableObject.DELETE_QUESTION:
                int deleteId = ((DeleteQuestionButton) e.getSource()).getDeleteButtonId();
                for (DeleteQuestionButton button : deleteButton) {
                    if (deleteId == button.getDeleteButtonId())
                        button.setBackground(Button.BUTTON_COLOR_PRESSED);
                }
                break;*/
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        switch (((ClickableObject) e.getSource()).getID()) {
            case ClickableObject.SAVE_QUESTION:
                saveButton.setBackground(Button.BUTTON_COLOR_IDLE);
                break;
            /*case ClickableObject.EDIT_QUESTION:
                int editId = ((EditQuestionButton) e.getSource()).getEditButtonId();
                for (EditQuestionButton button : editButton) {
                    if (editId == button.getEditButtonId())
                        button.setBackground(Button.BUTTON_COLOR_IDLE);
                }
                break;
            case ClickableObject.DELETE_QUESTION:
                int deleteId = ((DeleteQuestionButton) e.getSource()).getDeleteButtonId();
                for (DeleteQuestionButton button : deleteButton) {
                    if (deleteId == button.getDeleteButtonId())
                        button.setBackground(Button.BUTTON_COLOR_IDLE);
                }
                break;*/
        }
    }

    private void editQuestion() {
        //TODO: switch to edit question page
    }


    private void deleteQuestion() {
        //TODO: trigger delete question function

    }

    private void createAssignment() {
        //TODO: get question list from db and addt the selected ones to assignment
        String aname = assignmentInput.getText();
//        String[] questionList = new Questions[3];

        for (int i = 0; i < questionCheckBoxes.length; i++) {
            if (questionCheckBoxes[i].isSelected()) {
//                questionList[i] = new Question(id);

            }

//      DatabaseManager.addAssignment(new Assignment(aname, questionList));


        }
    }
}