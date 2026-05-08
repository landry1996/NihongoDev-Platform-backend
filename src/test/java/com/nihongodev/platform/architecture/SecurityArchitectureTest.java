package com.nihongodev.platform.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class SecurityArchitectureTest {

    private static JavaClasses classes;

    @BeforeAll
    static void setUp() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.nihongodev.platform");
    }

    @Test
    void controllers_should_not_directly_access_repositories() {
        noClasses()
                .that().resideInAPackage("..web.controller..")
                .should().dependOnClassesThat().resideInAPackage("..persistence.repository..")
                .because("Controllers must go through application layer (hexagonal architecture)")
                .check(classes);
    }

    @Test
    void domain_should_not_depend_on_infrastructure() {
        noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAPackage("..infrastructure..")
                .because("Domain layer must not depend on infrastructure (hexagonal architecture)")
                .check(classes);
    }

    @Test
    void security_classes_should_reside_in_security_package() {
        classes()
                .that().haveSimpleNameEndingWith("Filter")
                .and().resideInAPackage("..infrastructure..")
                .should().resideInAPackage("..infrastructure.security..")
                .because("Security filters belong in the security package")
                .check(classes);
    }

    @Test
    void exception_classes_should_extend_runtime_exception() {
        classes()
                .that().resideInAPackage("..domain.exception..")
                .should().beAssignableTo(RuntimeException.class)
                .because("Domain exceptions should be unchecked")
                .check(classes);
    }
}
