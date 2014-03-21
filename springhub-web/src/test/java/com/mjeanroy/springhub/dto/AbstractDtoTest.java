package com.mjeanroy.springhub.dto;

import static com.google.common.collect.Sets.newHashSet;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.entry;

import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.mjeanroy.springhub.models.AbstractModel;

public class AbstractDtoTest {

	@Test
	public void isNew_should_return_false_if_bean_has_id() {
		// GIVEN
		FooDto fooDto = new FooDto();
		fooDto.setId(1L);

		// WHEN
		boolean isNew = fooDto.isNew();

		// THEN
		assertThat(isNew).isFalse();
	}

	@Test
	public void isNew_should_return_true_if_bean_has_id_equal_to_zero() {
		// GIVEN
		FooDto fooDto = new FooDto();
		fooDto.setId(0L);

		// WHEN
		boolean isNew = fooDto.isNew();

		// THEN
		assertThat(isNew).isTrue();
	}

	@Test
	public void isNew_should_return_false_if_bean_has_id_equal_to_null() {
		// GIVEN
		FooDto fooDto = new FooDto();
		fooDto.setId(null);

		// WHEN
		boolean isNew = fooDto.isNew();

		// THEN
		assertThat(isNew).isTrue();
	}

	@Test
	public void toMap_should_return_bean_map() {
		// GIVEN
		long id = 1L;
		String name = "foo";

		FooDto fooDto = new FooDto();
		fooDto.setId(id);
		fooDto.setName(name);

		// WHEN
		Map<String, Object> map = fooDto.toMap();

		// THEN
		assertThat(map).isNotNull().isNotEmpty().hasSize(3).contains(
				entry("id", id),
				entry("name", name),
				entry("new", false)
		);
	}

	@Test
	public void toMap_should_return_bean_map_and_exclude_some_properties() {
		// GIVEN
		long id = 1L;
		String name = "foo";

		FooDto fooDto = new FooDto();
		fooDto.setId(id);
		fooDto.setName(name);

		Set<String> excludes = newHashSet("new");

		// WHEN
		Map<String, Object> map = fooDto.toMapExcluding(excludes);

		// THEN
		assertThat(map).isNotNull().isNotEmpty().hasSize(2).contains(
				entry("id", id),
				entry("name", name)
		);
	}

	public static class FooModel extends AbstractModel {

		@Override
		public Long modelId() {
			return 1L;
		}
	}

	public static class FooDto extends AbstractDto<FooModel> {
		private String name;

		public FooDto() {
		}

		public FooDto(long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}
