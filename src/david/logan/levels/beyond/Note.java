package david.logan.levels.beyond;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = false)
@Table(name="notes")
@Entity
public class Note {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Integer id;

	@NotNull
	public String body;

	public Note(String body) { id = null; this.body = body; }
	public Note() { id = null; body = null; }
	public Note(int id, String body)  { this.id = new Integer(id); this.body = body; }
}
