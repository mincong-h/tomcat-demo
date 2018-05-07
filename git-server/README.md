# Simple Git Server

The objective of this demo is to clone a Git repository via URL:

    http://localhost:8080/git/app.git

Please modify the `base-path` in `GitHttpServlet.java` before running
this demo.

## Preparation on Server Side

On the server side, all Git repositories should be bare. They are stored under
base path:

    /Users/mincong/Desktop/server

Initialize a bare Git repository:

    $ cd ~/Desktop/server
    $ mkdir app.git && cd app.git
    $ git init --bare

## Preparation on Client Side

On the client side, create a repository and add file-system path as remote `fs`.
Also, send some dummy data to server side.

    $ cd ~/Desktop/client
    $ mkdir app && cd app
    $ git init
    $ echo Hello > README.md
    $ git add README.md
    $ git commit -m 'Initial commit'
    $ git remote add fs /Users/mincong/Desktop/server/app.git
    $ git push -u fs master

## Run Demo

Run the application `git-server`

    $ mvn clean install
    $ mvn cargo:run

Clone the project:

    $ cd ~
    $ git clone http://localhost:8080/git/app.git

Then the repository `app` is cloned.
