package vn.edu.uit.chat_application.util;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

public final class CopyObjectUtils {
    public static void copyPropertiesIgnoreNull(Object source, Object dest) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        List<String> ignoredProperties = new ArrayList<>(pds.length);
        for(PropertyDescriptor pd : pds) {
            if(!src.isReadableProperty(pd.getName()) && pd.getReadMethod() == null){
                ignoredProperties.add(pd.getName());
            }
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                ignoredProperties.add(pd.getName());
            }
        }
        BeanUtils.copyProperties(source, dest, ignoredProperties.toArray(String[]::new));
    }
}
