package com.nihongodev.platform;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@DisplayName("Cultural Intelligence Module - Architecture Rules")
class CulturalArchitectureTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    static void setup() {
        importedClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.nihongodev.platform");
    }

    @Test
    @DisplayName("Domain model classes should NOT depend on Spring, JPA, or infrastructure packages")
    void domainModelShouldNotDependOnSpringJpaOrInfrastructure() {
        noClasses()
                .that().resideInAPackage("com.nihongodev.platform.domain.model..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.springframework..",
                        "jakarta.persistence..",
                        "javax.persistence..",
                        "com.nihongodev.platform.infrastructure.."
                )
                .because("Domain model must remain framework-agnostic and independent of infrastructure concerns")
                .check(importedClasses);
    }

    @Test
    @DisplayName("Application layer should NOT depend on infrastructure")
    void applicationLayerShouldNotDependOnInfrastructure() {
        noClasses()
                .that().resideInAPackage("com.nihongodev.platform.application..")
                .should().dependOnClassesThat().resideInAPackage("com.nihongodev.platform.infrastructure..")
                .because("Application layer must depend on port interfaces, not infrastructure implementations")
                .check(importedClasses);
    }

    @Test
    @DisplayName("Cultural service layer should not bypass ports to access persistence directly")
    void culturalServicesShouldNotAccessPersistenceDirectly() {
        noClasses()
                .that().resideInAPackage("com.nihongodev.platform.application.service.cultural..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "com.nihongodev.platform.infrastructure..",
                        "jakarta.persistence..",
                        "org.springframework.data.."
                )
                .because("Cultural intelligence services must use port interfaces, not infrastructure directly")
                .check(importedClasses);
    }
}
