/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/
package org.springframework.beans;

import java.beans.PropertyEditor;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import org.springframework.beans.PropertyAccessorUtils;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.propertyeditors.ByteArrayPropertyEditor;
import org.springframework.beans.propertyeditors.CharArrayPropertyEditor;
import org.springframework.beans.propertyeditors.CharacterEditor;
import org.springframework.beans.propertyeditors.CharsetEditor;
import org.springframework.beans.propertyeditors.ClassArrayEditor;
import org.springframework.beans.propertyeditors.ClassEditor;
import org.springframework.beans.propertyeditors.CurrencyEditor;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.beans.propertyeditors.CustomMapEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.beans.propertyeditors.FileEditor;
import org.springframework.beans.propertyeditors.InputSourceEditor;
import org.springframework.beans.propertyeditors.InputStreamEditor;
import org.springframework.beans.propertyeditors.LocaleEditor;
import org.springframework.beans.propertyeditors.PatternEditor;
import org.springframework.beans.propertyeditors.PropertiesEditor;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.beans.propertyeditors.TimeZoneEditor;
import org.springframework.beans.propertyeditors.URIEditor;
import org.springframework.beans.propertyeditors.URLEditor;
import org.springframework.beans.propertyeditors.UUIDEditor;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceArrayPropertyEditor;
import org.springframework.util.ClassUtils;
import org.xml.sax.InputSource;

public class PropertyEditorRegistrySupport implements PropertyEditorRegistry {
	private ConversionService conversionService;
	private boolean defaultEditorsActive = false;
	private boolean configValueEditorsActive = false;
	private Map<Class<?>, PropertyEditor> defaultEditors;
	private Map<Class<?>, PropertyEditor> overriddenDefaultEditors;
	private Map<Class<?>, PropertyEditor> customEditors;
	private Map<String, PropertyEditorRegistrySupport.CustomEditorHolder> customEditorsForPath;
	private Set<PropertyEditor> sharedEditors;
	private Map<Class<?>, PropertyEditor> customEditorCache;

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public ConversionService getConversionService() {
		return this.conversionService;
	}

	protected void registerDefaultEditors() {
		this.defaultEditorsActive = true;
	}

	public void useConfigValueEditors() {
		this.configValueEditorsActive = true;
	}

	public void overrideDefaultEditor(Class<?> requiredType,
			PropertyEditor propertyEditor) {
		if (this.overriddenDefaultEditors == null) {
			this.overriddenDefaultEditors = new HashMap();
		}

		this.overriddenDefaultEditors.put(requiredType, propertyEditor);
	}

	public PropertyEditor getDefaultEditor(Class<?> requiredType) {
		if (!this.defaultEditorsActive) {
			return null;
		} else {
			if (this.overriddenDefaultEditors != null) {
				PropertyEditor editor = (PropertyEditor) this.overriddenDefaultEditors
						.get(requiredType);
				if (editor != null) {
					return editor;
				}
			}

			if (this.defaultEditors == null) {
				this.createDefaultEditors();
			}

			return (PropertyEditor) this.defaultEditors.get(requiredType);
		}
	}

