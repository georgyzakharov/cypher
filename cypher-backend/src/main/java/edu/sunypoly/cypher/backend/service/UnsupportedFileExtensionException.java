public class UnsupportedFileExtensionException extends Exception {
	public UnsupportedFileExtensionException (String errorMessage) {
		super("Unsupported file extension '" + errorMessage + "'");
	}

	public UnsupportedFileExtensionException (String errorMessage, Throwable err) {
		super("Unsupported file extension '" + errorMessage + "'", err);
	}
}