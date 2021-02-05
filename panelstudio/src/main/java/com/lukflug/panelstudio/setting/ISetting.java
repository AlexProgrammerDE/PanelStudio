package com.lukflug.panelstudio.setting;

import java.util.stream.Stream;

import com.lukflug.panelstudio.base.IBoolean;

/**
 * Interface representing a single setting.
 * @author lukflug
 */
public interface ISetting extends ILabeled {
	/**
	 * Returns boolean interface indicating whether the setting is visible.
	 * @return the visibility of the setting
	 */
	public IBoolean isVisible();
	
	/**
	 * Returns sub settings.
	 * @return sub-settings
	 */
	public Stream<ISetting> getSubSettings();
}
