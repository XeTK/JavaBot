package plugin.web.imgur;

/*
 * This holds data returned from an Imgur request.
 */
public class ImgurResponse {

	private DataSet data;

	static class DataSet {

		String title;
		String type;
		String error;
		String description;
		int views;
		int ups;
		int downs;
		int height;
		int width;
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
	public String getError(){
		return data.error;
	}
	public String getDesc() {
		return data.description;
	}
	public String getRes() {
		return "[" + data.width + "x" + data.height + "]";
	}
}
