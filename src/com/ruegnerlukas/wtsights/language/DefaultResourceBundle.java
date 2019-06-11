package com.ruegnerlukas.wtsights.language;

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
		map.put("mm_sight_tank_metadata_create", 	"Create Sight Metadata");
		map.put("mm_sight_tank_metadata_create_tt", "Create a new Sight Metadata file. A Metadata file is used to store information about the sight, for example data to calculate the correct bullet trajectories.");
		map.put("mm_sight_tank_metadata_load", 		"Load Sight Metadata");
		map.put("mm_sight_tank_metadata_load_tt", 	"Load and edit an existing Metadata file.");
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
		map.put("ce_title", 			"Create Sight Metadata");
		map.put("ce_img_zoomed_in", 	"Sight Zoomed In");
		map.put("ce_img_zoomed_out", 	"Sight Zoomed Out");
		map.put("ce_zoomed_in", 		"Is zoomed in");
		map.put("ce_zoomed_in_tt", 		"Whether or not the selected background image is zoomed in.");
		map.put("ce_zoom_mod_out",		"Zoom (Out) Multiplier");
		map.put("ce_zoom_mod_in",		"Zoom (In) Multiplier");
		map.put("ce_zoom_mod_out_tt",	"Multiplies the zoom by this value. Can be used if the zoom of the ingame sight is different from the one in the editor.");
		map.put("ce_zoom_mod_in_tt",	"Multiplies the zoom by this value. Can be used if the zoom of the ingame sight is different from the one in the editor.");
		map.put("ce_marked_ranges", 	"Distances of marked ranges:");
		map.put("ce_export", 			"Export");
		map.put("ce_export_tt", 		"Save this Sight Metadata as a file.");
		map.put("ce_edit_sight", 		"Edit Sight");
		map.put("ce_edit_sight_tt",		"Create a custom sight without saving this metadata to a file.");
		map.put("ce_export_title", 		"Save Sight Metadata");
		map.put("ce_alert_export_missing","Can not save data. At least one shell does not have enough markers.");
		map.put("ce_alert_export_failed","Save Sight Metadata");

		
		// CALIBRATION SELECT
		map.put("cs_title", 			"Select Sight Metadata");
		map.put("cs_create_new", 		"Create new Sight Metadata");
		map.put("cs_create_new_tt", 	"Create a new Sight Metadata instead of using an existing file.");
		map.put("cs_path_tt", 			"The path of the selected file.");
		map.put("cs_browse", 			"Browse");
		map.put("cs_browse_tt", 		"Browse files.");
		map.put("cs_browse_title", 		"Open Sight Metadata");
		map.put("cs_browse_file_tye", 	"Sight Metadata");
		map.put("cs_next", 				"Next");
		map.put("cs_cancel", 			"Cancel");
		map.put("cs_alert_missing", 	"No Sight Metadata selected. Select Sight Metadata to continue.");

		// SCREENSHOT UPLOAD
		map.put("ssu_title", 			"Upload Screenshots");
		map.put("ssu_selected_tank", 	"Selected tank name:");
		map.put("ssu_zoomed_in", 		"Zoomed In");
		map.put("ssu_zoomed_in_tt", 	"An image of a zoomed in sight. Not mandatory, but can be used if the zoom of the ingame sight is different from the one in the editor.");
		map.put("ssu_zoomed_out", 		"Zoomed Out");
		map.put("ssu_zoomed_out_tt", 	"An image of the normal sight. Not mandatory, but can be used if the zoom of the ingame sight is different from the one in the editor.");
		map.put("ssu_browse", 			"Browse");
		map.put("ssu_reset", 			"X");
		map.put("ssu_file_type_img", 	"Image");
		map.put("ssu_triggergroup_tt", 	"TriggerGroup = ");
		map.put("ssu_shellspeed_tt", 	"Speed = ");
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
		map.put("se_ce_alert_name_forbidden",	"The name of the element contains a character that is not allowed.");
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
		map.put("se_env_background_offset",		"Background Offset:");
		map.put("se_env_background_scale_rotation","Background Scale & Rotation:");
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
		map.put("se_mbri_radius",				"Radius:");
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
		// custom circle outline
		map.put("se_mcco_use_thousandth",		"Use Thousandth");
		map.put("se_mcco_movement",				"Movement:");
		map.put("se_mcco_angle",					"Angle:");
		map.put("se_mcco_center",				"Center:");
		map.put("se_mcco_auto_center",			"Use Auto-Center:");
		map.put("se_mcco_origin",				"Origin:");
		map.put("se_mcco_move_speed",			"Move Speed:");
		map.put("se_mcco_position",				"Position:");
		map.put("se_mcco_diameter",				"Diameter:");
		map.put("se_mcco_segment",				"Segment:");
		map.put("se_mcco_thickness",				"Outline Thickness:");
		// custom circle filled
		map.put("se_mccf_use_thousandth",		"Use Thousandth");
		map.put("se_mccf_movement",				"Movement:");
		map.put("se_mccf_angle",					"Angle:");
		map.put("se_mccf_center",				"Center:");
		map.put("se_mccf_auto_center",			"Use Auto-Center:");
		map.put("se_mccf_origin",				"Origin:");
		map.put("se_mccf_move_speed",			"Move Speed:");
		map.put("se_mccf_position",				"Position:");
		map.put("se_mccf_diameter",				"Diameter:");
		map.put("se_mccf_segment",				"Segment:");
		map.put("se_mccf_quality", 				"Quality:");
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
		map.put("se_mcl_pos_offset",			"Position Offset:");
		// custom quad filled
		map.put("se_mcqf_use_thousandth",		"Use Thousandth");
		map.put("se_mcqf_movement",				"Movement:");
		map.put("se_mcqf_angle",				"Angle:");
		map.put("se_mcqf_center",				"Center:");
		map.put("se_mcqf_auto_center",			"Use Auto-Center:");
		map.put("se_mcqf_origin",				"Origin:");
		map.put("se_mcqf_move_speed",			"Move Speed:");
		map.put("se_mcqf_position_1",			"Position 1:");
		map.put("se_mcqf_position_2",			"Position 2:");
		map.put("se_mcqf_position_3",			"Position 3:");
		map.put("se_mcqf_position_4",			"Position 4:");
		map.put("se_mcqf_pos_offset",			"Position Offset:");
		// custom quad outline
		map.put("se_mcqo_use_thousandth",		"Use Thousandth");
		map.put("se_mcqo_movement",				"Movement:");
		map.put("se_mcqo_angle",				"Angle:");
		map.put("se_mcqo_center",				"Center:");
		map.put("se_mcqo_auto_center",			"Use Auto-Center:");
		map.put("se_mcqo_origin",				"Origin:");
		map.put("se_mcqo_move_speed",			"Move Speed:");
		map.put("se_mcqo_position_1",			"Position 1:");
		map.put("se_mcqo_position_2",			"Position 2:");
		map.put("se_mcqo_position_3",			"Position 3:");
		map.put("se_mcqo_position_4",			"Position 4:");
		map.put("se_mcqo_pos_offset",			"Position Offset:");
		// custom polygon outline
		map.put("se_mcpo_use_thousandth",		"Use Thousandth");
		map.put("se_mcpo_movement",				"Movement:");
		map.put("se_mcpo_angle",				"Angle:");
		map.put("se_mcpo_center",				"Center:");
		map.put("se_mcpo_auto_center",			"Use Auto-Center:");
		map.put("se_mcpo_origin",				"Origin:");
		map.put("se_mcpo_move_speed",			"Move Speed:");
		map.put("se_mcpo_vertices_title",		"Vertices:");
		map.put("se_mcpo_add_vertex",			"Add Vertex");
		map.put("se_mcpo_remove_vertex",		"X");
		map.put("se_mcpo_pos_offset",			"Position Offset:");
		// custom polygon filled
		map.put("se_mcpf_use_thousandth",		"Use Thousandth");
		map.put("se_mcpf_movement",				"Movement:");
		map.put("se_mcpf_angle",				"Angle:");
		map.put("se_mcpf_center",				"Center:");
		map.put("se_mcpf_auto_center",			"Use Auto-Center:");
		map.put("se_mcpf_origin",				"Origin:");
		map.put("se_mcpf_move_speed",			"Move Speed:");
		map.put("se_mcpf_vertices_title",		"Vertices:");
		map.put("se_mcpf_add_vertex",			"Add Vertex");
		map.put("se_mcpf_remove_vertex",		"X");
		map.put("se_mcpf_pos_offset",			"Position Offset:");
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
		map.put("se_mct_enable_highlight",		"Enable Lighting");
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
		// funnel
		map.put("se_mf_target_size",				"Target Size (in cm)");
		map.put("se_mf_range_start",				"Range Start");
		map.put("se_mf_range_end",					"Range End");
		map.put("se_mf_range_step",					"Range Step");
		map.put("se_mf_move",						"Move");
		map.put("se_mf_show_right",					"Show Right Side");
		map.put("se_mf_show_left",					"Show Left Side");
		map.put("se_mf_horizontal",					"Horizontal");
		map.put("se_mf_base_line",					"Draw Base Line");
		map.put("se_mf_use_thousandth",				"Use Thousandth");
		map.put("se_mf_offset",						"Offset");
		map.put("se_mf_flip",						"Flip Funnel");
		
		
		// ELEMENT TYPES
		map.put("et_central_horzline",				"Central Horizontal Line");
		map.put("et_central_vert_line",				"Central Vertical Line");
		map.put("et_rangefinder",					"Rangefinder");
		map.put("et_horz_range_indicators",			"Horzizontal Range Indicators");
		map.put("et_ballistic_range_indicators",	"Ballistic Range Indicators");
		map.put("et_shell_ballistic_block",			"Shell Ballistics Block");
		map.put("et_custom_line",					"Custom Line");
		map.put("et_custom_text",					"Custom Text");
		map.put("et_custom_circle_outline",			"Custom Circle (Outline)");
		map.put("et_custom_circle_filled",			"Custom Circle (Filled)");
		map.put("et_custom_quad_outline",			"Custom Quad (Outline)");
		map.put("et_custom_quad_filled",			"Custom Quad (Filled)");
		map.put("et_custom_rect_filled",			"Custom Rectangle (Filled)");
		map.put("et_custom_rect_outline",			"Custom Rectangle (Outline)");
		map.put("et_custom_poly_filled",			"Custom Polygon (Filled)");
		map.put("et_custom_poly_outline",			"Custom Polygon (Outline)");
		map.put("et_funnel",						"Funnel");


		// LAUNCHER
//		map.put("lc_title",				"WT Sight Editor");
//		map.put("lc_skip_search",		"Skip");
//		map.put("lc_skip_update",		"Skip");
//		map.put("lc_skip_update",		"Update");
//		map.put("lc_update_found_msg",	"New version found: {new}. Current version: {current}");
//		map.put("lc_update_not_found_msg","Already up-to-date. ({current})");
//		map.put("lc_updating_msg",		"Updating to version {new} ...");

		
		
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
