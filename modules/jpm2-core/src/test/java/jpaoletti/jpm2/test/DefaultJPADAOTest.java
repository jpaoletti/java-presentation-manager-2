package jpaoletti.jpm2.test;

import jpaoletti.jpm2.core.dao.DefaultJPADAO;
import jpaoletti.jpm2.core.model.UserFavorite;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for DefaultJPADAO functionality
 *
 * @author jpaoletti
 */
public class DefaultJPADAOTest {

    private DefaultJPADAO dao;

    @BeforeEach
    public void setUp() {
        dao = new DefaultJPADAO();
    }

    @Test
    public void testSetAndGetClassName() {
        String className = "jpaoletti.jpm2.core.model.UserFavorite";
        dao.setClassName(className);

        assertEquals(className, dao.getClassName(), "ClassName debe ser el mismo");
    }

    @Test
    public void testGetPersistentClassWithValidClassName() {
        dao.setClassName("jpaoletti.jpm2.core.model.UserFavorite");

        Class<Object> persistentClass = dao.getPersistentClass();

        assertNotNull(persistentClass, "PersistentClass no debe ser null");
        assertEquals(UserFavorite.class, persistentClass, "Debe ser la clase UserFavorite");
    }

    @Test
    public void testGetPersistentClassWithInvalidClassName() {
        dao.setClassName("com.invalid.NonExistentClass");

        Class<Object> persistentClass = dao.getPersistentClass();

        assertNull(persistentClass, "PersistentClass debe ser null para clase inexistente");
    }

    @Test
    public void testGetPersistentClassWithNullClassName() {
        // No setear className
        Class<Object> persistentClass = dao.getPersistentClass();

        assertNull(persistentClass, "PersistentClass debe ser null cuando className es null");
    }

    @Test
    public void testGetIdFromObject() {
        UserFavorite favorite = new UserFavorite();
        favorite.setId(123L);

        Long id = dao.getId(favorite);

        assertNotNull(id, "ID no debe ser null");
        assertEquals(Long.valueOf(123L), id, "ID debe ser 123");
    }

    @Test
    public void testGetIdFromObjectWithNullId() {
        UserFavorite favorite = new UserFavorite();
        // No setear id, quedará null

        Long id = dao.getId(favorite);

        assertNull(id, "ID debe ser null cuando no está seteado");
    }

    @Test
    public void testGetIdFromObjectWithoutIdField() {
        // Objeto sin campo "id"
        String stringObject = "test";

        Long id = dao.getId(stringObject);

        // Debería retornar null por la excepción
        assertNull(id, "ID debe ser null para objeto sin campo id");
    }

    /**
     * Test que documenta el uso típico en Spring
     */
    @Test
    public void testSpringConfigurationExample() {
        // En Spring, típicamente se configura así:
        // <bean id="userDAO" class="jpaoletti.jpm2.core.dao.DefaultJPADAO">
        //     <property name="className" value="com.mycompany.model.User"/>
        // </bean>

        DefaultJPADAO userDAO = new DefaultJPADAO();
        userDAO.setClassName("jpaoletti.jpm2.core.model.UserFavorite");

        assertNotNull(userDAO.getPersistentClass());
        assertEquals("jpaoletti.jpm2.core.model.UserFavorite", userDAO.getClassName());

        // El DAO está listo para usar con:
        // - userDAO.list(config)
        // - userDAO.count(config)
        // - userDAO.get(id)
        // - userDAO.save(entity)
        // - userDAO.update(entity)
        // - userDAO.delete(entity)

        assertTrue(true, "Ver documentación en DEFAULTJPADAO_USAGE.md");
    }
}
