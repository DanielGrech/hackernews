#! /bin/bash

#  Android tests

echo "Going to execute Android tests.."

cd android

./gradlew clean assembleDebug \
	:model:test :model:jacocoTestReport \
	:network:test :network:jacocoTestReport \
	:data:testDebugUnitTest :data:testDebugUnitTestCoverage \
	:app:testDevDebugUnitTest :app:testDevDebugUnitTestCoverage

androidTestsResult=$?

cd ..

if [ $androidTestsResult -ne 0 ]; then
    exit $androidTestsResult
fi

# Backend tests

echo "Going to execute Backend tests.."

cd backend

cd ..