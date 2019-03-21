# Auction Sniper from Growing Object Oriented Software Guided by Tests, with progression
This repository follows the progression of the example Auction Sniper application from the book Growing Object Oriented Software Guided by Tests. It includes branches and tags for chapters and pages of the book.

It also includes a Dockerfile for building a test Openfire server, with the configurations required to run the end to end tests. Run it with:
```text
docker build -t openfire . && docker run -p 5222:5222 -p 9080:9080 openfire
```