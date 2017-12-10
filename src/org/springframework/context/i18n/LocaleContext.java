package org.springframework.context.i18n;
import java.util.Locale;
public interface LocaleContext {

	/**
	 * Return the current Locale, which can be fixed or determined dynamically,
	 * depending on the implementation strategy.
	 */
	Locale getLocale();

}
