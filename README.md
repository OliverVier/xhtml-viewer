# About the project

This project is about building a CLI tool for reading in xhtml files and
getting information about the page structure.

XHTML pages may contain compositions or inclusions of other pages. Having
lots of pages might hinder the ability to quickly understand the page structure. 

For example, if you want to access a parameter named navbar in the page.xhtml, which is set in a navigation.xhtml file,
which is included via the <ui:include> tag in the template.xhtml, which is used by the page.xhtml, things might start
to get complicated, when there are more things to consider.

|Template|Parameters|
|---|---|
|template.xhtml|locale|
|navigation.xhtml|navbar|
|page.xhtml|page|

In the event that page.xhtml uses the template.xhtml, which includes the navigation.xhtml, page.xhtml now has
access to the parameters page, navbar and locale.

# What is currently supported?

The tool currently supports viewing the pages separately in the CLI.
You can access the page's attributes and relations by using the appropriate parameters.
There is also a feature, where you are able to export the read in xhtml files as a diagramm.
Which pages you want to include is set by the user in form of a regex pattern.