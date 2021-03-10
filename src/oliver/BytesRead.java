package oliver;

import java.util.HashMap;

public class BytesRead {

	
	String name;
	
	HashMap<String, BytesRead> children = new HashMap<String, BytesRead>();

	public BytesRead(String name) {
		super();
		this.name = name;
	}
	
	
	
}
