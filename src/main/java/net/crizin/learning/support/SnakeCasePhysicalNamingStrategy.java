package net.crizin.learning.support;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

import java.util.Locale;

public class SnakeCasePhysicalNamingStrategy implements PhysicalNamingStrategy {
	@Override
	public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment jdbcEnvironment) {
		return name;
	}

	@Override
	public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment jdbcEnvironment) {
		return name;
	}

	@Override
	public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
		return jdbcEnvironment.getIdentifierHelper().toIdentifier(convert(name.getText()), name.isQuoted());
	}

	@Override
	public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment jdbcEnvironment) {
		return jdbcEnvironment.getIdentifierHelper().toIdentifier(convert(name.getText()), name.isQuoted());
	}

	@Override
	public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment jdbcEnvironment) {
		return jdbcEnvironment.getIdentifierHelper().toIdentifier(convert(name.getText()), name.isQuoted());
	}

	private String convert(String name) {
		boolean isFirst = true;
		StringBuilder sb = new StringBuilder();

		for (String part1 : StringUtils.splitByCharacterTypeCamelCase(name)) {
			for (String part2 : StringUtils.split(part1, '_')) {
				if (StringUtils.isNotBlank(part2)) {
					if (isFirst) {
						isFirst = false;
					} else {
						sb.append('_');
					}

					sb.append(part2.trim().toLowerCase(Locale.ROOT));
				}
			}
		}

		return sb.toString();
	}
}