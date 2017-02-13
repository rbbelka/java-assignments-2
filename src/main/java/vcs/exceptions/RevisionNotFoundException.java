package vcs.exceptions;

/**
 * @author natalia on 13.02.17.
 */
public class RevisionNotFoundException extends VcsException {
    public RevisionNotFoundException(String message) {
        super(message);
    }
}
