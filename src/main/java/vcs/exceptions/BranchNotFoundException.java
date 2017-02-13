package vcs.exceptions;

/**
 * @author natalia on 13.02.17.
 */
public class BranchNotFoundException extends VcsException {
    public BranchNotFoundException(String message) {
        super(message);
    }
}
