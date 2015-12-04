package hello.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "oauth_access_uri")
public class OAuthURIAccess {
	@Id
	private int id;
	@Column(name="uri")
	private String uri;
	@Column(name = "role")
	private String role;
	@Column(name = "secured")
	private String secured;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "OAuthURIAccess [id=" + id + ", uri=" + uri + ", role=" + role + ", secured=" + secured + "]";
	}

	public String getSecured() {
		return secured;
	}

	public void setSecured(String secured) {
		this.secured = secured;
	}

}
