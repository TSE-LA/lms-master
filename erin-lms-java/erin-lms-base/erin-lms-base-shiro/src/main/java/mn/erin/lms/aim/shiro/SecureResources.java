package mn.erin.lms.aim.shiro;

/**
 * @author Oyungerel Chuluunsukh.
 */
public final class SecureResources
{
 private SecureResources(){}

  public static String[] getAdminResources(){
    return new String[]  {
        "lms/admins",
        "lms/assessments/**",
        "lms/certificate",
        "lms/quizzes/**",
        "lms/assessments-report",
        "lms/analytics/survey/**",
        "sessions/**",
        "utility/convert/**",
        "lms/system-configs/background-image",
        "lms/courses/**"
    };
  }

  public static String[] getAuthorityResources(){
    return new String[] {
        "aim/memberships/**",
        "aim/group-members",
        "aim/roles",
        "lms/course-activity/**",
        "lms/courses/report/**",
        "lms/exam/create",
        "lms/question-category/**",
        "lms/questions/**"
    };
  }

  public static String[] getSecureResources(){
    return new String[] {
        "aim/users/**",
        "aim/group/**",
        "lms/users/**",
        "lms/analytics/**",
        "lms/certificate/**",
        "lms/classroom-courses/**",
        "lms/courses/**",
        "lms/course-categories/**",
        "lms/employee-analytics/**",
        "lms/exam/**",
        "lms/exam-categories"
    };
  }

  public static String[] getAnonResources(){
    return new String[] {
        "aim/login",
        "aim/logout",
        "aim/validate-session"
    };
  }
}
