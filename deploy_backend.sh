# Deploy app

echo "Deploying app"
goapp deploy -application hackernews-1082 backend


# Update cron jobs..

echo "Updating cron jobs"
appcfg.py update_cron --application=hackernews-1082 backend
