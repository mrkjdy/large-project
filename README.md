# large-project
Step Counter created by Group 4 in COP4331


## Git commands
Use `git init` to start using git in a directory on your computer
	This makes a hidden folder in that directory called .git that git uses to manage branches, file changes, etc

Use `git remote add [name of remote] [repo url]` to give git a repository you want to use

For this project you can use:
`git remote add origin https://github.com/mrkjdy/large-project`
(origin is typically used as the name of most remotes)

Use `git pull [name of remote] [branch name]` to download the files off of a repo
For this project you can use this if you have named your remote "origin":
`git pull origin master`

If you have existing files already in your git directory, git will attempt to auto-merge your files with the ones off of the repo. 
If the auto-merge fails git will tell you that you need to fix conficts in certain files. You can do this by opening the files that have conflicts and selecting which version you want to keep.

To upload your changes to the repo you need to do a few things:
1. Tell git what files you want to change in the repo
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

2. You need to tell git that your ready to make changes to the repo:
`git commit`
`git commit -m "commit message details"`
No `-m` will open a simple text editor where you should write about the changes you are commiting to the repo.
`-m` will skip the text editor and use the following quoted message as the commit message.
This command won't do anything if you haven't made any changes.

3.	You need to push your commit to the repo:
`git push [name of remote] [branch name]`
For this project you can use this if you have named your remote "origin":
`git push origin master`
This command won't do anything if you haven't committed any changes
