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
		EntityManager entityManager = emf.createEntityManager();
//		entityManager.getTransaction().begin();
		Note retNote;
		try
		{
			retNote = (Note)entityManager.createQuery("SELECT n FROM Note n where n.id = :id")
					.setParameter("id", id)
					.getSingleResult();
		} catch(NoResultException e)
		{
			retNote = null;
		} finally {
//			entityManager.getTransaction().commit();
			entityManager.close();
		}
		return retNote;
	}

	@SuppressWarnings("unchecked")
	public List<Note> getAllNotes()
	{
		EntityManager entityManager = emf.createEntityManager();
//		entityManager.getTransaction().begin();
		List<Note> allNotes;
		try {
			allNotes = entityManager.createQuery("SELECT n FROM Note n")
					.getResultList();
		} catch(NoResultException e) {
			allNotes = new ArrayList<Note>();
		} finally {
//			entityManager.getTransaction().commit();
			entityManager.close();
		}
		return allNotes;
	}
	
	@SuppressWarnings("unchecked")
	public List<Note> getAllNotes(String searchParm)
	{
		List<Note> allNotes = null;
		EntityManager entityManager = emf.createEntityManager();
//		entityManager.getTransaction().begin();
		try {
			allNotes = entityManager.createQuery("SELECT n FROM Note n WHERE n.body like :word")
					.setParameter("word",  "%" + searchParm + "%")
					.getResultList();
		} catch(NoResultException e) {
			allNotes = new ArrayList<Note>();
		} finally {
//			entityManager.getTransaction().commit();
			entityManager.close();
		}
		return allNotes;
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
	
	public boolean updateNote(Note n)
	{
		boolean ok = false;

		EntityManager entityManager = emf.createEntityManager();
		entityManager.getTransaction().begin();

		try {
			Note updNote = (Note)entityManager.createQuery("SELECT n FROM Note n where n.id = :id")
					.setParameter("id", n.id)
					.getSingleResult();
			updNote.body = n.body;
			entityManager.merge(updNote);
			entityManager.getTransaction().commit();
			ok = true;
		} catch(NoResultException e) {
			entityManager.getTransaction().rollback();
			ok = false;
		} finally {
			entityManager.close();
		}
		
		return ok;
	}
	
	public boolean deleteNote(int id)
	{
		boolean ok = false;
		
		EntityManager entityManager = emf.createEntityManager();
		entityManager.getTransaction().begin();
		
		try { // NoResultException
			Note delNote = (Note)entityManager.createQuery("SELECT n FROM Note n where n.id = :id")
					.setParameter("id", id)
					.getSingleResult();
			entityManager.remove(delNote);
			entityManager.getTransaction().commit();
			ok = true;
		} catch(NoResultException e) {
			entityManager.getTransaction().rollback();
			ok = false;
		} finally {
			entityManager.close();
		}
		return ok;
	}
	
	public boolean deleteAllNotes()
	{
		EntityManager entityManager = emf.createEntityManager();
		entityManager.getTransaction().begin();
		entityManager.createQuery("DELETE FROM Note").executeUpdate();
		entityManager.getTransaction().commit();
		entityManager.close();
		return true;
	}
}
