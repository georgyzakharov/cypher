# Cypher

A simple-to-use programming competition management system written in the Java
programming language. Designed for Linux-based systems and developed with a
target OS of Ubuntu 18.04 LTS.

Cypher permits three types of users: competitors, judges and audience members.
The system also incorporates a system administrator account. Competitors can
represent teams or individuals.

Currently, Cypher is comprised of a [frontend](cypher/cypher-frontend/src/main/java/edu/sunypoly/cypher/frontend) and a [backend](cypher/cypher-backend/src/main/java/edu/sunypoly/cypher/backend)
component.

## Frontend

The frontend, as its name suggests, handles code submission for competitors
and problem and grading module submissions for judges.

## Backend

The backend receives objects passed from the frontend which contain essential
elements utilized in the compilation and execution of competitor code, namely:
* Competitor source code
* Team identifier
* Problem number
* Programming language in which the source code is written

Each element of a submission object (excluding problem number) is encapsulated
within Java "String" objects. Team identifiers are alphanumeric.

## Docker

Cypher makes use of [Docker](https://github.com/docker) containers to effectively
sandbox the compilation and execution of competitor source code. In order to use
Cypher, you must have Docker installed on your machine. See the [Docker official
guide](https://docs.docker.com/v17.09/engine/installation/) for notes on how to
install Docker for your OS.

## Supported Languages

Cypher currently supports the compilation/interpretation of four programming
languages:

  1. [Java](https://docs.oracle.com/javase/7/docs/technotes/guides/language/)
  2. [C](https://en.wikibooks.org/wiki/C_Programming)
  3. [C++](https://isocpp.org/)
  4. [Python](https://www.python.org/)
  
 ### Memo from the Cypher DevTeam

Cypher is an open-source project to which any individual may contribute. The
ultimate goal of Cypher as envisioned by its original developers remains unchanged
despite years of revision and iterative production: to provide a simple, effective,
timely and modular programming competition management system. Most of all, the
purpose of Cypher is to have fun!

Future plans for the Cypher dev team include but are not limited to:
1. World domination
2. Genetically engineering a race of talking trees
3. Converting sunlight into pixie dust
4. Redistributing rock formations across continents (excluding Antarctica)
5. Establishing a boy band with a global following
6. Promoting and participating in the zombification of earthworms
7. Hunting hippocampi in the Mariana Trench
8. Codifying and standardizing the Jan-Jan:ta'He-lu language
9. Imposing economic sanctions on nations that manufacture and purchase shoelaces
10. Hosting an international Elvis Presley impersonation convention in the Bering Strait

Sincerely,
### The Cypher Team
