package com.ruegnerlukas.githubApi;

import java.util.ArrayList;
import java.util.List;

public class Release {
	public String url;
	public String html_url;
	public String assets_url;
	public String upload_url;
	public String tarball_url;
	public String zipball_url;
	public int id;
	public String node_id;
	public String tag_name;
	public String target_commitish;
	public String name;
	public String body;
	public boolean draft;
	public boolean prerelease;
	public String created_at;
	public String published_at;
	public Author author;
	public List<Asset> assets = new ArrayList<Asset>();
}

