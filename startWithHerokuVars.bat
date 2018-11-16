@echo off
heroku config -s -a large-project >.env && heroku local