	private void createDefaultEditors() {
		this.defaultEditors = new HashMap(64);
		this.defaultEditors.put(Charset.class, new CharsetEditor());
		this.defaultEditors.put(Class.class, new ClassEditor());
		this.defaultEditors.put(Class[].class, new ClassArrayEditor());
		this.defaultEditors.put(Currency.class, new CurrencyEditor());
		this.defaultEditors.put(File.class, new FileEditor());
		this.defaultEditors.put(InputStream.class, new InputStreamEditor());
		this.defaultEditors.put(InputSource.class, new InputSourceEditor());
		this.defaultEditors.put(Locale.class, new LocaleEditor());
		this.defaultEditors.put(Pattern.class, new PatternEditor());
		this.defaultEditors.put(Properties.class, new PropertiesEditor());
		this.defaultEditors.put(Resource[].class,
				new ResourceArrayPropertyEditor());
		this.defaultEditors.put(TimeZone.class, new TimeZoneEditor());
		this.defaultEditors.put(URI.class, new URIEditor());
		this.defaultEditors.put(URL.class, new URLEditor());
		this.defaultEditors.put(UUID.class, new UUIDEditor());
		this.defaultEditors.put(Collection.class, new CustomCollectionEditor(
				Collection.class));
		this.defaultEditors.put(Set.class,
				new CustomCollectionEditor(Set.class));
		this.defaultEditors.put(SortedSet.class, new CustomCollectionEditor(
				SortedSet.class));
		this.defaultEditors.put(List.class, new CustomCollectionEditor(
				List.class));
		this.defaultEditors.put(SortedMap.class, new CustomMapEditor(
				SortedMap.class));
		this.defaultEditors.put(byte[].class, new ByteArrayPropertyEditor());
		this.defaultEditors.put(char[].class, new CharArrayPropertyEditor());
		this.defaultEditors.put(Character.TYPE, new CharacterEditor(false));
		this.defaultEditors.put(Character.class, new CharacterEditor(true));
		this.defaultEditors.put(Boolean.TYPE, new CustomBooleanEditor(false));
		this.defaultEditors.put(Boolean.class, new CustomBooleanEditor(true));
		this.defaultEditors.put(Byte.TYPE, new CustomNumberEditor(Byte.class,
				false));
		this.defaultEditors.put(Byte.class, new CustomNumberEditor(Byte.class,
				true));
		this.defaultEditors.put(Short.TYPE, new CustomNumberEditor(Short.class,
				false));
		this.defaultEditors.put(Short.class, new CustomNumberEditor(
				Short.class, true));
		this.defaultEditors.put(Integer.TYPE, new CustomNumberEditor(
				Integer.class, false));
		this.defaultEditors.put(Integer.class, new CustomNumberEditor(
				Integer.class, true));
		this.defaultEditors.put(Long.TYPE, new CustomNumberEditor(Long.class,
				false));
		this.defaultEditors.put(Long.class, new CustomNumberEditor(Long.class,
				true));
		this.defaultEditors.put(Float.TYPE, new CustomNumberEditor(Float.class,
				false));
		this.defaultEditors.put(Float.class, new CustomNumberEditor(
				Float.class, true));
		this.defaultEditors.put(Double.TYPE, new CustomNumberEditor(
				Double.class, false));
		this.defaultEditors.put(Double.class, new CustomNumberEditor(
				Double.class, true));
		this.defaultEditors.put(BigDecimal.class, new CustomNumberEditor(
				BigDecimal.class, true));
		this.defaultEditors.put(BigInteger.class, new CustomNumberEditor(
				BigInteger.class, true));
		if (this.configValueEditorsActive) {
			StringArrayPropertyEditor sae = new StringArrayPropertyEditor();
			this.defaultEditors.put(String[].class, sae);
			this.defaultEditors.put(short[].class, sae);
			this.defaultEditors.put(int[].class, sae);
			this.defaultEditors.put(long[].class, sae);
		}

	}

	protected void copyDefaultEditorsTo(PropertyEditorRegistrySupport target) {
		target.defaultEditorsActive = this.defaultEditorsActive;
		target.configValueEditorsActive = this.configValueEditorsActive;
		target.defaultEditors = this.defaultEditors;
		target.overriddenDefaultEditors = this.overriddenDefaultEditors;
	}

	public void registerCustomEditor(Class<?> requiredType,
			PropertyEditor propertyEditor) {
		this.registerCustomEditor(requiredType, (String) null, propertyEditor);
	}

	public void registerCustomEditor(Class<?> requiredType,
			String propertyPath, PropertyEditor propertyEditor) {
		if (requiredType == null && propertyPath == null) {
			throw new IllegalArgumentException(
					"Either requiredType or propertyPath is required");
		} else {
			if (propertyPath != null) {
				if (this.customEditorsForPath == null) {
					this.customEditorsForPath = new LinkedHashMap(16);
				}

				this.customEditorsForPath.put(propertyPath,
						new PropertyEditorRegistrySupport.CustomEditorHolder(
								propertyEditor, requiredType));
			} else {
				if (this.customEditors == null) {
					this.customEditors = new LinkedHashMap(16);
				}

				this.customEditors.put(requiredType, propertyEditor);
				this.customEditorCache = null;
			}

		}
	}

