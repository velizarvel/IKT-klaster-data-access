package com.iktpreobuka.dataaccess.services;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Service;

import com.iktpreobuka.dataaccess.entities.AddressEntity;

@Service
public class AddressDAOImpl implements AddressDAO{
	
	@PersistenceContext
	private EntityManager em;

	@Override
	public List<AddressEntity> findAddressByUsername(String name) {
		String sql = "SELECT a FROM AddressEntity a LEFT JOIN FETCH a.users u WHERE u.name=:name";
		Query query = em.createQuery(sql);
		query.setParameter("name", name);
		
		List<AddressEntity> retVals = query.getResultList();
		return retVals;
	}

}
