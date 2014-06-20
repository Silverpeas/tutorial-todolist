package org.silverpeas.components.todolist.mock;

import javax.inject.Named;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.Metamodel;
import java.util.Map;

/**
 * @author mmoquillon
 */
@Named("entityManager")
public class EntityManager implements javax.persistence.EntityManager {
  @Override
  public void persist(final Object o) {

  }

  @Override
  public <T> T merge(final T t) {
    return null;
  }

  @Override
  public void remove(final Object o) {

  }

  @Override
  public <T> T find(final Class<T> tClass, final Object o) {
    return null;
  }

  @Override
  public <T> T find(final Class<T> tClass, final Object o,
      final Map<String, Object> stringObjectMap) {
    return null;
  }

  @Override
  public <T> T find(final Class<T> tClass, final Object o, final LockModeType lockModeType) {
    return null;
  }

  @Override
  public <T> T find(final Class<T> tClass, final Object o, final LockModeType lockModeType,
      final Map<String, Object> stringObjectMap) {
    return null;
  }

  @Override
  public <T> T getReference(final Class<T> tClass, final Object o) {
    return null;
  }

  @Override
  public void flush() {

  }

  @Override
  public void setFlushMode(final FlushModeType flushModeType) {

  }

  @Override
  public FlushModeType getFlushMode() {
    return null;
  }

  @Override
  public void lock(final Object o, final LockModeType lockModeType) {

  }

  @Override
  public void lock(final Object o, final LockModeType lockModeType,
      final Map<String, Object> stringObjectMap) {

  }

  @Override
  public void refresh(final Object o) {

  }

  @Override
  public void refresh(final Object o, final Map<String, Object> stringObjectMap) {

  }

  @Override
  public void refresh(final Object o, final LockModeType lockModeType) {

  }

  @Override
  public void refresh(final Object o, final LockModeType lockModeType,
      final Map<String, Object> stringObjectMap) {

  }

  @Override
  public void clear() {

  }

  @Override
  public void detach(final Object o) {

  }

  @Override
  public boolean contains(final Object o) {
    return false;
  }

  @Override
  public LockModeType getLockMode(final Object o) {
    return null;
  }

  @Override
  public void setProperty(final String s, final Object o) {

  }

  @Override
  public Map<String, Object> getProperties() {
    return null;
  }

  @Override
  public Query createQuery(final String s) {
    return null;
  }

  @Override
  public <T> TypedQuery<T> createQuery(final CriteriaQuery<T> tCriteriaQuery) {
    return null;
  }

  @Override
  public <T> TypedQuery<T> createQuery(final String s, final Class<T> tClass) {
    return null;
  }

  @Override
  public Query createNamedQuery(final String s) {
    return null;
  }

  @Override
  public <T> TypedQuery<T> createNamedQuery(final String s, final Class<T> tClass) {
    return null;
  }

  @Override
  public Query createNativeQuery(final String s) {
    return null;
  }

  @Override
  public Query createNativeQuery(final String s, final Class aClass) {
    return null;
  }

  @Override
  public Query createNativeQuery(final String s, final String s2) {
    return null;
  }

  @Override
  public void joinTransaction() {

  }

  @Override
  public <T> T unwrap(final Class<T> tClass) {
    return null;
  }

  @Override
  public Object getDelegate() {
    return null;
  }

  @Override
  public void close() {

  }

  @Override
  public boolean isOpen() {
    return false;
  }

  @Override
  public EntityTransaction getTransaction() {
    return null;
  }

  @Override
  public EntityManagerFactory getEntityManagerFactory() {
    return null;
  }

  @Override
  public CriteriaBuilder getCriteriaBuilder() {
    return null;
  }

  @Override
  public Metamodel getMetamodel() {
    return null;
  }
}