	@Deprecated
	public void registerSharedEditor(Class<?> requiredType,
			PropertyEditor propertyEditor) {
		this.registerCustomEditor(requiredType, (String) null, propertyEditor);
		if (this.sharedEditors == null) {
			this.sharedEditors = new HashSet();
		}

		this.sharedEditors.add(propertyEditor);
	}

	public boolean isSharedEditor(PropertyEditor propertyEditor) {
		return this.sharedEditors != null
				&& this.sharedEditors.contains(propertyEditor);
	}

	public PropertyEditor findCustomEditor(Class<?> requiredType,
			String propertyPath) {
		Class requiredTypeToUse = requiredType;
		if (propertyPath != null) {
			if (this.customEditorsForPath != null) {
				PropertyEditor editor = this.getCustomEditor(propertyPath,
						requiredType);
				if (editor == null) {
					LinkedList strippedPaths = new LinkedList();
					this.addStrippedPropertyPaths(strippedPaths, "",
							propertyPath);

					String strippedPath;
					for (Iterator it = strippedPaths.iterator(); it.hasNext()
							&& editor == null; editor = this.getCustomEditor(
							strippedPath, requiredType)) {
						strippedPath = (String) it.next();
					}
				}

				if (editor != null) {
					return editor;
				}
			}

			if (requiredType == null) {
				requiredTypeToUse = this.getPropertyType(propertyPath);
			}
		}

		return this.getCustomEditor(requiredTypeToUse);
	}

	public boolean hasCustomEditorForElement(Class<?> elementType,
			String propertyPath) {
		if (propertyPath != null && this.customEditorsForPath != null) {
			Iterator i$ = this.customEditorsForPath.entrySet().iterator();

			while (i$.hasNext()) {
				Entry entry = (Entry) i$.next();
				if (PropertyAccessorUtils.matchesProperty(
						(String) entry.getKey(), propertyPath)
						&& ((PropertyEditorRegistrySupport.CustomEditorHolder) entry
								.getValue()).getPropertyEditor(elementType) != null) {
					return true;
				}
			}
		}

		return elementType != null && this.customEditors != null
				&& this.customEditors.containsKey(elementType);
	}

	protected Class<?> getPropertyType(String propertyPath) {
		return null;
	}

	private PropertyEditor getCustomEditor(String propertyName,
			Class<?> requiredType) {
		PropertyEditorRegistrySupport.CustomEditorHolder holder = (PropertyEditorRegistrySupport.CustomEditorHolder) this.customEditorsForPath
				.get(propertyName);
		return holder != null ? holder.getPropertyEditor(requiredType) : null;
	}

	private PropertyEditor getCustomEditor(Class<?> requiredType) {
		if (requiredType != null && this.customEditors != null) {
			PropertyEditor editor = (PropertyEditor) this.customEditors
					.get(requiredType);
			if (editor == null) {
				if (this.customEditorCache != null) {
					editor = (PropertyEditor) this.customEditorCache
							.get(requiredType);
				}

				if (editor == null) {
					Iterator it = this.customEditors.keySet().iterator();

					while (it.hasNext() && editor == null) {
						Class key = (Class) it.next();
						if (key.isAssignableFrom(requiredType)) {
							editor = (PropertyEditor) this.customEditors
									.get(key);
							if (this.customEditorCache == null) {
								this.customEditorCache = new HashMap();
							}

							this.customEditorCache.put(requiredType, editor);
						}
					}
				}
			}

			return editor;
		} else {
			return null;
		}
	}

