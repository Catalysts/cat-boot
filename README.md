# cat-boot

CAT-Boot: A set of libraries on top of the famous and fabolous [Spring Boot](https://projects.spring.io/spring-boot/)

[![Build Status](https://travis-ci.org/Catalysts/cat-boot.svg?branch=master)](https://travis-ci.org/Catalysts/cat-boot)
[ ![Download](https://api.bintray.com/packages/catalysts/catalysts-jars/cat-boot/images/download.svg) ](https://bintray.com/catalysts/catalysts-jars/cat-boot/_latestVersion)

### Version Information

* 0.2.x is compatible with spring-boot 1.5.x
* 0.1.x is compatible with spring-boot 1.3.x

## Java-Melody

See [`/cat-boot-javamelody`](/cat-boot-javamelody)

## Profiling

See [`/cat-boot-profiling`](/cat-boot-profiling)

## I18n Handling

We have two modules here:

* Endpoint handling with Spring Boot (exposes [`MessageSource`](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/context/MessageSource.html) to the web) at [`/cat-boot-i18n`](/cat-boot-i18n)
* The Angular side for it to reuse message bundles from the server at [`/cat-boot-i18n-angular`](/cat-boot-i18n-angular) 

## Thymeleaf enhancement

See [`/cat-boot-thymeleaf`](/cat-boot-thymeleaf)

## PDF Reporting engine

See [`/cat-boot-report-pdf`](/cat-boot-report-pdf)

## Redis-Clustering

See [`/cat-boot-cluster`](/cat-boot-cluster)