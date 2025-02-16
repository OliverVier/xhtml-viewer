# Project idea

This project serves my need to read-in xhtml files 
and getting information about the file content structure.
XHTML pages may contain compositions (templates). When used in large
amounts, compositions might be hindering the ability to quickly understand 
the page structure. For example, some parameters can be set in a parent 
template, some others in several child templates, which a page then receives via the parent template.

For example we might have multiple templates with a single parameter named after the template.
|Type|Values|
|----|------|
|Templates|A.xhtml, B.xhtml, C.xhtml|
|Actual page|page.xhtml|

|Templates used by||
|-|-|
| A.xhtml <-- B.xhtml | B uses A |
| C.xhtml <-- B.xhtml | B uses C |
| B.xhtml <-- page.xhtml | page uses B` |

page.xhtml now has access to parameter A, B and C. If your structure is slightly more complex
and you have several parameters on different pages to keep track off, the complexity rises
fast.

I want to quickly have the information about which template is used by which page and which parameters are included in using the given template. This information is given in pure textform or as a diagram (currently supports plantuml output)

I might look into making a plugin for Eclipse.