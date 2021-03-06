package backend;

import holders.Assignment;
import holders.Question;
import holders.User;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class to perform all SQL related functions. Used mainly for the purpose of
 * adding objects to the database (users, questions, assignments), retrieving
 * information to construct objects in the UI, and update records like marks.
 */
public class DatabaseManager {
    public static Connection conn;
    private static String sql;
    private static PreparedStatement pstmt;
    private static ResultSet rs;

    public static void connectDB() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost/C01ProjectDB", "root", "root");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a new question to the database based on the question object attributes.
     * Also checks if a multiple choice question, to appropriately populate the
     * list of multiple choice answers.
     *
     * @param question the question object to be added
     */
    public static void addQuestion(Question question) {
        // SQL Query
        int questionType = (question.multipleChoices == null) ? 2 : 1;
        try {
            sql = "INSERT INTO question(question, answer, qtype) VALUES(?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, question.question);
            pstmt.setString(2, question.answer);
            pstmt.setInt(3, questionType);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // If true we know the question is a multiple choice question
        if (questionType == 1) {
            try {
                String sql_get = "SELECT qid FROM question WHERE question=?";
                PreparedStatement ret_id = conn.prepareStatement(sql_get);
                ret_id.setString(1, question.question);
                // Result set for desired qid
                ResultSet rs = ret_id.executeQuery();


                Integer qid = null;
                if (rs.next()) {
                    qid = rs.getInt(1);
                }

                String sql = "INSERT INTO mc(qid, choice) VALUES(?, ?)";
                PreparedStatement add_mc = conn.prepareStatement(sql);
                add_mc.setInt(1, qid);
                for (int i = 0; i < (question.multipleChoices).length; i++) {
                    add_mc.setString(2, question.multipleChoices[i]);
                    add_mc.executeUpdate();

                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Gets a question and constructs the question object before returning,
     * based on the provided questionID.
     *
     * @param questionID the id of the question to retrieve
     * @return the question object for the given id, or null not existing
     */
    public static Question getQuestion(int questionID) {


        try {
            sql = "SELECT question, answer, qtype, qid FROM question WHERE qid=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, questionID);
            rs = pstmt.executeQuery();
            String question = null, answer = null;
            int qtype = 0, qid = 0;
            while (rs.next()) {
                question = rs.getString(1);
                answer = rs.getString(2);
                qtype = rs.getInt(3);
                qid = rs.getInt(4);
            }
            String[] mc_choices = null;
            // If multiple choice, need mc_choices.
            if (qtype == 1) {
                String sql_mc = "SELECT choice FROM mc WHERE qid=?";
                PreparedStatement pstmt_mc = conn.prepareStatement(sql_mc);
                pstmt_mc.setInt(1, questionID);
                ResultSet rs_mc = pstmt_mc.executeQuery();

                ArrayList<String> mc_list = new ArrayList<>();

                while (rs_mc.next()) {
                    mc_list.add(rs_mc.getString(1));
                }
                mc_choices = mc_list.toArray(new String[mc_list.size()]);
            }

            Question res_question = new Question(qid, question, answer, null, mc_choices);
            return res_question;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all questions that are related to an assignment. If assignment id is invalid, then return
     * all available questions.
     *
     * @param assignId assignment id
     * @return list of Question on an assignment
     */
    public static List<Question> getAllQuestions(int assignId) {
        String question, answer;
        int qtype, qid;
        List<Question> question_list = new ArrayList<>();
        List<String> mc_list;
        String[] mc_choices;
        try {
            if (assignId < 1) {
                sql = "SELECT question, answer, qtype, qid FROM question";
                pstmt = conn.prepareStatement(sql);
            } else {
                sql = "SELECT question, answer, qtype, q.qid FROM question q JOIN " +
                        "related_question rq ON rq.qid=q.qid where rq.aid=?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, assignId);
            }
            rs = pstmt.executeQuery();

            while (rs.next()) {
                question = rs.getString(1);
                answer = rs.getString(2);
                qtype = rs.getInt(3);
                qid = rs.getInt(4);
                mc_choices = null; // reset for each question
                mc_list = new ArrayList<>(); // reset for each question
                // Again check for multiple choice.
                if (qtype == 1) {
                    String sql_mc = "SELECT choice FROM mc WHERE qid=?";
                    PreparedStatement pstmt_mc = conn.prepareStatement(sql_mc);
                    pstmt_mc.setInt(1, qid);
                    ResultSet rs_mc = pstmt_mc.executeQuery();
                    while (rs_mc.next()) {
                        mc_list.add(rs_mc.getString(1));

                    }
                    mc_choices = mc_list.toArray(new String[mc_list.size()]);
                }
                question_list.add(new Question(qid, question, answer, null, mc_choices));
            }
            return question_list;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Adds a new user into the database, based on the attributes of the user
     * object passed.
     *
     * @param user the user to be added into the database
     */
    public static void addUser(User user) {
        try {
            sql = "INSERT INTO users(uname, email, password, cid, type) VALUES(?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.name);
            pstmt.setString(2, user.email);
            // This may need to change. Should we really store raw password text?
            pstmt.setString(3, user.input_pass);
            pstmt.setInt(4, user.courseID);
            pstmt.setInt(5, user.type);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets a user based on specified user and password inputs. This only
     * returns the user if both are correct.
     *
     * @param user the name of the user
     * @param pass the password of the user
     * @return the user object to load information for
     */
    public static User getUser(String user, String pass) {
        try {
            sql = "SELECT uid, uname, email, password, cid, type FROM users WHERE uname=? AND password=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user);
            pstmt.setString(2, pass);
            rs = pstmt.executeQuery();
            String uname = null, email = null, password = null;
            int cid = -1, type = -1, userid = -1;
            while (rs.next()) {
                userid = rs.getInt(1);
                uname = rs.getString(2);
                email = rs.getString(3);
                password = rs.getString(4);
                cid = rs.getInt(5);
                type = rs.getInt(6);
            }
            User res_user = new User(userid, uname, email, password, cid, type);
            if (userid == -1) {
                return null;
            } else {
                return res_user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Adds an assignment to the database based on the assignment object attributes.
     *
     * @param assignment the assignment object to store
     */
    public static void addAssignment(Assignment assignment) {
        int aid = -1;

        try {
            // add assignment to table
            sql = "INSERT INTO assignment(aname,cid, due_date) VALUES(?, ?, DATE(?))";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, assignment.name);
            pstmt.setInt(2, assignment.courseID);
            pstmt.setString(3, assignment.dueDate);
            pstmt.executeUpdate();

            // get the ID of the new assignment
            sql = "SELECT aid FROM assignment ORDER BY aid DESC LIMIT 1";
            pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            if (rs.first()) {
                aid = rs.getInt(1);
            }

            // add related questions to the assignment
            for (int qid : assignment.questions) {

                sql = "INSERT INTO related_question(aid,qid) VALUES(?, ?)";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, aid);
                pstmt.setInt(2, qid);
                pstmt.executeUpdate();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets an assignment based on specified assignment id. Returns the object
     * with the appropriate assignment attributes.
     *
     * @param assignmentID id of the assignment to retrieve
     * @return the assignment object with appropriate attributes
     */
    public static Assignment getAssignment(int assignmentID) {
        String aname = "";
        String date = "";
        List<Integer> qids = new ArrayList<>();
        Date dueDate;
        int aid = -1, courseID = -1;
        Assignment assignment = null;
        try {
            // get assignment info
            sql = "SELECT aid, aname, cid, due_date  FROM assignment WHERE aid=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, assignmentID);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                aid = rs.getInt(1);
                aname = rs.getString(2);
                courseID = rs.getInt(3);
                dueDate = rs.getDate(4);
                date = convertDateToString(dueDate);
            }
            // get question list for this assignment
            sql = "SELECT qid FROM related_question WHERE aid=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, assignmentID);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                qids.add(rs.getInt(1));
            }

            assignment = new Assignment(aid, aname, courseID, qids, date);


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return assignment;
    }

    /**
     * get all assignments that are associated with a course/user. If userID is invalid, then get assignments based on courseID
     * and no mark is given to the assignments return.
     * If userID is valid, then return Assignments with user's marks on each assignment
     *
     * @param userID
     * @param courseID
     * @return list of assignments related to the given userID or courseID
     */
    public static List<Assignment> getAllAssignment(int userID, int courseID) {
        String aname = null;
        String date;
        int assignid = -1, tempId;
        float mark = -1;
        Date dueDate = new Date();
        List<Integer> qid = new ArrayList<>();
        List<Assignment> assign_list = new ArrayList<>();
        try {
            if (userID < 1) {
                sql = "SELECT a.aid, aname, due_date, qid FROM assignment a LEFT OUTER JOIN related_question rq ON " +
                        "rq.aid=a.aid WHERE cid=?";

                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, courseID);
            } else {
                sql = "SELECT a.aid, aname, due_date, qid, m.mark FROM assignment a JOIN related_question rq ON " +
                        "rq.aid=a.aid LEFT JOIN marks m ON a.cid = m.cid AND a.aid=m.aid and student = ? WHERE a.cid = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, userID);
                pstmt.setInt(2, courseID);
            }
            rs = pstmt.executeQuery();

            while (rs.next()) {
                tempId = rs.getInt(1);

                if (tempId != assignid) {
                    // this is new assignment, store previous one to list if there is one
                    if (assignid != -1) {
                        date = convertDateToString(dueDate);
                        assign_list.add(new Assignment(assignid, aname, courseID, qid, date, mark));
                    }
                    // clear question list for new assignment
                    assignid = tempId;
                    qid = new ArrayList<>();
                }
                aname = rs.getString(2);
                dueDate = rs.getDate(3);
                qid.add(rs.getInt(4));
                if (userID >= 1) {
                    mark = rs.getFloat(5);
                    if (rs.wasNull()) {
                        mark = -1;
                    }
                }
            }
            // loop ends before storing the last assignment
            date = convertDateToString(dueDate);
            assign_list.add(new Assignment(assignid, aname, courseID, qid, date, mark));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return assign_list;
    }

    /**
     * Update current user's assignment mark if due date has not passed and current mark is higher than previous mark
     *
     * @param assignId assignment id
     * @param mark     current mark
     */
    public static void updateAssignmentMark(int assignId, float mark) {
        float previousMark;
        int userId = CurrentSession.user.id;
        int courseId = CurrentSession.user.courseID;

        try {
            if (!passedDueDate(assignId)) {
                // check if there is a previous mark
                sql = "SELECT mark FROM marks WHERE student =? AND aid=?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, userId);
                pstmt.setInt(2, assignId);
                rs = pstmt.executeQuery();
                // choose query accordingly
                if (rs.next()) {
                    // only update mark if previous mark is lower than current mark
                    previousMark = rs.getFloat(1);
                    if (previousMark < mark) {
                        sql = "UPDATE marks SET mark = ? WHERE student = ? AND cid = ? AND aid =?";
                    } else {
                        sql = null;
                    }
                } else {
                    // there is not previous mark
                    sql = "INSERT INTO marks(mark, student, cid, aid) VALUES (?,?,?,?)";
                }
                if (sql != null) {
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setFloat(1, mark);
                    pstmt.setInt(2, userId);
                    pstmt.setInt(3, courseId);
                    pstmt.setInt(4, assignId);
                    pstmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if given assignment due date has passed yet or not
     *
     * @param assignId assignment id
     * @return true if due date has passed, otherwise false
     */
    private static boolean passedDueDate(int assignId) {
        Date dueDate = new Date();
        Date today = new Date();
        try {
            sql = "SELECT due_date FROM assignment where aid = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, assignId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                dueDate = rs.getDate(1);
            }
            DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
            df.format(today);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (dueDate.before(today));

    }

    /**
     * Given a Date object convert it into a string in yyyy/MM/dd format
     *
     * @param date Date object
     * @return string representation of the date
     */
    private static String convertDateToString(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        return df.format(date);
    }

    /**
     * Get the course id of the given course. If course is not in database then add it first then get the new
     * course id.
     *
     * @param course course name
     * @return course id
     */
    public static int getCourseID(String course) {
        int courseID = -1;
        try {
            sql = "SELECT cid FROM course WHERE course = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, course);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                // course exists so get course id
                courseID = rs.getInt(1);
            } else {
                // add new course
                sql = "INSERT INTO course(course) VALUES (?)";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, course);
                pstmt.executeUpdate();
                // get new course id
                sql = "SELECT cid FROM course WHERE course = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, course);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    courseID = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courseID;
    }
}
