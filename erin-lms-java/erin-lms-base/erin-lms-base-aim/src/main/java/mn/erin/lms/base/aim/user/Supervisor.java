package mn.erin.lms.base.aim.user;

/**
 * A supervisor is able within a course to view and grade learners' work, but may not
 * alter or delete any of the activities or resources
 * In courses where departments are used, the supervisor may well be responsible for
 * one particular department and will not need access to other departments.
 * <p>
 * Supervisors can only see courses in the department that they are in;
 * <p>
 * the Dashboard only shows updates for their departments;
 * <p>
 * They only receive notifications for their belonging departments; and they can only mark their departments'
 * learners
 *
 * @author Bat-Erdene Tsogoo.
 */
public interface Supervisor extends LmsUser
{
}
