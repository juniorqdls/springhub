package com.mjeanroy.springhub.models.entities.identity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import java.io.Serializable;

import com.mjeanroy.springhub.models.AbstractModel;
import com.mjeanroy.springhub.models.Model;
import com.mjeanroy.springhub.models.entities.JPAEntity;

/**
 * Abstract implementation of jpa entity using an id primary.
 * Primary key used {@link javax.persistence.GenerationType#IDENTITY} generated value.
 *
 * @param <PK> Generic type of id.
 */
@MappedSuperclass
public abstract class AbstractEntity<PK extends Serializable> extends AbstractModel<PK> implements Model<PK>, JPAEntity<PK> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	protected PK id;

	@Override
	public PK getId() {
		return id;
	}
}
