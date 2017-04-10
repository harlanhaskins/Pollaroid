import com.bipoller.BiPollerApplication;
import io.dropwizard.setup.Environment;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

public class BiPollerApplicationTest extends TestCase {

    protected BiPollerApplication dbManager;

    @Override
    public void setUp() {

        // Create instance of BiPoller Application
        dbManager = new BiPollerApplication();

        // Attempt to make a connection to database
        dbManager.createConnection("","Luke Shadler", "test1234");
        Assert.assertNotNull(dbManager.getConnection());

        // Run Database
        //dbManager.run(dbManager.getConnection(),new Environment());
        //Assert. ...


        // Create sample tuples to add to tables
    }

    @Override
    public void tearDown() {

        // Attempt to close connection
        dbManager.closeConnection();
        Assert.assertNull(dbManager.getConnection());

    }

    @Test
    /**
     *  Attempts to add new tuples to tables
     */
    public void insertIntoTables() {}

    @Test
    /**
     *  Clears Table of All Elements
     */
    public void clearTable() {}

    @Test
    /**
     *  Tests user authorization
     */
    public void testAuthorization() {}


}
