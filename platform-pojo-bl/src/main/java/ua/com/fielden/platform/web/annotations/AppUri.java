package ua.com.fielden.platform.web.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

/**
 * Annotations to mark those constructor arguments that require an application URI as a string to be passed in (for whatever purpose).
 *
 * @author TG Team
 *
 */
@Retention(RUNTIME)
@Target({ElementType.PARAMETER})
@BindingAnnotation
public @interface AppUri {
}