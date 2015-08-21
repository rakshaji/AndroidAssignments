package com.raksha.assignment.assignment3;

/**
 * Created by Raksha on 3/12/2015.
 */
public interface AppConstants {
    // json data ids
    String INSTRUCTOR_FIRST_NAME = "firstName";
    String INSTRUCTOR_LAST_NAME = "lastName";
    String INSTRUCTOR_OFFICE = "office";
    String INSTRUCTOR_EMAIL = "email";
    String INSTRUCTOR_PHONE = "phone";
    String INSTRUCTOR_RATING = "rating";
    String AVERAGE_RATING = "average";
    String TOTAL_RATING = "totalRatings";
    String COMMENT_TEXT = "text";
    String COMMENT_DATE = "date";
    String INSTRUCTOR_ID = "id";

    // string formats
    String AVG_RATING_FORMAT = "%s (%s reviews)";
    String FULL_NAME_FORMAT = "%s %s";

    // urls
    String GET_REVIEWS_URL = "http://bismarck.sdsu.edu/rateme/comments/";
    String POST_COMMENT_URL = "http://bismarck.sdsu.edu/rateme/comment/";
    String POST_RATING_URL = "http://bismarck.sdsu.edu/rateme/rating/";
    String GET_INSTRUCTOR_DETAILS_URL = "http://bismarck.sdsu.edu/rateme/instructor/";
    String GET_ALL_INSTRUCTORS_URL = "http://bismarck.sdsu.edu/rateme/list";

    // others
    String BLANK_STRING = "";
    String DATE_FORMAT = "M/dd/yy";
}
