## Useful commands
Keep in mind these are pretty basic, and many of these commands have other options or alternative ways to use them

#### To start using git in a directory on your computer
`git init`

This makes a hidden folder in that directory called .git that git uses to manage branches, file changes, etc

#### To manage repositorys (remotes) git can use:
`git remote add [name of remote] [repo url]`

`git remote rm [name of remote]`

For this project you can use:

`git remote add origin https://github.com/mrkjdy/large-project`

"origin" is a typical remote name

#### To download files from a repo:
`git pull [name of remote] [branch name]`

For this project you can use this if you have named your remote "origin":

`git pull origin master`

If you have existing files already in your git directory, git will attempt to auto-merge your files with the ones off of the repo. 
If the auto-merge fails git will tell you that you need to fix conficts in certain files. You can do this by opening the files that have conflicts and selecting which version you want to keep.

#### To upload your changes to a repo you need to do a few things:
1. Tell git what files you want to change in the repo:

	`git add [name of file1] [name of file2] ...`

	`git rm [name of file1] [name of file2] ...`

	You can also tell it to add or remove all files with a certain extension:

	`git add *.extension`

	`git rm *.extension`

	Or you can tell it to add or remove all files in the directory:

	`git add *`

	`git rm *`

	Keep in mind if you remove files, it doesn't remove them from your computer, git just removes them from your current commit.
	Also if you remove files from your computer it doesn't remove them from the repo, you have to tell git to remove them manually.

2. You need to tell git that you're ready to make changes to the repo:

	`git commit`

	Commits current changes and opens a simple text editor where you should write about the changes you are commiting to the repo.

	`git commit -m "commit message details"`

	Commits current changes, skips the text editor, and uses the quoted message as the commit message.
	
	This command won't do anything if you haven't made any changes.

3.	You need to push your commit to the repo:

	`git push [name of remote] [branch name]`

	For this project you can use this if you have named your remote "origin":

	`git push origin master`

	This command won't do anything if you haven't committed any changes

#### To save your credentials for the current working directory:
`git config credential.helper store`

then

`git pull ...`

#### To view heroku logs:
1. Tell heroku what app this directory is using:

`heroku git:remote -a large-project`

2. View the logs:
`heroku logs`	(Prints recent logs)

`heroku logs -t`	(Prints live logs)

#### To run the server locally:
(Remember you need heroku and npm installed to run the server locally)
(Also, for the start scipts to work you need to tell heroku what app this repo is using. See the section below)

`heroku local`

Note: the above command will use any vars you have set in the .env file, or none if the .env file does not exist

To run the server locally with the ClearDB database use one of these commands:

`./startWithHerokuVars.sh` for Linux/Mac

or

`startWithHerokuVars.bat` for Windows

These scripts will also store the ClearDB vars in the .env file.

If you wish to use your own vars then you should edit the .env file. This can be done to specify an alternative database.

#### To add our heroku remote to the current repo:
`heroku git:remote -a large-project`