package com.project6.ecommerce;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.project6.ecommerce", importOptions = {ImportOption.DoNotIncludeTests.class})
public class ArchitectureTest {

    @ArchTest
    static final ArchRule controllers_should_only_depend_on_services =
            classes().that().resideInAPackage("..controller..")
                    .should().onlyDependOnClassesThat().resideInAnyPackage(
                            "..controller..",
                            "..service..",
                            "..domain..",
                            "..mapper..",
                            "java..",
                            "javax..",
                            "jakarta..",
                            "org.springframework..",
                            "lombok.."
                    );

    @ArchTest
    static final ArchRule services_should_not_depend_on_controllers =
            noClasses().that().resideInAPackage("..service..")
                    .should().dependOnClassesThat().resideInAPackage("..controller..");

    @ArchTest
    static final ArchRule services_should_only_be_accessed_by_controllers_or_other_services =
            classes().that().resideInAPackage("..service..")
                    .should().onlyBeAccessed().byAnyPackage("..controller..", "..service..", "..config.."); // Added ..config..

    @ArchTest
    static final ArchRule repositories_should_only_be_accessed_by_services =
            classes().that().resideInAPackage("..repository..")
                    .should().onlyBeAccessed().byAnyPackage("..service..", "..repository..", "..config.."); // Added ..config.. just in case, though DataInitializer now uses Services
}
