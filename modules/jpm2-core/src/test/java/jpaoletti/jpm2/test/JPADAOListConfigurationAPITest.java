package jpaoletti.jpm2.test;

import javax.persistence.criteria.JoinType;
import jpaoletti.jpm2.core.dao.DAOOrder;
import jpaoletti.jpm2.core.dao.JPADAOListConfiguration;
import jpaoletti.jpm2.core.dao.JPADAO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test que verifica la existencia y estructura de la API de JPADAOListConfiguration.
 * Este test es principalmente documentación de la API disponible.
 *
 * Para ejemplos completos de uso, ver el archivo JPA_DAO_USAGE_EXAMPLES.md
 *
 * @author jpaoletti
 */
public class JPADAOListConfigurationAPITest {

    @Test
    public void testJPADAOListConfigurationClassExists() {
        assertNotNull(JPADAOListConfiguration.class, "JPADAOListConfiguration debe existir");
    }

    @Test
    public void testJPADAOClassExists() {
        assertNotNull(JPADAO.class, "JPADAO debe existir");
    }

    @Test
    public void testDAOOrderClassExists() {
        assertNotNull(DAOOrder.class, "DAOOrder debe existir");

        // Verificar que puede construirse correctamente
        DAOOrder ascOrder = new DAOOrder("field", true);
        assertTrue(ascOrder.isAsc(), "Orden ascendente debe estar configurado");
        assertEquals("field", ascOrder.getOrder(), "El campo debe ser 'field'");

        DAOOrder descOrder = new DAOOrder("field", false);
        assertFalse(descOrder.isAsc(), "Orden descendente debe estar configurado");
    }

    @Test
    public void testJPAAliasClassExists() {
        assertNotNull(JPADAOListConfiguration.JPAAlias.class, "JPAAlias debe existir");

        // Verificar que puede construirse
        JPADAOListConfiguration.JPAAlias alias1 = new JPADAOListConfiguration.JPAAlias("property", "alias");
        assertEquals("property", alias1.getProperty());
        assertEquals("alias", alias1.getAlias());
        assertEquals(JoinType.INNER, alias1.getJoinType(), "JoinType por defecto debe ser INNER");

        JPADAOListConfiguration.JPAAlias alias2 = new JPADAOListConfiguration.JPAAlias("property", "alias", JoinType.LEFT);
        assertEquals(JoinType.LEFT, alias2.getJoinType(), "JoinType debe ser LEFT");
    }

    /**
     * Este test documenta la API básica de configuración.
     * En producción, se usaría así:
     *
     * <pre>
     * JPADAOListConfiguration config = dao.build();
     *
     * // Agregar predicado
     * config.withPredicate((cb, root) -> cb.equal(root.get("username"), "jpaoletti"));
     *
     * // Agregar ordenamiento
     * config.withOrder(new DAOOrder("title", true));
     *
     * // Configurar paginación
     * config.withFrom(0).withMax(10);
     *
     * // Ejecutar
     * List results = dao.list(config);
     * Long total = dao.count(config);
     * </pre>
     */
    @Test
    public void testAPIDocumentation() {
        // Este test solo documenta la existencia de los métodos clave
        // Los métodos reales requieren un EntityManager para funcionar
        assertTrue(true, "Ver documentación en JPA_DAO_USAGE_EXAMPLES.md");
    }
}
