package torrent.exceptions;

public class FileIsNotOnTrackerException extends TorrentException {
    public FileIsNotOnTrackerException(String s) {
        super(s);
    }
}
