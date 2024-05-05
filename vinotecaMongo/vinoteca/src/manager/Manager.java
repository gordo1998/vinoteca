package manager;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import model.Bodega;
import model.Campo;
import model.Entrada;
import model.Vid;
import utils.TipoVid;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class Manager {
	private static Manager manager;
	private ArrayList<Entrada> entradas;
	private Session session;
	private Transaction tx;
	private Bodega b;
	private Campo c;
	//atributos de mongo db
	private static Manager managerMongo;
	private MongoClientURI mongoClientURI;
	MongoClient mongoClient;
	private MongoCollection<Document> collection;
	private MongoDatabase mongoDatabase;
	private List<Document> lDocuments;
	private int numVids;
	
	private Manager () {
		this.entradas = new ArrayList<>();
	}
	
	public static Manager getInstance() {
		if (manager == null) {
			manager = new Manager();
		}
		return manager;
	}
	
	private void createSession() {
		/*
		Configuration configuration = new Configuration().configure("hibernate.cfg.xml");
		org.hibernate.SessionFactory sessionFactory = configuration.buildSessionFactory();
    	session = sessionFactory.openSession();
    	*/
    	String uri = "mongodb://localhost:27017"; //Esta será la Url de la base de datos
        mongoClientURI = new MongoClientURI(uri); // 
        mongoClient = new MongoClient(mongoClientURI);
        mongoDatabase = mongoClient.getDatabase("Vinoteca");
        
	}

	public void init() {
		createSession();
		System.out.print(getInputData()); 
		manageActions();
		//showAllCampos();
		session.close();
		mongoClient.close();
	}

	private void manageActions() {
		for (Entrada entrada : this.entradas) {
			try {
				System.out.println(entrada.getInstruccion());
				switch (entrada.getInstruccion().toUpperCase().split(" ")[0]) {
					case "B":
						addBodega(new Bodega(entrada.getInstruccion().split(" ")[1]));
						break;
					case "C":
						addCampo();
						break;
					case "V":
						addVid(new Vid(entrada.getInstruccion().split(" ")[1], entrada.getInstruccion().split(" ")[2]));
						break;
					case "#":
						vendimia();
						break;
					default:
						System.out.println("Instruccion incorrecta");
				}
			} catch (HibernateException e) {
				e.printStackTrace();
			}
		}
	}

	private void vendimia() {
		
		collection = mongoDatabase.getCollection("Campo");
		
		collection.updateMany(eq("Vendimia", false), set("Vendimia", true));
		
		/*
		this.b.getVids().addAll(this.c.getVids()); //Buelca todo los vids de campo a Bodega. 
		
		tx = session.beginTransaction();
		session.save(b);
		
		tx.commit();
		
		try {
            tx = session.beginTransaction();
            List<Campo> c = getCampos();

            for (Campo campo : c) {
                Bodega b = campo.getBodega(); 
                if (b != null) {
                    List<Vid> vidsDelCampo = campo.getVids();
                    for (Vid vid : vidsDelCampo) {
                        b.getVids().add(vid);
                        vid.setBodega(b);
                        session.update(vid);
                    }
                }
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
        
        */
		
		
	}
	
	
    private List<Campo> getCampos() {
        return session.createQuery("FROM Campo").list();
    }

	private void addVid(Vid vid) {
		/*
		Vid v = new Vid(TipoVid.valueOf(split[1].toUpperCase()), Integer.parseInt(split[2]));
		tx = session.beginTransaction();
		session.save(v);
		
		c.addVid(v);
		session.save(c);
		
		tx.commit();
		*/
		
//		collection = mongoDatabase.getCollection("Bodega");
	//	Document lastBodega = collection.find().sort(new Document("_id", -1)).first();
		
		collection = mongoDatabase.getCollection("Campo");
		Document lastCampo = collection.find().sort(new Document("_id", -1)).first();
		
		collection = mongoDatabase.getCollection("Vid");
		Document document = new Document().append("Tipo", vid.getVid()).append("Cantidad", vid.getCantidad()).
				append("Campo", lastCampo);
		collection.insertOne(document);
				
		
	}

	private void addCampo() {
		/*
		c = new Campo(b);
		tx = session.beginTransaction();
		
		int id = (Integer) session.save(c);
		c = session.get(Campo.class, id);
		
		tx.commit();
		*/
		collection = mongoDatabase.getCollection("Bodega");//llama a Bodegas
		Document lastBodega = collection.find().sort(new Document("_id", -1)).first();//Encuentra el documento de un colección.
		collection = mongoDatabase.getCollection("Campo");
		Document document = new Document().append("Vendimia", false).append("Bodega", lastBodega);
		collection.insertOne(document);
		
	}

	private void addBodega(Bodega bodega) {
		/*
		b = new Bodega(split[1]);
		tx = session.beginTransaction();
		
		int id = (Integer) session.save(b);
		b = session.get(Bodega.class, id);
		
		tx.commit();
		*/
		collection = mongoDatabase.getCollection("Bodega");
		Document document = new Document().append("name", bodega.getName());
		collection.insertOne(document);
		
	}
	
	private void getEntrada() {
		//tx = session.beginTransaction();
		//Query q = session.createQuery("select e from Entrada e");
		//this.entradas.addAll(q.list());
		//tx.commit();
		
		
        
	}
	
	public ArrayList<Entrada> getInputData(){
		collection = mongoDatabase.getCollection("Entrada"); //Aqui coge la entrada
		
		for(Document document : collection.find()) {
			Entrada input = new Entrada();
			input.setInstruccion(document.getString("instruccion"));
			entradas.add(input);
		}
		return entradas;
	}

	private void showAllCampos() {
		tx = session.beginTransaction();
		Query q = session.createQuery("select c from Campo c");
		List<Campo> list = q.list();
		for (Campo c : list) {
			System.out.println(c);
		}
		tx.commit();
	}

	
}
