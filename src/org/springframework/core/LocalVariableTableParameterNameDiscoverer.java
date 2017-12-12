/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/
package org.springframework.core;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.asm.ClassReader;
import org.springframework.asm.Label;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Type;
import org.springframework.asm.commons.EmptyVisitor;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.ClassUtils;

public class LocalVariableTableParameterNameDiscoverer implements
		ParameterNameDiscoverer {
	private static Log logger = LogFactory
			.getLog(LocalVariableTableParameterNameDiscoverer.class);
	private static final Map<Member, String[]> NO_DEBUG_INFO_MAP = Collections
			.emptyMap();
	private final Map<Class<?>, Map<Member, String[]>> parameterNamesCache = new ConcurrentHashMap();

	public String[] getParameterNames(Method method) {
		Class declaringClass = method.getDeclaringClass();
		Map map = (Map) this.parameterNamesCache.get(declaringClass);
		if (map == null) {
			map = this.inspectClass(declaringClass);
			this.parameterNamesCache.put(declaringClass, map);
		}

		return map != NO_DEBUG_INFO_MAP ? (String[]) map.get(method) : null;
	}

	public String[] getParameterNames(Constructor ctor) {
		Class declaringClass = ctor.getDeclaringClass();
		Map map = (Map) this.parameterNamesCache.get(declaringClass);
		if (map == null) {
			map = this.inspectClass(declaringClass);
			this.parameterNamesCache.put(declaringClass, map);
		}

		return map != NO_DEBUG_INFO_MAP ? (String[]) map.get(ctor) : null;
	}

	private Map<Member, String[]> inspectClass(Class<?> clazz) {
		InputStream is = clazz.getResourceAsStream(ClassUtils
				.getClassFileName(clazz));
		if (is == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Cannot find \'.class\' file for class ["
						+ clazz
						+ "] - unable to determine constructors/methods parameter names");
			}

			return NO_DEBUG_INFO_MAP;
		} else {
			try {
				ClassReader ex = new ClassReader(is);
				ConcurrentHashMap map = new ConcurrentHashMap();
				ex.accept(
						new LocalVariableTableParameterNameDiscoverer.ParameterNameDiscoveringVisitor(
								clazz, map), false);
				ConcurrentHashMap arg4 = map;
				return arg4;
			} catch (IOException arg14) {
				if (logger.isDebugEnabled()) {
					logger.debug(
							"Exception thrown while reading \'.class\' file for class ["
									+ clazz
									+ "] - unable to determine constructors/methods parameter names",
							arg14);
				}
			} finally {
				try {
					is.close();
				} catch (IOException arg13) {
					;
				}

			}

			return NO_DEBUG_INFO_MAP;
		}
	}

	private static class LocalVariableTableVisitor extends EmptyVisitor {
		private static final String CONSTRUCTOR = "<init>";
		private final Class<?> clazz;
		private final Map<Member, String[]> memberMap;
		private final String name;
		private final Type[] args;
		private final boolean isStatic;
		private String[] parameterNames;
		private boolean hasLvtInfo = false;
		private final int[] lvtSlotIndex;

		public LocalVariableTableVisitor(Class<?> clazz,
				Map<Member, String[]> map, String name, String desc,
				boolean isStatic) {
			this.clazz = clazz;
			this.memberMap = map;
			this.name = name;
			this.args = Type.getArgumentTypes(desc);
			this.parameterNames = new String[this.args.length];
			this.isStatic = isStatic;
			this.lvtSlotIndex = computeLvtSlotIndices(isStatic, this.args);
		}

		public void visitLocalVariable(String name, String description,
				String signature, Label start, Label end, int index) {
			this.hasLvtInfo = true;

			for (int i = 0; i < this.lvtSlotIndex.length; ++i) {
				if (this.lvtSlotIndex[i] == index) {
					this.parameterNames[i] = name;
				}
			}

		}

		public void visitEnd() {
			if (this.hasLvtInfo || this.isStatic
					&& this.parameterNames.length == 0) {
				this.memberMap.put(this.resolveMember(), this.parameterNames);
			}

		}

		private Member resolveMember() {
			ClassLoader loader = this.clazz.getClassLoader();
			Class[] classes = new Class[this.args.length];

			for (int ex = 0; ex < this.args.length; ++ex) {
				classes[ex] = ClassUtils.resolveClassName(
						this.args[ex].getClassName(), loader);
			}

			try {
				return (Member) ("<init>".equals(this.name) ? this.clazz
						.getDeclaredConstructor(classes) : this.clazz
						.getDeclaredMethod(this.name, classes));
			} catch (NoSuchMethodException arg3) {
				throw new IllegalStateException(
						"Method ["
								+ this.name
								+ "] was discovered in the .class file but cannot be resolved in the class object",
						arg3);
			}
		}

		private static int[] computeLvtSlotIndices(boolean isStatic,
				Type[] paramTypes) {
			int[] lvtIndex = new int[paramTypes.length];
			int nextIndex = isStatic ? 0 : 1;

			for (int i = 0; i < paramTypes.length; ++i) {
				lvtIndex[i] = nextIndex;
				if (isWideType(paramTypes[i])) {
					nextIndex += 2;
				} else {
					++nextIndex;
				}
			}

			return lvtIndex;
		}

		private static boolean isWideType(Type aType) {
			return aType == Type.LONG_TYPE || aType == Type.DOUBLE_TYPE;
		}
	}

	private static class ParameterNameDiscoveringVisitor extends EmptyVisitor {
		private static final String STATIC_CLASS_INIT = "<clinit>";
		private final Class<?> clazz;
		private final Map<Member, String[]> memberMap;

		public ParameterNameDiscoveringVisitor(Class<?> clazz,
				Map<Member, String[]> memberMap) {
			this.clazz = clazz;
			this.memberMap = memberMap;
		}

		public MethodVisitor visitMethod(int access, String name, String desc,
				String signature, String[] exceptions) {
			return !isSyntheticOrBridged(access) && !"<clinit>".equals(name) ? new LocalVariableTableParameterNameDiscoverer.LocalVariableTableVisitor(
					this.clazz, this.memberMap, name, desc, isStatic(access))
					: null;
		}

		private static boolean isSyntheticOrBridged(int access) {
			return (access & 4096 | access & 64) > 0;
		}

		private static boolean isStatic(int access) {
			return (access & 8) > 0;
		}
	}
}