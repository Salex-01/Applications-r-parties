package ricm.channels.nio;

public class IOGroup {
	public Writer write;
	public Reader read;

	public IOGroup(Writer w, Reader r) {
		write = w;
		read = r;
	}
}
