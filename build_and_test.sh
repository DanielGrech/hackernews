#! /bin/bash

#  Android tests

cd android

./gradlew clean assembleDebug \
	:model:test :model:jacocoTestReport \
	:network:test :network:jacocoTestReport \
	:data:testDebugUnitTest :data:testDebugUnitTestCoverage \
	:app:testDevDebugUnitTest :app:testDevDebugUnitTestCoverage

cd ..

# Backend tests