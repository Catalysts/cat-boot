# cat-boot

CAT-Boot: A set of libraries on top of the famous and fabolous [Spring Boot](https://projects.spring.io/spring-boot/)

[![Build Status](https://travis-ci.org/Catalysts/cat-boot.svg?branch=master)](https://travis-ci.org/Catalysts/cat-boot)
[![Coverage Status](https://coveralls.io/repos/github/Catalysts/cat-boot/badge.svg?branch=master)](https://coveralls.io/github/Catalysts/cat-boot?branch=master)

## Java-Melody

See [`/cat-boot-javamelody`](/cat-boot-javamelody)

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