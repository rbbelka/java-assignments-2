package vcs.exceptions;

/**
 * @author natalia on 13.02.17.
 */
public class MergeConflictException extends VcsException {
    public MergeConflictException(String message) {
        super(message);
    }
}
