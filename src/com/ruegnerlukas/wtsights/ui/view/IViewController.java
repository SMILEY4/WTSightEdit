package com.ruegnerlukas.wtsights.ui.view;

import java.util.Map;

import com.ruegnerlukas.wtsights.ui.view.ViewManager.ParamKey;

public interface IViewController {

	void create(Map<ParamKey,Object> parameters);
	
}
