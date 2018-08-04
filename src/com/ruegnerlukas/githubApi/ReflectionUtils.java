package com.ruegnerlukas.githubApi;

import java.lang.reflect.Field;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;

public class ReflectionUtils {

	
	
	public static boolean setBoolField(Object obj, String key, boolean value) {
		Field field = null;
		try {
			field = obj.getClass().getDeclaredField(key);
			field.setBoolean(obj, value);
			return true;
		} catch (NoSuchFieldException e) {
			return false;
		} catch(SecurityException | IllegalArgumentException | IllegalAccessException e) {
			Logger.get().warn(e);
			return false;
		}
	}
	
	
	
	
	public static boolean setIntField(Object obj, String key, int value) {
		Field field = null;
		try {
			field = obj.getClass().getDeclaredField(key);
			field.setInt(obj, value);
			return true;
		} catch (NoSuchFieldException e) {
			return false;
		} catch(SecurityException | IllegalArgumentException | IllegalAccessException e) {
			Logger.get().warn(e);
			return false;
		}
	}
	
	
	
	
	public static boolean setStringField(Object obj, String key, String value) {
		Field field = null;
		try {
			field = obj.getClass().getDeclaredField(key);
			field.set(obj, value);
			return true;
		} catch (NoSuchFieldException e) {
			return false;
		} catch(SecurityException | IllegalArgumentException | IllegalAccessException e) {
			Logger.get().warn(e);
			return false;
		}
	}
	
	
	
}
