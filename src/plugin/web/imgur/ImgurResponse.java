package plugin.web.imgur;

import java.util.List;

/*
 * This holds data returned from an Imgur request.
 */
public class ImgurResponse {

	private DataSet data;

	static class DataSet {

		String title;
		String type;
		int views;
		int ups;
		int downs;
		boolean nsfw;

		public DataSet() {
		}

	}

	public ImgurResponse() {
	}

	public String getTitle() {
		return data.title;
	}

	public String getType() {
		return data.type;
	}

	public int getViews() {
		return data.views;
	}

	public int getLikes() {
		return data.ups;
	}

	public int getDislikes() {
		return data.downs;
	}

	public boolean isNsfw() {
		return data.nsfw;
	}

}
