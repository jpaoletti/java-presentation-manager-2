package jpaoletti.jpm2.test;

import java.util.ArrayList;
import java.util.List;
import jpaoletti.jpm2.core.PresentationManager;
import jpaoletti.jpm2.core.dao.AuditDAO;
import jpaoletti.jpm2.core.exception.EntityNotFoundException;
import jpaoletti.jpm2.core.model.AuditRecord;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.service.AuditService;
import org.hibernate.criterion.Order;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.mockito.Mockito.*;

/**
 *
 * @author jpaoletti
 */
@ContextConfiguration(locations = {"classpath:/application-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class AuditServiceTest {

    @Mock
    private AuditDAO dao;
    @InjectMocks
    @Autowired
    private AuditService service;
    @Autowired
    private PresentationManager jpm;

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void test() throws EntityNotFoundException {
        final Entity entity = getJpm().getEntity("jpm-entity-test");
        final List<AuditRecord> list = new ArrayList<>();
        list.add(new AuditRecord(1L, "user", entity.getId(), "show", "1", "Test"));
        list.add(new AuditRecord(2L, "user", entity.getId(), "add", null, "Test2"));
        list.add(new AuditRecord(3L, "user", "other", "add", null, "Test3"));
        when(getDao().list(any(Order.class))).thenReturn(list);
        Assert.assertNotNull(service);
        //Assert.assertEquals(1, getService().getGeneralRecords(entity).size());
        //Assert.assertEquals(1, getService().getItemRecords(entity, "1").size());
        //Assert.assertEquals(0, getService().getItemRecords(entity, "0").size());
    }

    public AuditDAO getDao() {
        return dao;
    }

    public void setDao(AuditDAO dao) {
        this.dao = dao;
    }

    public AuditService getService() {
        return service;
    }

    public void setService(AuditService service) {
        this.service = service;
    }

    public PresentationManager getJpm() {
        return jpm;
    }

    public void setJpm(PresentationManager jpm) {
        this.jpm = jpm;
    }
}