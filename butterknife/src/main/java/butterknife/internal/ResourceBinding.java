package butterknife.internal;

final class ResourceBinding implements Binding {
	
	private final String name;
	private final String type;
	private final boolean required;
	
	ResourceBinding(String name, String type, boolean required){
		this.name = name;
		this.type = type;
		this.required = required;
	}

	@Override
	public String getDescription() {
		return "field '" + name + "'";
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public boolean isRequired() {
		return required;
	}
}
