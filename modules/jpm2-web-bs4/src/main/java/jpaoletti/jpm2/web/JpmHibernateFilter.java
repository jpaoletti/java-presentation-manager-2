package jpaoletti.jpm2.web;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.FlushMode;
import org.hibernate.SessionFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 *
 * @author jpaoletti
 */
public class JpmHibernateFilter extends OncePerRequestFilter {

    public static final String DEFAULT_SESSION_FACTORY_BEAN_NAME = "sessionFactory";
    private String sessionFactoryBeanName = DEFAULT_SESSION_FACTORY_BEAN_NAME;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            final SessionFactory sessionFactory = lookupSessionFactory();
            if (sessionFactory != null) {
                sessionFactory.getCurrentSession().setFlushMode(FlushMode.COMMIT);
            }
        } catch (Exception e) {
            //Just in case
        }
        filterChain.doFilter(request, response);
    }

    protected SessionFactory lookupSessionFactory() {
        final WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
        return wac.getBean(getSessionFactoryBeanName(), SessionFactory.class);
    }

    public void setSessionFactoryBeanName(String sessionFactoryBeanName) {
        this.sessionFactoryBeanName = sessionFactoryBeanName;
    }

    protected String getSessionFactoryBeanName() {
        return this.sessionFactoryBeanName;
    }
}
