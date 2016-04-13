
# this is hacky but I just call the command line code using this script 
# on the file that we pulled from jmars called test2.png

cd /Users/ieee8023/Documents/workspace3/cratercnn-cli/
java -cp `sh getclasspath.sh lib`:classes:. SlidingWindow /Users/ieee8023/Downloads/jmars/test2.png