package org.lab.sms.domain;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * CommonBean class defines shared functionality among all javabean classes.
 */
public abstract class CommonBean<T> implements Serializable, Comparable<T>, Cloneable {

    private Log logger = LogFactory.getLog(this.getClass());
    private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    /**
     * Annotation definition for capturing which bean properties to compare when determining if two objects are equal,
     * greater than or less than. Specify <code>order</code> when key is a composite of more than one property.
     *
     * @key(order=1) int id;
     * @key(order=2) Date effectiveDate;
     * <p/>
     * <p/>
     * Key fields are always treated as @required, so adding @required to a key field is redundant.
     * <p/>
     * key - noun - a field or group of characters within a record that identifies the record, establishing its position
     * among sorted records, and/or provides information about its contents.
     */
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Key {
        int order() default 1;
    }

    /**
     * Annotation definition for capturing which bean properties are required. A call to isValid() will return true
     * if all properties defined as @key or @required are not null.
     * <p/>
     * Key fields are already assumed to be required, so adding @required to a key field is redundant.
     */
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Required {
    }

    /**
     * Annotation definition for capturing which bean properties to include when toString() is called.
     * <p/>
     * Key fields and required fields are always included in the output of toString(), so adding @Info to a
     * property that already has @key or @required is redundant.
     */
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Info {
    }

    private static Map<String, Fields> annotatedFieldsMap = new HashMap<String, Fields>();

    private static class Fields {
        List<Field> keyFields;
        List<Field> requiredFields;
        List<Field> infoFields;

        public Fields(List<Field> keyFields, List<Field> requiredFields, List<Field> infoFields) {
            this.keyFields = keyFields;
            this.requiredFields = requiredFields;
            this.infoFields = infoFields;
        }
    }

    /**
     * Checks if all <code>@Key</code> fields of this object and <code>obj</code> are equal.
     *
     * @param obj object to compare to
     * @return true if all key fields are equal
     */
    public boolean equals(Object obj) {

        if (obj == null) return false;

        List<Field> fields = getAnnotatedFields(this.getClass()).keyFields;

        StringBuffer buf = new StringBuffer();

        for (Field field : fields) {
            try {
                Object thisValue = PropertyUtils.getSimpleProperty(this, field.getName());
                Object objValue = PropertyUtils.getSimpleProperty(obj, field.getName());
                if (thisValue == null) {
                    if (objValue != null) return false;
                } else if (objValue == null || !thisValue.equals(objValue)) {
                    return false;
                }
            } catch (Exception e) {
                buf.append("[" + e.getClass().getName() + ":" + e.getMessage() + "]");
            }
        }

        return true;
    }

    /**
     * Calculate hash code using key fields.
     *
     * @return hashCode calculated from key fields
     */
    public int hashCode() {

        int hashCode = 0;
        List<Field> fields = getAnnotatedFields(this.getClass()).keyFields;

        for (Field field : fields) {
            try {
                Object thisValue = PropertyUtils.getSimpleProperty(this, field.getName());
                // If the field f is a boolean: calculate (f ? 0 : 1);
                // If the field f is a byte, char, short or int: calculate (int)f;
                // If the field f is a long: calculate (int)(f ^ (f >>> 32));
                // If the field f is a float: calculate Float.floatToIntBits(f);
                // If the field f is a double: calculate Double.doubleToLongBits(f) and handle the return value like every long value;
                // If the field f is an object: Use the result of the hashCode() method or 0 if f == null;
                // If the field f is an array: See every field as separate element and calculate the hash value in a recursive fashion and combine the values as described next.
                int hashValue = 0;
                if (thisValue instanceof Boolean) {
                    hashValue = ((Boolean) thisValue).booleanValue() ? 0 : 1;
                } else if (thisValue instanceof Byte) {
                    hashValue = ((Byte) thisValue).intValue();
                } else if (thisValue instanceof Short) {
                    hashValue = ((Short) thisValue).intValue();
                } else if (thisValue instanceof Integer) {
                    hashValue = ((Integer) thisValue).intValue();
                } else if (thisValue instanceof Long) {
                    hashValue = (int) (((Long) thisValue).longValue() ^ (((Long) thisValue).longValue() >>> 32));
                } else if (thisValue instanceof Float) {
                    hashValue = Float.floatToIntBits((Float) thisValue);
                } else if (thisValue instanceof Double) {
                    hashValue = (int) Double.doubleToLongBits((Float) thisValue);
                } else {
                    hashValue = thisValue.hashCode();
                }
                hashCode = 37 * hashCode + hashValue;
            } catch (Exception e) {
                logger.error(e.getClass().getName() + " - " + e.getMessage());
            }
        }

        return hashCode;
    }

