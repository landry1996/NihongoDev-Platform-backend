package com.nihongodev.platform.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@DisplayName("Hexagonal Architecture Rules")
class HexagonalArchitectureTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    static void setup() {
        importedClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.nihongodev.platform");
    }

    @Test
    @DisplayName("Domain should not depend on infrastructure")
    void domainShouldNotDependOnInfrastructure() {
        noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAPackage("..infrastructure..")
                .check(importedClasses);
    }

    @Test
    @DisplayName("Domain should not depend on application")
    void domainShouldNotDependOnApplication() {
        noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAPackage("..application..")
                .check(importedClasses);
    }

    @Test
    @DisplayName("Domain should not depend on Spring")
    void domainShouldNotDependOnSpring() {
        noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAPackage("org.springframework..")
                .check(importedClasses);
    }

    @Test
    @DisplayName("Application should not depend on infrastructure")
    void applicationShouldNotDependOnInfrastructure() {
        noClasses()
                .that().resideInAPackage("..application..")
                .should().dependOnClassesThat().resideInAPackage("..infrastructure..")
                .check(importedClasses);
    }

    @Test
    @DisplayName("Controllers should not access repositories directly")
    void controllersShouldNotAccessRepositories() {
        noClasses()
                .that().resideInAPackage("..web.controller..")
                .should().dependOnClassesThat().resideInAPackage("..persistence.repository..")
                .check(importedClasses);
    }

    @Test
    @DisplayName("Controllers should reside in infrastructure.web.controller package")
    void controllersShouldResideInCorrectPackage() {
        classes()
                .that().haveSimpleNameEndingWith("Controller")
                .should().resideInAPackage("..infrastructure.web.controller..")
                .check(importedClasses);
    }

    @Test
    @DisplayName("Use cases should reside in application.usecase package")
    void useCasesShouldResideInCorrectPackage() {
        classes()
                .that().haveSimpleNameEndingWith("UseCase")
                .and().areNotInterfaces()
                .should().resideInAPackage("..application.usecase..")
                .check(importedClasses);
    }
}
