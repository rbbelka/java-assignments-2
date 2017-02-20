package vcs.exceptions;

/**
 * @author natalia on 13.02.17.
 */
public class CurrentBranchDeletionException extends VcsException {
    public CurrentBranchDeletionException(String message) {
        super(message);
    }
}
