package com.mjeanroy.springhub.test.dbunit;

import static org.apache.commons.io.FileUtils.toFile;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.data.MapEntry.entry;
import static org.mockito.Mockito.mock;

import javax.sql.DataSource;
import java.io.File;
import java.net.URL;
import java.util.Iterator;

import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

public class DBUnitTest {

	private DataSource dataSource;

	@Before
	public void setUp() {
		dataSource = mock(DataSource.class);
	}

	@Test
	public void should_build_new_dbunit_instance() {
		// WHEN
		DBUnit dbUnit = new DBUnit(dataSource);

		// THEN
		assertThat(dbUnit.dataSource).isNotNull().isEqualTo(dataSource);
		assertThat(dbUnit.xmlDataSet).isNotNull().isEmpty();
		assertThat(dbUnit.replacements).isNotNull().isEmpty();
		assertThat(dbUnit.setUpOperation).isNotNull().isEqualTo(DatabaseOperation.CLEAN_INSERT);
		assertThat(dbUnit.tearDownOperation).isNotNull().isEqualTo(DatabaseOperation.DELETE_ALL);
	}

	@Test
	public void addXML_should_add_xml_from_classpath() {
		// GIVEN
		String path = "/dbunit/datasets/01-foo.xml";
		DBUnit dbUnit = new DBUnit(dataSource);

		// WHEN
		DBUnit result = dbUnit.addXML(path);

		// THEN
		assertThat(result).isSameAs(dbUnit);
		assertThat(dbUnit.xmlDataSet).isNotNull().hasSize(1);
		assertThat(dbUnit.xmlDataSet.iterator().next()).exists().canRead();
	}

	@Test
	public void addDirectory_should_add_xml_in_directory() {
		// GIVEN
		String path = "/dbunit/datasets";
		DBUnit dbUnit = new DBUnit(dataSource);

		// WHEN
		DBUnit result = dbUnit.addDirectory(path);

		// THEN
		assertThat(result).isSameAs(dbUnit);
		assertThat(dbUnit.xmlDataSet).isNotNull().hasSize(2);

		Iterator<File> iterator = dbUnit.xmlDataSet.iterator();
		assertThat(iterator.next()).exists().canRead();
		assertThat(iterator.next()).exists().canRead();
	}

	@Test
	public void addFile_should_add_single_file() {
		// GIVEN
		String path = "/dbunit/datasets/02-bar.xml";
		URL url = getClass().getResource(path);
		File file = toFile(url);
		DBUnit dbUnit = new DBUnit(dataSource);

		// WHEN
		DBUnit result = dbUnit.addFile(file);

		// THEN
		assertThat(result).isSameAs(dbUnit);
		assertThat(dbUnit.xmlDataSet).isNotNull().hasSize(1);

		Iterator<File> iterator = dbUnit.xmlDataSet.iterator();
		assertThat(iterator.next()).exists().canRead().isEqualTo(file);
	}

	@Test
	public void addFile_should_add_directory() {
		// GIVEN
		String path = "/dbunit/datasets";
		URL url = getClass().getResource(path);
		File file = toFile(url);
		DBUnit dbUnit = new DBUnit(dataSource);

		// WHEN
		DBUnit result = dbUnit.addFile(file);

		// THEN
		assertThat(result).isSameAs(dbUnit);
		assertThat(dbUnit.xmlDataSet).isNotNull().hasSize(2);
	}

	@Test
	public void addReplacement_should_add_replacement_value() {
		// GIVEN
		String key = "foo";
		String value = "bar";
		DBUnit dbUnit = new DBUnit(dataSource);

		// WHEN
		DBUnit result = dbUnit.addReplacement(key, value);

		// THEN
		assertThat(result).isSameAs(dbUnit);
		assertThat(dbUnit.replacements).isNotNull().hasSize(1).contains(
				entry(key, value)
		);
	}

	@Test
	public void setSetUpOperation_should_replace_set_up_operation() {
		// GIVEN
		DatabaseOperation operation = DatabaseOperation.NONE;
		DBUnit dbUnit = new DBUnit(dataSource);

		// WHEN
		DBUnit result = dbUnit.setSetUpOperation(operation);

		// THEN
		assertThat(result).isSameAs(dbUnit);
		assertThat(dbUnit.setUpOperation).isNotNull().isEqualTo(operation);
	}

	@Test
	public void setTearDownOperation_should_replace_tear_down_operation() {
		// GIVEN
		DatabaseOperation operation = DatabaseOperation.NONE;
		DBUnit dbUnit = new DBUnit(dataSource);

		// WHEN
		DBUnit result = dbUnit.setTearDownOperation(operation);

		// THEN
		assertThat(result).isSameAs(dbUnit);
		assertThat(dbUnit.tearDownOperation).isNotNull().isEqualTo(operation);
	}
}
