package david.logan.levels.beyond;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;

//import org.hibernate.Hibernate;

//
//	The standard DAO layer. Nothing special here really.
//	You can tell that the code regards a database bStatus=Status.INACTIVE as deleted.
//	There is currently no way for the DAO layer to ever return an INACTIVE record.
//
//	The code does make sure that there is only one ACTIVE record with a particular username.
//	In the original MySQL sql statements to build the database, I also added insert and update
//	triggers to ensure it as well, so if you are using the triggers, it's just an extra
//	check to ensure we never have a duplicate ACTIVE username. We can have as many duplicate
//	INACTIVE usernames as we wish.
//

public class LBDao {
	@PersistenceContext

	private EntityManagerFactory emf = Persistence.createEntityManagerFactory("h2");
	
	private static LBDao dao = null;
	public static LBDao getDAO() {
		if(dao != null) return dao;
		return (dao = new LBDao());
	}
	
	private LBDao() {}
	
	public Note getNote(int id)
	{
		try
		{
			EntityManager entityManager = emf.createEntityManager();
			entityManager.getTransaction().begin();
			Note retNote = (Note)entityManager.createQuery("SELECT n FROM Note n where n.id = :id")
					.setParameter("id", id)
					.getSingleResult();
			entityManager.close();
			return retNote;
		} catch(NoResultException e)
		{
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Note> getAllNotes()
	{
		try {
			EntityManager entityManager = emf.createEntityManager();
			entityManager.getTransaction().begin();
			List<Note> allNotes = entityManager.createQuery("SELECT n FROM Note n")
					.getResultList();
			entityManager.close();
			return allNotes;
		} catch(NoResultException e) {
			return new ArrayList<Note>();
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Note> getAllNotes(String searchParm)
	{
		try {
			EntityManager entityManager = emf.createEntityManager();
			entityManager.getTransaction().begin();
			List<Note> allNotes = entityManager.createQuery("SELECT n FROM Note n WHERE n.body like :word")
					.setParameter("word",  "%" + searchParm + "%")
					.getResultList();
			entityManager.close();
			return allNotes;
		} catch(NoResultException e) {
			return new ArrayList<Note>();
		}
	}
	
	public int addNote(Note n)
	{
		Note newNote = new Note(n.body);
		EntityManager entityManager = emf.createEntityManager();
		entityManager.getTransaction().begin();
		entityManager.persist(newNote);
		entityManager.getTransaction().commit();
		entityManager.close();
		return newNote.id;
	}
	
	public void updateNote(Note n)
	{
		EntityManager entityManager = emf.createEntityManager();
		entityManager.getTransaction().begin();
		Note updNote = (Note)entityManager.createQuery("SELECT n FROM Note n where n.id = :id")
				.setParameter("id", n.id)
				.getSingleResult();
		updNote.body = n.body;
		entityManager.merge(updNote);
		entityManager.getTransaction().commit();
		entityManager.close();
	}
	
	public void deleteNote(int id)
	{
		EntityManager entityManager = emf.createEntityManager();
		entityManager.getTransaction().begin();
		Note delNote = (Note)entityManager.createQuery("SELECT n FROM Note n where n.id = :id")
				.setParameter("id", id)
				.getSingleResult();
		entityManager.remove(delNote);
		entityManager.getTransaction().commit();
		entityManager.close();
	}
	
	public void deleteAllNotes()
	{
		EntityManager entityManager = emf.createEntityManager();
		entityManager.getTransaction().begin();
		entityManager.createQuery("DELETE FROM Note").executeUpdate();
		entityManager.getTransaction().commit();
		entityManager.close();
	}
}
