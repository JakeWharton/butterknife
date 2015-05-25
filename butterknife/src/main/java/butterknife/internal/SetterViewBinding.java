package butterknife.internal;

import static butterknife.internal.ButterKnifeProcessor.VIEW_TYPE;

final class SetterViewBinding implements ViewBinding {

    private final String name;
    private final String parameterType;
    private final boolean required;

    public SetterViewBinding(String name, String parameterType, boolean required) {
        this.name = name;
        this.parameterType = parameterType;
        this.required = required;
    }

    public String getName() {
        return name;
    }

    public String getParameterType() {
        return parameterType;
    }

    public boolean requiresCast() {
        return !VIEW_TYPE.equals(parameterType);
    }

    public boolean isRequired() {
        return required;
    }

    @Override
    public String getDescription() {
        return "setter '" + name + "'";
    }
}