	protected Class<?> guessPropertyTypeFromEditors(String propertyName) {
		if (this.customEditorsForPath != null) {
			PropertyEditorRegistrySupport.CustomEditorHolder editorHolder = (PropertyEditorRegistrySupport.CustomEditorHolder) this.customEditorsForPath
					.get(propertyName);
			if (editorHolder == null) {
				LinkedList strippedPaths = new LinkedList();
				this.addStrippedPropertyPaths(strippedPaths, "", propertyName);

				String strippedName;
				for (Iterator it = strippedPaths.iterator(); it.hasNext()
						&& editorHolder == null; editorHolder = (PropertyEditorRegistrySupport.CustomEditorHolder) this.customEditorsForPath
						.get(strippedName)) {
					strippedName = (String) it.next();
				}
			}

			if (editorHolder != null) {
				return editorHolder.getRegisteredType();
			}
		}

		return null;
	}

	protected void copyCustomEditorsTo(PropertyEditorRegistry target,
			String nestedProperty) {
		String actualPropertyName = nestedProperty != null
				? PropertyAccessorUtils.getPropertyName(nestedProperty)
				: null;
		Iterator i$;
		Entry entry;
		if (this.customEditors != null) {
			i$ = this.customEditors.entrySet().iterator();

			while (i$.hasNext()) {
				entry = (Entry) i$.next();
				target.registerCustomEditor((Class) entry.getKey(),
						(PropertyEditor) entry.getValue());
			}
		}

		if (this.customEditorsForPath != null) {
			i$ = this.customEditorsForPath.entrySet().iterator();

			while (true) {
				PropertyEditorRegistrySupport.CustomEditorHolder editorHolder;
				String editorNestedProperty;
				String editorNestedPath;
				do {
					String editorPath;
					int pos;
					label34 : do {
						while (i$.hasNext()) {
							entry = (Entry) i$.next();
							editorPath = (String) entry.getKey();
							editorHolder = (PropertyEditorRegistrySupport.CustomEditorHolder) entry
									.getValue();
							if (nestedProperty != null) {
								pos = PropertyAccessorUtils
										.getFirstNestedPropertySeparatorIndex(editorPath);
								continue label34;
							}

							target.registerCustomEditor(
									editorHolder.getRegisteredType(),
									editorPath,
									editorHolder.getPropertyEditor());
						}

						return;
					} while (pos == -1);

					editorNestedProperty = editorPath.substring(0, pos);
					editorNestedPath = editorPath.substring(pos + 1);
				} while (!editorNestedProperty.equals(nestedProperty)
						&& !editorNestedProperty.equals(actualPropertyName));

				target.registerCustomEditor(editorHolder.getRegisteredType(),
						editorNestedPath, editorHolder.getPropertyEditor());
			}
		}
	}

	private void addStrippedPropertyPaths(List<String> strippedPaths,
			String nestedPath, String propertyPath) {
		int startIndex = propertyPath.indexOf(91);
		if (startIndex != -1) {
			int endIndex = propertyPath.indexOf(93);
			if (endIndex != -1) {
				String prefix = propertyPath.substring(0, startIndex);
				String key = propertyPath.substring(startIndex, endIndex + 1);
				String suffix = propertyPath.substring(endIndex + 1,
						propertyPath.length());
				strippedPaths.add(nestedPath + prefix + suffix);
				this.addStrippedPropertyPaths(strippedPaths, nestedPath
						+ prefix, suffix);
				this.addStrippedPropertyPaths(strippedPaths, nestedPath
						+ prefix + key, suffix);
			}
		}

	}

	private static class CustomEditorHolder {
		private final PropertyEditor propertyEditor;
		private final Class<?> registeredType;

		private CustomEditorHolder(PropertyEditor propertyEditor,
				Class<?> registeredType) {
			this.propertyEditor = propertyEditor;
			this.registeredType = registeredType;
		}

		private PropertyEditor getPropertyEditor() {
			return this.propertyEditor;
		}

		private Class<?> getRegisteredType() {
			return this.registeredType;
		}

		private PropertyEditor getPropertyEditor(Class<?> requiredType) {
			return this.registeredType != null
					&& (requiredType == null || !ClassUtils.isAssignable(
							this.registeredType, requiredType)
							&& !ClassUtils.isAssignable(requiredType,
									this.registeredType))
					&& (requiredType != null
							|| Collection.class
									.isAssignableFrom(this.registeredType) || this.registeredType
								.isArray()) ? null : this.propertyEditor;
		}
	}
}