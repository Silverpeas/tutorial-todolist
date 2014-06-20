package org.silverpeas.components.todolist.mock;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.metamodel.Metamodel;
import java.util.Map;

/**
 * @author mmoquillon
 */
@Named("entityManagerFactory")
public class EntityManagerFactory implements javax.persistence.EntityManagerFactory {

  @Inject
  private EntityManager entityManager;

  @Override
  public EntityManager createEntityManager() {
    return entityManager;
  }

  @Override
  public EntityManager createEntityManager(final Map map) {
    return entityManager;
  }

  @Override
  public CriteriaBuilder getCriteriaBuilder() {
    return null;
  }

  @Override
  public Metamodel getMetamodel() {
    return null;
  }

  @Override
  public boolean isOpen() {
    return false;
  }

  @Override
  public void close() {

  }

  @Override
  public Map<String, Object> getProperties() {
    return null;
  }

  @Override
  public Cache getCache() {
    return null;
  }

  @Override
  public PersistenceUnitUtil getPersistenceUnitUtil() {
    return null;
  }
}
