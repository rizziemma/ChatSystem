package src;

import java.util.List;

import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public class HistoriqueDAO {


   private Session session;

   public HistoriqueDAO()  {
     Configuration configuration = new Configuration().configure("resources/hibernate.cfg.xml");
     ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
     SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
     session = sessionFactory.openSession();
   }


   public void updateHistorique(Historique h){
     session.beginTransaction();
     session.merge(h);
     session.getTransaction().commit();
   }

   @SuppressWarnings("unchecked")
public List<Historique> getHistoriques(){
     Query query = session.createQuery("from HISTORIQUE");
     return (List<Historique>) query.list();
   }

   public Historique getHistoriqueByUtilisateur(Utilisateur u){
     Query query = session.createQuery("from HISTORIQUE H where  H.contact = " + u);
     return (Historique) query.list().get(0);
   }
}