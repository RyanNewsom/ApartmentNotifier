# Apartment Notifier
Emails me whenever a new apartment is posted to my apartments website. Written in Kotlin w/ Gradle as a build system and Firestore as the database. Deployed to Google Cloud Functions as an Http function, and invoked every hour using a google cloud cron job set up in the google cloud console UI.

## To build:
`./gradlew shadowJar`

## To run unit tests:
`./gradlew test`

## To deploy:
`gcloud functions deploy notifier --entry-point Notifier --runtime java11 --trigger-http --allow-unauthenticated --memory 512MB`
