package vcs.exceptions;

/**
 * General class for exceptions throwed in app
 */
public class VcsException extends Exception {

    public VcsException() {
        super();
    }

    public VcsException(String message) {
        super(message);
    }
}
