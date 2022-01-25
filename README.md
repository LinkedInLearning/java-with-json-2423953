# Java with JSON
This is the repository for the LinkedIn Learning course Java with JSON. The full course is available from [LinkedIn Learning][lil-course-url].

![Java with JSON][lil-thumbnail-url] 

JSON is often used as the encoding for data sent and received by web services, but it can also be used to store data in files and much more. Understanding how JSON works is crucial to building feature-rich applications and experiences. In this course, instructor Jon-Luke West goes over the use of data serialization/deserialization, the way JSON acts as the format, the syntax of JSON, how to read and write JSON in an application, and additional ways you can control the way data is handled. Jon-Luke begins with JSON basics, such as data interchange formats, primitives, and reading and writing JSON. He walks you through a simple JSON implementation, then shows you how to work with arrays. Plus, Jon-Luke covers additional formatting and customization options, like writing, reading, and testing nested JSON, as well as selecting fields to expose, handling null values, and much more.

## Instructions
This repository has branches for each of the videos in the course. You can use the branch pop up menu in github to switch to a specific branch and take a look at the course at that stage, or you can add `/tree/BRANCH_NAME` to the URL to go to the branch you want to access.

## Branches
The branches are structured to correspond to the videos in the course. The naming convention is `CHAPTER#_MOVIE#`. As an example, the branch named `02_03` corresponds to the second chapter and the third video in that chapter. 
Some branches will have a beginning and an end state. These are marked with the letters `b` for "beginning" and `e` for "end". The `b` branch contains the code as it is at the beginning of the movie. The `e` branch contains the code as it is at the end of the movie. The `main` branch holds the final state of the code when in the course.

When switching from one exercise files branch to the next after making changes to the files, you may get a message like this:

    error: Your local changes to the following files would be overwritten by checkout:        [files]
    Please commit your changes or stash them before you switch branches.
    Aborting

To resolve this issue:
	
    Add changes to git using this command: git add .
	Commit changes using this command: git commit -m "some message"


### Instructor

Jon-Luke West 
                            
Product Engineer

                            

Check out my other courses on [LinkedIn Learning](https://www.linkedin.com/learning/instructors/jon-luke-west).

[lil-course-url]: https://www.linkedin.com/learning/java-with-json
[lil-thumbnail-url]: https://cdn.lynda.com/course/2423953/2423953-1643045935854-16x9.jpg
