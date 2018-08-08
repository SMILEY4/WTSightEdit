package com.ruegnerlukas.wtlauncher.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ruegnerlukas.simpleutils.logging.logger.Logger;
import com.ruegnerlukas.wtlauncher.githubApi.APIRelease;
import com.ruegnerlukas.wtlauncher.githubApi.Asset;
import com.ruegnerlukas.wtlauncher.githubApi.GitHubAPI;
import com.ruegnerlukas.wtlauncher.githubApi.Release;

public class LocalNetworkInterface implements NetworkInterface {

	
	
	
	@Override
	public boolean checkNetworkStatus() {
		return true;
	}

	
	
	
	@Override
	public Release getLatestRelease(String author, String repository) {
		try {
			String strJson = "{\"url\":\"https://api.github.com/repos/SMILEY4/WTSightEdit/releases/12153076\",\"assets_url\":\"https://api.github.com/repos/SMILEY4/WTSightEdit/releases/12153076/assets\",\"upload_url\":\"https://uploads.github.com/repos/SMILEY4/WTSightEdit/releases/12153076/assets{?name,label}\",\"html_url\":\"https://github.com/SMILEY4/WTSightEdit/releases/tag/0.7.1\",\"id\":12153076,\"node_id\":\"MDc6UmVsZWFzZTEyMTUzMDc2\",\"tag_name\":\"0.7.1\",\"target_commitish\":\"master\",\"name\":\"WTSightEdit (v0.7.1)\",\"draft\":false,\"author\":{\"login\":\"SMILEY4\",\"id\":13238118,\"node_id\":\"MDQ6VXNlcjEzMjM4MTE4\",\"avatar_url\":\"https://avatars3.githubusercontent.com/u/13238118?v=4\",\"gravatar_id\":\"\",\"url\":\"https://api.github.com/users/SMILEY4\",\"html_url\":\"https://github.com/SMILEY4\",\"followers_url\":\"https://api.github.com/users/SMILEY4/followers\",\"following_url\":\"https://api.github.com/users/SMILEY4/following{/other_user}\",\"gists_url\":\"https://api.github.com/users/SMILEY4/gists{/gist_id}\",\"starred_url\":\"https://api.github.com/users/SMILEY4/starred{/owner}{/repo}\",\"subscriptions_url\":\"https://api.github.com/users/SMILEY4/subscriptions\",\"organizations_url\":\"https://api.github.com/users/SMILEY4/orgs\",\"repos_url\":\"https://api.github.com/users/SMILEY4/repos\",\"events_url\":\"https://api.github.com/users/SMILEY4/events{/privacy}\",\"received_events_url\":\"https://api.github.com/users/SMILEY4/received_events\",\"type\":\"User\",\"site_admin\":false},\"prerelease\":false,\"created_at\":\"2018-07-29T13:03:27Z\",\"published_at\":\"2018-07-29T12:45:33Z\",\"assets\":[{\"url\":\"https://api.github.com/repos/SMILEY4/WTSightEdit/releases/assets/8050322\",\"id\":8050322,\"node_id\":\"MDEyOlJlbGVhc2VBc3NldDgwNTAzMjI=\",\"name\":\"WTSightEdit.zip\",\"label\":null,\"uploader\":{\"login\":\"SMILEY4\",\"id\":13238118,\"node_id\":\"MDQ6VXNlcjEzMjM4MTE4\",\"avatar_url\":\"https://avatars3.githubusercontent.com/u/13238118?v=4\",\"gravatar_id\":\"\",\"url\":\"https://api.github.com/users/SMILEY4\",\"html_url\":\"https://github.com/SMILEY4\",\"followers_url\":\"https://api.github.com/users/SMILEY4/followers\",\"following_url\":\"https://api.github.com/users/SMILEY4/following{/other_user}\",\"gists_url\":\"https://api.github.com/users/SMILEY4/gists{/gist_id}\",\"starred_url\":\"https://api.github.com/users/SMILEY4/starred{/owner}{/repo}\",\"subscriptions_url\":\"https://api.github.com/users/SMILEY4/subscriptions\",\"organizations_url\":\"https://api.github.com/users/SMILEY4/orgs\",\"repos_url\":\"https://api.github.com/users/SMILEY4/repos\",\"events_url\":\"https://api.github.com/users/SMILEY4/events{/privacy}\",\"received_events_url\":\"https://api.github.com/users/SMILEY4/received_events\",\"type\":\"User\",\"site_admin\":false},\"content_type\":\"application/x-zip-compressed\",\"state\":\"uploaded\",\"size\":8823852,\"download_count\":15,\"created_at\":\"2018-07-29T12:45:12Z\",\"updated_at\":\"2018-07-29T12:45:21Z\",\"browser_download_url\":\"https://github.com/SMILEY4/WTSightEdit/releases/download/0.7.1/WTSightEdit.zip\"}],\"tarball_url\":\"https://api.github.com/repos/SMILEY4/WTSightEdit/tarball/0.7.1\",\"zipball_url\":\"https://api.github.com/repos/SMILEY4/WTSightEdit/zipball/0.7.1\",\"body\":\"- improved log messages\\r\\n- fixed bug: the program crashed when loading files containing unexpected symbols\\r\\n- fixed bug: \\\"crosshairDistHorSizeMain\\\" does not hide indicators in editor\\r\\n- fixed bug: stepsize and range of number-input-fields corrected\\r\\n- fixed bug: selecting \\\"Ballistic Range Indicators\\\" does not highlight elements\\r\\n- stability improved\"}";
			JsonParser parser = new JsonParser();
			JsonObject jsonRelease = parser.parse(strJson).getAsJsonObject();
			Release release = APIRelease.jsonToRelease(jsonRelease);
			return release;

		} catch (Exception e) {
			Logger.get().error(e);
		}
		
		return null;
	}




	@Override
	public void download(File fileUpdateDir, Release release) throws IOException {

		Asset asset = release.assets.get(0);
		fileUpdateDir.mkdirs();
		
		ZipInputStream zipIn = new ZipInputStream(new FileInputStream(new File("C:\\Users\\LukasRuegner\\Desktop\\WTSightEdit_071.zip")));

		ZipEntry entry = zipIn.getNextEntry();
		byte[] buffer = new byte[1024];

		while (entry != null) {

			File outFile = new File(fileUpdateDir.getAbsolutePath() + File.separator + entry.getName());

			if (!entry.isDirectory()) {

				new File(outFile.getParent()).mkdirs();
				FileOutputStream out = new FileOutputStream(outFile);
				int len;
				while ((len = zipIn.read(buffer)) > 0) {
					out.write(buffer, 0, len);
				}
				out.close();

			} else {
				outFile.mkdirs();
			}

			zipIn.closeEntry();
			entry = zipIn.getNextEntry();

		}

		zipIn.close();
		
	}

	
	
	
	

}