    /**
     * Compares all <code>@Key</code> fields to determine if this object is greater than,
     * equal to or less than <code>obj</obj>.
     *
     * @param obj object to compare to
     * @return 0 if equal, 1 if greater than and -1 if less than
     */
    public int compareTo(T obj) {

        // If object being compared to is null always return 1
        if (obj == null) return 1;

        if (equals(obj)) return 0;

        List<Field> fields = getAnnotatedFields(this.getClass()).keyFields;
        for (Field field : fields) {
            try {
                Object thisValue = PropertyUtils.getProperty(this, field.getName());
                Object objValue = PropertyUtils.getProperty(obj, field.getName());
                if (thisValue == null && objValue == null) return 0;
                if (thisValue != null && objValue == null) return 1;
                if (thisValue == null && objValue != null) return -1;

                //Type type = thisValue.getClass();
                //Method compareToMethod = thisValue.getClass().getMethod("compareTo", new Class[] {thisValue.getClass()});

                Class clazz = field.getType();
                Class parameterTypes[] = new Class[]{field.getType()};
                Method compareToMethod;
                try {
                    compareToMethod = clazz.getMethod("compareTo", field.getType());
                    Object result = compareToMethod.invoke(thisValue, objValue);
                    if (((Integer) result).intValue() != 0) {
                        return ((Integer) result).intValue();
                    }
                } catch (NoSuchMethodException nsme) {
                    if (!thisValue.equals(objValue)) {
                        if (thisValue.hashCode() > objValue.hashCode()) {
                            return 1;
                        } else {
                            return -1;
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Error comparing objects: " + e.getMessage());
            }
        }

        return 0;

    }

    /**
     * Constructs a string containing class name, <code>@Key</code> fields, <code>@Required</code> fields and
     * <code>@Info</code> fields.
     *
     * @return string in simple format {class name}({field name}={field value},...)
     */
    public String toString() {

        List<Field> fields = new ArrayList<Field>();
        fields.addAll(getAnnotatedFields(this.getClass()).keyFields);
        fields.addAll(getAnnotatedFields(this.getClass()).requiredFields);
        fields.addAll(getAnnotatedFields(this.getClass()).infoFields);

        StringBuffer buf = new StringBuffer();

        for (Field field : fields) {
            buf.append((buf.length() != 0 ? "," : "") + field.getName() + "=");
            try {
                Object value = PropertyUtils.getSimpleProperty(this, field.getName());
                if (value instanceof Date) {
                    value = new SimpleDateFormat(DEFAULT_DATE_PATTERN).format(value);
                }
                buf.append(value);
            } catch (Exception e) {
                buf.append("[" + e.getClass().getName() + ":" + e.getMessage() + "]");
            }
        }

        return this.getClass().getSimpleName() + "(" + buf.toString() + ")";
    }

    /**
     * Validate object by confirming that all <code>@Required</code> fields are not null and
     * that all String fields do not have zero length.
     *
     * @throws IllegalArgumentException
     */
    public void validate() throws IllegalArgumentException {

        List<Field> fields = new ArrayList<Field>();
        fields.addAll(getAnnotatedFields(this.getClass()).keyFields);
        fields.addAll(getAnnotatedFields(this.getClass()).requiredFields);

        for (Field field : fields) {
            try {
                if (PropertyUtils.getSimpleProperty(this, field.getName()) == null) {
                    throw new IllegalArgumentException("Required value missing: " + field.getName());
                }
                Object value = PropertyUtils.getProperty(this, field.getName());
                if (value instanceof String && (value == null || ((String) value).length() == 0)) {
                    throw new IllegalArgumentException("Required value missing: " + field.getName());
                } else if (value instanceof List && ((List) value).size() == 0) {
                    throw new IllegalArgumentException("Required value missing: " + field.getName());
                }
            } catch (IllegalArgumentException iae) {
                throw iae;
            } catch (Exception e) {
                throw new RuntimeException("Error getting field '" + field.getName() + "' value", e);
            }
        }
    }

    protected static Fields getAnnotatedFields(Class clazz) {

        Fields annotatedFields = annotatedFieldsMap.get(clazz.getName());
        if (annotatedFields == null) {
            setAnnotatedFields(clazz);
            annotatedFields = annotatedFieldsMap.get(clazz.getName());
        }

        return annotatedFields;
    }


    protected static void setAnnotatedFields(Class clazz) {

        if (annotatedFieldsMap.get(clazz.getName()) == null) {
            Map<Integer, Field> keyMap = new TreeMap<Integer, Field>();
            List<Field> keyFields = new ArrayList<Field>();
            List<Field> requiredFields = new ArrayList<Field>();
            List<Field> infoFields = new ArrayList<Field>();

            for (Field field : clazz.getDeclaredFields()) {
                Key key = field.getAnnotation(Key.class);
                if (key != null) {
                    keyMap.put(new Integer(key.order()), field);
                    keyFields.add(field);
                }
                if (field.getAnnotation(Required.class) != null) {
                    if (!keyFields.contains(field)) {
                        requiredFields.add(field);
                    }
                }
                if (field.getAnnotation(Info.class) != null) {
                    if (!keyFields.contains(field) && !requiredFields.contains(field)) {
                        infoFields.add(field);
                    }
                }
            }

            // Put key fields in proper order
            keyFields = new ArrayList<Field>();
            for (Field field : keyMap.values()) {
                keyFields.add(field);
            }
            Fields annotatedFields = new Fields(keyFields, requiredFields, infoFields);
            annotatedFieldsMap.put(clazz.getName(), annotatedFields);
        }

    }
}