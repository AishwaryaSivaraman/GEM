# GEM
GEM is an ecliplse plugin. 

Steps to Import:

1. Import the source code as eclipse plugin.
2. pass -pythonPath <path to your python installation location>
  e.g. /usr/local/bin/python
3. Run as ecliplse application.

Steps to Generate Refactorings:
1. Open any Java file in the eclipse IDE.
2. Text select the method name for which you want to generate extract method refactorings and press CMD/Win+6.
3. Now open the Results view to see the top-5 refactoring candidates. 
4. Click on each row in the results table to view the candidate in the Java file.

This is an Example:
![image](http://github.com/XuSihan/GEM/raw/master/images/Example.png)

