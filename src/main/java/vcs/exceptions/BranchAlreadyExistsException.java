package vcs.exceptions;

/**
 * @author natalia on 13.02.17.
 */
public class BranchAlreadyExistsException extends VcsException {
    public BranchAlreadyExistsException(String message) {
        super(message);
    }
}
