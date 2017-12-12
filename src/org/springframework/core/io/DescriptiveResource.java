/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/
package org.springframework.core.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.core.io.AbstractResource;

public class DescriptiveResource extends AbstractResource {
	private final String description;

	public DescriptiveResource(String description) {
		this.description = description != null ? description : "";
	}

	public boolean exists() {
		return false;
	}

	public boolean isReadable() {
		return false;
	}

	public InputStream getInputStream() throws IOException {
		throw new FileNotFoundException(
				this.getDescription()
						+ " cannot be opened because it does not point to a readable resource");
	}

	public String getDescription() {
		return this.description;
	}

	public boolean equals(Object obj) {
		return obj == this
				|| obj instanceof DescriptiveResource
				&& ((DescriptiveResource) obj).description
						.equals(this.description);
	}

	public int hashCode() {
		return this.description.hashCode();
	}
}
