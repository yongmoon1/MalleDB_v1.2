package util;

public class Status {
    private final String name;
    private final String description;
    private String key;
    private String value;

    /**
     * @param name        A short name for the status.
     * @param description A description of the status.
     */
    public Status(String name, String description) {
        super();
        this.name = name;
        this.description = description;
    }

    public Status(String name, String description, String key, String value) {
        super();
        this.name = name;
        this.description = description;
        this.key = key;
        this.value = value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Status [name=" + name + ", description=" + description + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Status other = (Status) obj;
        if (description == null) {
            if (other.description != null) {
                return false;
            }
        } else if (!description.equals(other.description)) {
            return false;
        }
        if (name == null) {
            return other.name == null;
        } else return name.equals(other.name);
    }

    /**
     * Is {@code this} a passing state for the operation: {@link Status#OK} or {@link Status#BATCHED_OK}.
     *
     * @return true if the operation is successful, false otherwise
     */
    public boolean isOk() {
        return this == OK || this == BATCHED_OK || this.name.equals("READ_OK");
    }

    public static final Status OK = new Status("OK", "The operation completed successfully.");
    public static final Status ERROR = new Status("ERROR", "The operation failed.");
    public static final Status NOT_FOUND = new Status("NOT_FOUND", "The requested record was not found.");
    public static final Status NOT_IMPLEMENTED = new Status("NOT_IMPLEMENTED", "The operation is not " +
            "implemented for the current binding.");
    public static final Status UNEXPECTED_STATE = new Status("UNEXPECTED_STATE", "The operation reported" +
            " success, but the result was not as expected.");
    public static final Status BAD_REQUEST = new Status("BAD_REQUEST", "The request was not valid.");
    public static final Status FORBIDDEN = new Status("FORBIDDEN", "The operation is forbidden.");
    public static final Status SERVICE_UNAVAILABLE = new Status("SERVICE_UNAVAILABLE", "Dependant " +
            "service for the current binding is not available.");
    public static final Status BATCHED_OK = new Status("BATCHED_OK", "The operation has been batched by " +
            "the binding to be executed later.");
    public static final Status HASHMAP_FULL = new Status("HASHMAP_FULL", "HASHMAP " +
            "IS FULL.");
}

