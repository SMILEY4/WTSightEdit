package com.ruegnerlukas.wtsights.ui.language;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class DefaultResourceBundle extends ResourceBundle {

	
	private Map<String,String> map = new HashMap<String,String>();
	
	
	
	
	public DefaultResourceBundle() {
		
		// MAIN MENU
		map.put("mm_title", 						"WT Sight Editor");
		map.put("mm_sight_tank_create", 			"Create new Sight");
		map.put("mm_sight_tank_create_tt", 			"Create a new custom tank-sight.");
		map.put("mm_sight_tank_load", 				"Load Sight");
		map.put("mm_sight_tank_load_tt", 			"Load and edit an existing tank-sight.");
		map.put("mm_sight_tank_metadata_create", 	"Create Ballistic Data");
		map.put("mm_sight_tank_metadata_create_tt", "Create a new Calibration file. A Calibration file is used to calculate the correct bullet trajectories.");
		map.put("mm_sight_tank_metadata_load", 		"Load Ballistic Data");
		map.put("mm_sight_tank_metadata_load_tt", 	"Load and edit an existing calibration file.");
		map.put("mm_about", 						"About");
		map.put("mm_about_tt", 						"About WTSightEdit");
		map.put("mm_help", 							"Help");
		map.put("mm_help_tt", 						"Help");
		map.put("mm_settings", 						"Settings");
		map.put("mm_settings_dark_theme", 			"Dark Theme");
		
		// ABOUT
		map.put("ab_title", 			"About WT Sight Editor");
		map.put("ab_creator", 			"Created by smiley_4_");
		map.put("ab_product_version", 	"Product Version");
		map.put("ab_build_info", 		"Build information");
		map.put("ab_build_version", 	"Version: ");
		map.put("ab_build_date", 		"Date: ");
		map.put("ab_java_version", 		"Java version (installed)");

		// HELP
		map.put("hp_help", 				"Help");
		map.put("hp_how_to", 			"How to create a custom tank sight?");
		map.put("hp_video", 			"A video walkthrough can be found at:");
		map.put("hp_video_url", 		"https://youtu.be/bRAGADRql1g");
		map.put("hp_feedback_title", 	"Found a bug, want to give feedback or ask a question ?");
		map.put("hp_feedback_body", 	"		Write me a message on reddit (smiley_4_) or leave a comment under my last reddit post.");

		// CALIBRATION EDITOR
		map.put("ce_title", 			"Create Ballistic Data");
		map.put("ce_zoomed_in", 		"Zoomed In");
		map.put("ce_zoomed_in_tt", 		"Whether or not the selected background image is zoomed in.");
		map.put("ce_marked_ranges", 	"Distances of marked ranges:");
		map.put("ce_export", 			"Export");
		map.put("ce_export_tt", 		"Save this calibration as a file.");
		map.put("ce_edit_sight", 		"Edit Sight");
		map.put("ce_edit_sight_tt",		"Create a custom sight without saving this calibration to a file.");
		map.put("ce_export_title", 		"Save Ballistic Data");
		map.put("ce_alert_export_missing","Can not save data. At least one shell does not have enough markers.");
		map.put("ce_alert_export_failed","Save Ballistic Data");

		
		// CALIBRATION SELECT
		map.put("cs_title", 			"Select Ballistic Data");
		map.put("cs_create_new", 		"Create new Ballistic Data");
		map.put("cs_create_new_tt", 	"Create a new Calibration instead of using an existing file.");
		map.put("cs_path_tt", 			"The path of the selected file.");
		map.put("cs_browse", 			"Browse");
		map.put("cs_browse_tt", 		"Browse files.");
		map.put("cs_browse_title", 		"Open Calibration Data");
		map.put("cs_browse_file_tye", 	"Calibration File");
		map.put("cs_next", 				"Next");
		map.put("cs_cancel", 			"Cancel");
		map.put("cs_alert_missing", 	"No Ballistic Data selected. Select Ballistic Data to continue.");

		// SCREENSHOT UPLOAD
		map.put("ssu_title", 			"Upload Screenshots");
		map.put("ssu_selected_tank", 	"Selected tank name:");
		map.put("ssu_browse", 			"Browse");
		map.put("ssu_reset", 			"X");
		map.put("ssu_file_type_img", 	"Image");
		map.put("ssu_triggergroup_tt", 	"TriggerGroup = ");
		map.put("ssu_shelltype_tt", 	"Type = ");
		map.put("ssu_next", 			"Next");
		map.put("ssu_cancel", 			"Cancel");
		map.put("ssu_alert_missing", 	"No Images selected. Select at least one image to continue.");

		
		// VEHICLE SELECT
		map.put("vs_title", 			"Select Vehicle");
		map.put("vs_header", 			"Select Vehicle:");
		map.put("vs_filter_prompt", 	"Filter vehicle list");
		map.put("vs_filter", 			"Filter");
		map.put("vs_filter_tt", 		"Filter vehicle list.");
		map.put("vs_vehicle_list_tt", 	"The list of available vehicles.");
		map.put("vs_next", 				"Next");
		map.put("vs_cancel", 			"Cancel");
		map.put("vs_alert_mssing", 		"No vehicle selected. Select vehicle to continue.");

		// SIGHT EDITOR
		map.put("se_title", 					"Edit Sight");
		map.put("se_elements_add", 				"Add");
		map.put("se_elements_delete", 			"Delete");
		map.put("se_elements_rename", 			"Rename");
		map.put("se_elements_rename_dialog",	"Rename Element ({elementname})");
		map.put("se_elements_rename_null", 		"Error: Name is null.");
		map.put("se_elements_rename_empty", 	"The name of the element can not be empty.");
		map.put("se_elements_rename_duplicate", "The name of the element must be unique.");
		map.put("se_elements_delete_dialog_title","Delete Element");
		map.put("se_elements_delete_dialog_header","You are about to delete the element");
		map.put("se_elements_delete_dialog_content","Do you want to delete this element?");
		map.put("se_elements_delete_dialog_delete","Delete");
		map.put("se_elements_delete_dialog_cancel","Cancel");
		map.put("se_export", 					"Export");
		map.put("se_export_title", 				"Save Sight");
		map.put("se_alert_export_failed", 		"Sight could not be saved.");
		map.put("se_selections_show", 			"Show Selections");
		// create element
		map.put("se_ce_title", 					"Create Element");
		map.put("se_ce_element_type", 			"Element Type:");
		map.put("se_ce_element_name", 			"Element Name:");
		map.put("se_ce_cancel", 				"Cancel");
		map.put("se_ce_done", 					"Done");
		map.put("se_ce_alert_name_null", 		"Error: Name is null.");
		map.put("se_ce_alert_name_empty", 		"The name of the element can not be empty.");
		map.put("se_ce_alert_name_duplicate",	"The name of the element must be unique.");
		// environment
		map.put("se_env_rangefinder_show", 		"Show Rangefinder");
		map.put("se_env_rangefinder_progress",	"Rangefinder Progress:");
		map.put("se_env_range_correction",		"Rangefinder Correction:");
		map.put("se_env_crosshair_light",		"Enable Crosshair Lighting");
		map.put("se_env_grid_show",				"Display Grid Overlay");
		map.put("se_env_background_header",		"Background:");
		map.put("se_env_background_browse",		"Browse");
		map.put("se_env_background_reset",		"X");
		map.put("se_env_background_file_type",	"Image");
		map.put("se_env_resolution_header",		"Resolution:");
		map.put("se_env_zoomed_in",				"Show Zoomed In");
		map.put("se_env_zoomed_out",			"Show Zoomed Out");
		// general
		map.put("se_gen_thousandth_header",		"Thousandth:");
		map.put("se_gen_font_size",				"Font Size Scale:");
		map.put("se_gen_line_size",				"Line Size Scale:");
		map.put("se_gen_apply_correction",		"Apply Correction to Gun");
		// ballistic range indicators
		map.put("se_mbri_settings_header",		"Settings");
		map.put("se_mbri_lines",				"Lines");
		map.put("se_mbri_circles",				"Circles");
		map.put("se_mbri_text_shift",			"Text Shift:");
		map.put("se_mbri_text_offset",			"Text Offset:");
		map.put("se_mbri_text_alignment",		"Text Alignment:");
		map.put("se_mbri_position",				"Position:");
		map.put("se_mbri_size",					"Size (Major,Minor):");
		map.put("se_mbri_add_lines",			"Draw Additional Lines:");
		map.put("se_mbri_add_size",				"Size Additional (Major,Minor):");
		map.put("se_mbri_size_stroke_width",	"Size / Stroke Width:");
		map.put("se_mbri_stroke_width",			"Stroke Width:");
		map.put("se_mbri_line_size",			"Size:");
		map.put("se_mbri_circle_radius",		"Circle Radius:");
		map.put("se_mbri_circle_radius",		"Radius:");
		map.put("se_mbri_use_mils",				"Use Mils:");
		map.put("se_mbri_stretch_angle",		"Radial Stretch and Angle:");
		map.put("se_mbri_text_offset_radial",	"Text Offset:");
		map.put("se_mbri_text_alignment_radial","Text Alignment:");
		map.put("se_mbri_draw_upward",			"Draw Upward");
		map.put("se_mbri_can_move",				"Can Move");
		map.put("se_mbri_range_label_draw",		"Draw Range Correction Label");
		map.put("se_mbri_range_label_pos",		"Range Correction Label");
		map.put("se_mbri_distances_header",		"Distances");
		map.put("se_mbri_table_distance",		"Distance");
		map.put("se_mbri_table_rank",			"Rank");
		map.put("se_mbri_table_extend",			"Extend");
		map.put("se_mbri_table_text_x",			"Text X");
		map.put("se_mbri_table_text_y",			"Text Y");
		map.put("se_mbri_cell_major",			"MAJOR");
		map.put("se_mbri_cell_minor",			"minor");
		map.put("se_mbri_cell_remove",			"X");
		// central horz line
		map.put("se_mchl_draw",					"Draw Central Horizontal Line");
		// central vert line
		map.put("se_mcvl_draw",					"Draw Central Vertical Line");
		// custom circle
		map.put("se_mcc_use_thousandth",		"Use Thousandth");
		map.put("se_mcc_movement",				"Movement:");
		map.put("se_mcc_angle",					"Angle:");
		map.put("se_mcc_center",				"Center:");
		map.put("se_mcc_auto_center",			"Use Auto-Center:");
		map.put("se_mcc_origin",				"Origin:");
		map.put("se_mcc_move_speed",			"Move Speed:");
		map.put("se_mcc_position",				"Position:");
		map.put("se_mcc_diameter",				"Diameter:");
		map.put("se_mcc_segment",				"Segment:");
		map.put("se_mcc_thickness",				"Outline Thickness:");
		// custom line
		map.put("se_mcl_use_thousandth",		"Use Thousandth");
		map.put("se_mcl_movement",				"Movement:");
		map.put("se_mcl_angle",					"Angle:");
		map.put("se_mcl_center",				"Center:");
		map.put("se_mcl_auto_center",			"Use Auto-Center:");
		map.put("se_mcl_origin",				"Origin:");
		map.put("se_mcl_move_speed",			"Move Speed:");
		map.put("se_mcl_pos_start",				"Position Start:");
		map.put("se_mcl_pos_end",				"Position End:");
		// custom quad
		map.put("se_mcq_use_thousandth",		"Use Thousandth");
		map.put("se_mcq_movement",				"Movement:");
		map.put("se_mcq_angle",					"Angle:");
		map.put("se_mcq_center",				"Center:");
		map.put("se_mcq_auto_center",			"Use Auto-Center:");
		map.put("se_mcq_origin",				"Origin:");
		map.put("se_mcq_move_speed",			"Move Speed:");
		map.put("se_mcq_position_1",			"Position 1:");
		map.put("se_mcq_position_2",			"Position 2:");
		map.put("se_mcq_position_3",			"Position 3:");
		map.put("se_mcq_position_4",			"Position 4:");
		// custom text
		map.put("se_mct_use_thousandth",		"Use Thousandth");
		map.put("se_mct_movement",				"Movement:");
		map.put("se_mct_angle",					"Angle:");
		map.put("se_mct_center",				"Center:");
		map.put("se_mct_auto_center",			"Use Auto-Center:");
		map.put("se_mct_origin",				"Origin:");
		map.put("se_mct_move_speed",			"Move Speed:");
		map.put("se_mct_text",					"Text:");
		map.put("se_mct_position",				"Position:");
		map.put("se_mct_size",					"Size:");
		map.put("se_mct_alignment",				"Alignment:");
		// horz range indicators
		map.put("se_mhri_size_major",			"Size Major:");
		map.put("se_mhri_size_minor",			"Size Minor:");
		map.put("se_mhri_table_mil",			"Mil");
		map.put("se_mhri_table_rank",			"Rank");
		map.put("se_mhri_cell_major",			"MAJOR");
		map.put("se_mhri_cell_minor",			"minor");
		map.put("se_mhri_cell_remove",			"X");
		// rangefinder
		map.put("se_mrf_position",				"Position:");
		map.put("se_mrf_use_thousandth",		"Use Thousandth");
		map.put("se_mrf_text_scale",			"Text Scale:");
		map.put("se_mrf_color_1",				"Color 1:");
		map.put("se_mrf_color_2",				"Color 2:");
		// shell block
		map.put("se_msb_settings_header",			"Settings");
		map.put("se_msb_lines",						"Lines");
		map.put("se_msb_circles",					"Circles");
		map.put("se_msb_vert_text_shift",			"Text Shift:");
		map.put("se_msb_vert_text_offset",			"Text Offset:");
		map.put("se_msb_vert_text_alignment",		"Text Alignment:");
		map.put("se_msb_vert_position",				"Position:");
		map.put("se_msb_vert_size",					"Size (Major,Minor):");
		map.put("se_msb_vert_add_lines",			"Draw Additional Lines:");
		map.put("se_msb_vert_add_size",				"Size Additional (Major,Minor):");
		map.put("se_msb_rad_position",				"Position:");
		map.put("se_msb_rad_size_stroke_width",		"Size / Stroke Width:");
		map.put("se_msb_stroke_width",				"Stroke Width:");
		map.put("se_msb_line_size",					"Size:");
		map.put("se_msb_rad_circle_radius"	,		"Circle Radius:");
		map.put("se_msb_rad_radius",				"Radius:");
		map.put("se_msb_rad_stretch_angle",			"Radial Stretch and Angle:");
		map.put("se_msb_rad_text_offset",			"Text Offset:");
		map.put("se_msb_rad_text_alignment",		"Text Alignment:");
		map.put("se_msb_draw_upward",				"Draw Upward");
		map.put("se_msb_can_move",					"Can Move");
		map.put("se_msb_range_label_draw",			"Draw Range Correction Label");
		map.put("se_msb_range_label_pos",			"Range Correction Label");
		map.put("se_msb_distances_header",			"Distances");
		map.put("se_msb_table_distance",			"Distance");
		map.put("se_msb_table_rank",				"Rank");
		map.put("se_msb_table_extend",				"Extend");
		map.put("se_msb_table_text_x",				"Text X");
		map.put("se_msb_table_text_y",				"Text Y");
		map.put("se_msb_cell_major",				"MAJOR");
		map.put("se_msb_cell_minor",				"minor");
		map.put("se_msb_cell_remove",				"X");
		
		// LAUNCHER
		map.put("lc_title",				"WT Sight Editor");
		map.put("lc_skip_search",		"Skip");
		map.put("lc_skip_update",		"Skip");
		map.put("lc_skip_update",		"Update");
		map.put("lc_update_found_msg",	"New version found: {new}. Current version: {current}");
		map.put("lc_update_not_found_msg","Already up-to-date. ({current})");
		map.put("lc_updating_msg",		"Updating to version {new} ...");

		
		
	}
	
	
	
	
	@Override
	protected Object handleGetObject(String key) {
		return map.getOrDefault(key, "{unknown}");
	}

	
	
	
	@Override
	public Enumeration<String> getKeys() {
		return Collections.enumeration(map.keySet());
	}
	
}
