package com.iktpreobuka.dataaccess.services;

import java.util.List;

import com.iktpreobuka.dataaccess.entities.AddressEntity;

public interface AddressDAO {

	public List<AddressEntity> findAddressByUsername(String name);
}
